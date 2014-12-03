package ec.gob.senescyt.firma.security;

import ec.gob.senescyt.firma.exceptions.AlmacenLlavesExcepcion;
import ec.gob.senescyt.firma.exceptions.FirmaDigitalExcepcion;
import ec.gob.senescyt.firma.security.certs.CertificadosRaizFactory;
import ec.gob.senescyt.firma.security.certs.FirmaDigitalProxyConfiguracion;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.io.IOException;
import java.nio.charset.Charset;
import java.security.InvalidAlgorithmParameterException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertPath;
import java.security.cert.CertPathParameters;
import java.security.cert.CertPathValidator;
import java.security.cert.CertPathValidatorException;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.PKIXParameters;
import java.security.cert.X509Certificate;
import java.util.ArrayList;

import static com.google.common.collect.Lists.newArrayList;
import static ec.gob.senescyt.firma.security.certs.TiposCertificadosRaiz.BCE_RAIZ;
import static ec.gob.senescyt.firma.security.certs.TiposCertificadosRaiz.BCE_SUBORDINADO;
import static org.apache.commons.lang.RandomStringUtils.randomAlphabetic;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyList;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.MockitoAnnotations.initMocks;
import static org.powermock.api.mockito.PowerMockito.mock;
import static org.powermock.api.mockito.PowerMockito.mockStatic;
import static org.powermock.api.mockito.PowerMockito.when;

@RunWith(PowerMockRunner.class)
@PrepareForTest({ CertPathValidator.class, CertificateFactory.class, FirmaDigitalProxy.class })
public class FirmaDigitalProxyTest {

    private static final String ERROR_DE_VALIDACIÓN_DEL_CERTIFICADO = "Error de validación del certificado";
    private CertPathValidator validador;
    private CertificateFactory fabricaCertificados;
    private FirmaDigital firmaDigital;
    private CertificadosRaizFactory certificadosRaizFactory;
    private X509Certificate certificadoRaiz;
    private X509Certificate certificadoSubordinado;
    private X509Certificate certificadoHijo;
    private String caminoArchivo;
    private CertPath certPath;
    private String cadenaAFirmar;
    private String contrasenia;
    private FirmaDigitalProxyConfiguracion configuracion;
    private final ArgumentCaptor<PKIXParameters> capturaParametros = ArgumentCaptor.forClass(PKIXParameters.class);
    @Mock
    private FirmaDigital firmaDigitalReal;
    @Mock
    private AlmacenLlavesProvider almacenLlaves;


    @Before
    public void setUp() throws NoSuchAlgorithmException, CertificateException, IOException, AlmacenLlavesExcepcion {
        initMocks(this);
        mockStatic(CertPathValidator.class);
        mockStatic(CertificateFactory.class);
        validador = mock(CertPathValidator.class);
        fabricaCertificados = mock(CertificateFactory.class);
        certificadosRaizFactory = mock(CertificadosRaizFactory.class);
        certificadoRaiz = mock(X509Certificate.class);
        certificadoSubordinado = mock(X509Certificate.class);
        certificadoHijo = mock(X509Certificate.class);
        configuracion = mock(FirmaDigitalProxyConfiguracion.class);
        certPath = mock(CertPath.class);
        cadenaAFirmar = randomAlphabetic(10);
        caminoArchivo = randomAlphabetic(10);
        contrasenia = randomAlphabetic(10);
        when(CertPathValidator.getInstance("PKIX")).thenReturn(validador);
        when(CertificateFactory.getInstance("X.509")).thenReturn(fabricaCertificados);
        when(certificadosRaizFactory.obtenerCertificadoRaiz(BCE_RAIZ)).thenReturn(certificadoRaiz);
        when(certificadosRaizFactory.obtenerCertificadoRaiz(BCE_SUBORDINADO)).thenReturn(certificadoSubordinado);
        when(almacenLlaves.obtenerCertificadoDeLaFirma(caminoArchivo, contrasenia)).thenReturn(certificadoHijo);
        firmaDigital = new FirmaDigitalProxy(firmaDigitalReal, certificadosRaizFactory, almacenLlaves, configuracion);
    }

