package ec.gob.senescyt.firma.security;

import ec.gob.senescyt.firma.security.certs.CertificadoFactory;
import ec.gob.senescyt.firma.security.certs.CertificadosRaizFactory;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.io.IOException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.SignatureException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertPath;
import java.security.cert.CertPathParameters;
import java.security.cert.CertPathValidator;
import java.security.cert.CertPathValidatorException;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.ArrayList;

import static com.google.common.collect.Lists.newArrayList;
import static ec.gob.senescyt.firma.security.certs.TiposCertificadosRaiz.BCE_SUBORDINADO;
import static org.apache.commons.lang.RandomStringUtils.randomAlphabetic;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyList;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.times;
import static org.powermock.api.mockito.PowerMockito.mock;
import static org.powermock.api.mockito.PowerMockito.mockStatic;
import static org.powermock.api.mockito.PowerMockito.when;

@RunWith(PowerMockRunner.class)
@PrepareForTest({ CertPathValidator.class, CertificateFactory.class, FirmaDigitalProxy.class })
public class FirmaDigitalProxyTest {

    private CertPathValidator validador;
    private CertificateFactory fabricaCertificados;
    private FirmaDigital firmaDigital;
    private CertificadosRaizFactory certificadosRaizFactory;
    private CertificadoFactory certificadoFactory;
    private X509Certificate certificadoSubordinado;
    private X509Certificate certificadoHijo;

    @Before
    public void setUp() throws NoSuchAlgorithmException, CertificateException {
        mockStatic(CertPathValidator.class);
        mockStatic(CertificateFactory.class);
        validador = mock(CertPathValidator.class);
        fabricaCertificados = mock(CertificateFactory.class);
        certificadosRaizFactory = mock(CertificadosRaizFactory.class);
        certificadoFactory = mock(CertificadoFactory.class);
        certificadoSubordinado = mock(X509Certificate.class);
        certificadoHijo = mock(X509Certificate.class);
        when(CertPathValidator.getInstance("PKIX")).thenReturn(validador);
        when(CertificateFactory.getInstance("X.509")).thenReturn(fabricaCertificados);
        firmaDigital = new FirmaDigitalProxy(certificadosRaizFactory, certificadoFactory);
    }

    @Test
    public void debeValidarLaCadenaDeConfianzaDelCertificado() throws CertificateException, IOException, CertPathValidatorException, InvalidAlgorithmParameterException, UnrecoverableKeyException, NoSuchAlgorithmException, KeyStoreException, InvalidKeyException, SignatureException {
        CertPath certPath = mock(CertPath.class);
        when(fabricaCertificados.generateCertPath(anyList())).thenReturn(certPath);
        firmaDigital.firmar(randomAlphabetic(10), randomAlphabetic(10), randomAlphabetic(10));
        Mockito.verify(validador, times(1)).validate(eq(certPath), any(CertPathParameters.class));
    }

    @Test
    public void debeAgregarElCertificadoSubordinadoAlCertPath() throws CertificateException, IOException, UnrecoverableKeyException, NoSuchAlgorithmException, KeyStoreException, InvalidKeyException, SignatureException, CertPathValidatorException, InvalidAlgorithmParameterException {
        String caminoArchivo = randomAlphabetic(10);
        when(certificadosRaizFactory.obtenerCertificadoRaiz(BCE_SUBORDINADO)).thenReturn(certificadoSubordinado);
        when(certificadoFactory.obtenerCertificado(caminoArchivo)).thenReturn(certificadoHijo);
        CertPath certPath = mock(CertPath.class);
        ArrayList<Certificate> certificados = newArrayList(certificadoHijo, certificadoSubordinado);
        when(fabricaCertificados.generateCertPath(certificados)).thenReturn(certPath);
        firmaDigital.firmar(randomAlphabetic(10), caminoArchivo, randomAlphabetic(10));
        Mockito.verify(certificadosRaizFactory, times(1)).obtenerCertificadoRaiz(BCE_SUBORDINADO);
        Mockito.verify(certificadoFactory, times(1)).obtenerCertificado(caminoArchivo);
        Mockito.verify(fabricaCertificados, times(1)).generateCertPath(certificados);
    }
}