    @Test
    public void debeValidarLaCadenaDeConfianzaDelCertificado() throws CertificateException, FirmaDigitalExcepcion, CertPathValidatorException, InvalidAlgorithmParameterException {
        when(fabricaCertificados.generateCertPath(anyList())).thenReturn(certPath);
        firmaDigital.firmar(cadenaAFirmar, caminoArchivo, contrasenia);
        Mockito.verify(validador, times(1)).validate(eq(certPath), any(CertPathParameters.class));
    }

    @Test
    public void debeGenerarElCertPathConElCertificadoSubordinadoYElHijoDeLaLista() throws CertificateException, FirmaDigitalExcepcion {
        ArrayList<Certificate> certificados = newArrayList(certificadoHijo, certificadoSubordinado);
        firmaDigital.firmar(cadenaAFirmar, caminoArchivo, contrasenia);
        Mockito.verify(fabricaCertificados, times(1)).generateCertPath(certificados);
    }

    @Test
    public void debeCrearLosParametrosDeValidacionConUnCertificadoRaizDeAnchor() throws FirmaDigitalExcepcion, CertPathValidatorException, InvalidAlgorithmParameterException {
        firmaDigital.firmar(cadenaAFirmar, caminoArchivo, contrasenia);
        Mockito.verify(validador, times(1)).validate(any(CertPath.class), capturaParametros.capture());
        assertThat(capturaParametros.getValue().getTrustAnchors().size(), is(1));
    }

    @Test
    public void debeCrearElAnchorPartiendoDelCertificadoRaiz() throws FirmaDigitalExcepcion, CertPathValidatorException, InvalidAlgorithmParameterException {
        firmaDigital.firmar(cadenaAFirmar, caminoArchivo, contrasenia);
        Mockito.verify(validador, times(1)).validate(any(CertPath.class), capturaParametros.capture());
        boolean contieneCertificadoRaiz = capturaParametros.getValue().getTrustAnchors().stream().anyMatch(a -> a.getTrustedCert() == certificadoRaiz);
        assertThat(contieneCertificadoRaiz, is(true));
    }

    @Test
    public void debeVerificarQueRevocationEsteActivado() throws FirmaDigitalExcepcion, CertPathValidatorException, InvalidAlgorithmParameterException {
        firmaDigital.firmar(cadenaAFirmar, caminoArchivo, contrasenia);
        Mockito.verify(validador, times(1)).validate(any(CertPath.class), capturaParametros.capture());
        assertThat(capturaParametros.getValue().isRevocationEnabled(), is(true));
    }

    @Test
    public void debeInicializarLosParametrosDeValidacionAntesDeValidarElCertificado() throws FirmaDigitalExcepcion, CertPathValidatorException, InvalidAlgorithmParameterException {
        firmaDigital.firmar(cadenaAFirmar, caminoArchivo, contrasenia);
        InOrder orderDeLLamada = inOrder(configuracion, validador);
        orderDeLLamada.verify(configuracion, times(1)).configurar();
        orderDeLLamada.verify(validador, times(1)).validate(any(CertPath.class), any(CertPathParameters.class));
    }

    @Test
    public void debeRetornarElResultadoDeLaFirmaSiLaValidacionEsExitosa() throws FirmaDigitalExcepcion {
        byte[] firmaEsperada = randomAlphabetic(10).getBytes(Charset.defaultCharset());
        when(firmaDigitalReal.firmar(cadenaAFirmar, caminoArchivo, contrasenia)).thenReturn(firmaEsperada);
        byte[] firma = firmaDigital.firmar(cadenaAFirmar, caminoArchivo, contrasenia);
        assertThat(firma, is(firmaEsperada));
    }

    @Test
    public void debeLevantarUnaExcepcionSiLaValidacionFalla() throws CertPathValidatorException, InvalidAlgorithmParameterException {
        when(validador.validate(any(), any())).thenThrow(new CertPathValidatorException());
        try {
            firmaDigital.firmar(cadenaAFirmar, caminoArchivo, contrasenia);
        }
        catch (FirmaDigitalExcepcion excepcion) {
            assertThat(excepcion.getMessage(), is(ERROR_DE_VALIDACIÓN_DEL_CERTIFICADO));
        }
    }

    @Test
    public void debeLlamarAlMetodoRealDeValidacionDeLaFirma() throws AlmacenLlavesExcepcion {
        firmaDigital.existeLlavePrivadaParaFirmar(caminoArchivo, contrasenia);
        verify(firmaDigitalReal, times(1)).existeLlavePrivadaParaFirmar(caminoArchivo, contrasenia);
    }
}