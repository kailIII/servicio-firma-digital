package ec.gob.senescyt.firma.security;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.io.IOException;
import java.nio.charset.Charset;
import java.security.InvalidKeyException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.Signature;
import java.security.SignatureException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;

import static org.apache.commons.lang.RandomStringUtils.randomAlphabetic;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;
import static org.powermock.api.mockito.PowerMockito.mockStatic;

@RunWith(PowerMockRunner.class)
@PrepareForTest({Signature.class, FirmaDigitalImpl.class})
public class FirmaDigitalImplTest {

    public static final String CONTRASENIA = randomAlphabetic(10);
    @Mock
    private AlmacenLlavesPkcs12Provider almacenLlaves;
    @Mock
    private PrivateKey llavePrivada;
    private Signature firma;
    private FirmaDigitalImpl firmaDigital;
    private String caminoArchivo;
    private String cadenaAFirmar;

    @Before
    public void setUp() throws Exception {
        initMocks(this);
        mockStatic(Signature.class);
        firma = PowerMockito.mock(Signature.class);
        PowerMockito.when(Signature.getInstance("SHA256withRSA")).thenReturn(firma);
        cadenaAFirmar = randomAlphabetic(500);
        firmaDigital = new FirmaDigitalImpl(almacenLlaves);
        caminoArchivo = randomAlphabetic(10);
        PowerMockito.when(almacenLlaves.obtenerLlavePrivadaParaFirmar(caminoArchivo, CONTRASENIA)).thenReturn(llavePrivada);
    }

    @Test
    public void debePasarLaLlavePrivadaAlProcesoDeFirma() throws CertificateException, UnrecoverableKeyException, NoSuchAlgorithmException, IOException, KeyStoreException, SignatureException, InvalidKeyException {
        firmaDigital.firmar(cadenaAFirmar, caminoArchivo, CONTRASENIA);
        verify(firma, times(1)).initSign(llavePrivada);
    }

    @Test
    public void debeRetornarLaFirmaAsociadaALaCadenaConLaLlavePrivada() throws SignatureException, CertificateException, UnrecoverableKeyException, NoSuchAlgorithmException, IOException, KeyStoreException, InvalidKeyException {
        byte[] firmaEsperada = new byte[10];
        PowerMockito.when(firma.sign()).thenReturn(firmaEsperada);
        byte[] firmaActual = firmaDigital.firmar(cadenaAFirmar, caminoArchivo, CONTRASENIA);
        assertThat(firmaActual, is(firmaEsperada));
    }

    @Test
    public void debeRealizarLaFirmaEnElOrdenApropiado() throws CertificateException, UnrecoverableKeyException, NoSuchAlgorithmException, IOException, KeyStoreException, SignatureException, InvalidKeyException {
        firmaDigital.firmar(cadenaAFirmar, caminoArchivo, CONTRASENIA);
        InOrder inOrder = inOrder(firma);
        inOrder.verify(firma, times(1)).initSign(llavePrivada);
        inOrder.verify(firma, times(1)).update(cadenaAFirmar.getBytes(Charset.forName("UTF-8")));
        inOrder.verify(firma, times(1)).sign();
    }

    @Test
    public void debeRetornarVerdaderoSiExisteUnaLlavePrivadaParaFirmar() throws CertificateException, NoSuchAlgorithmException, KeyStoreException, IOException {
        boolean existeLlave = firmaDigital.existeLlavePrivadaParaFirmar(caminoArchivo, CONTRASENIA);
        assertThat(existeLlave, is(true));
    }

    @Test
    public void debeRetornarFalsoCuandoNoExisteUnaLlavePrivada() throws CertificateException, NoSuchAlgorithmException, KeyStoreException, IOException, UnrecoverableKeyException {
        when(almacenLlaves.obtenerLlavePrivadaParaFirmar(caminoArchivo, CONTRASENIA)).thenReturn(null);
        boolean existeLlave = firmaDigital.existeLlavePrivadaParaFirmar(caminoArchivo, CONTRASENIA);
        assertThat(existeLlave, is(false));
    }

    @Test
    public void debeRetornarFalsoCuandoLaContraseniaNoEsValidaParaObtenerLaLLave() throws CertificateException, NoSuchAlgorithmException, KeyStoreException, IOException, UnrecoverableKeyException {
        doThrow(new UnrecoverableKeyException()).when(almacenLlaves).obtenerLlavePrivadaParaFirmar(caminoArchivo, CONTRASENIA);
        boolean existeLlave = firmaDigital.existeLlavePrivadaParaFirmar(caminoArchivo, CONTRASENIA);
        assertThat(existeLlave, is(false));
    }

    @Test
    public void debeRetornarFalsoCuandoElAlgoritmoParaRecuperarLaLlaveNoExiste() throws UnrecoverableKeyException, CertificateException, NoSuchAlgorithmException, KeyStoreException, IOException {
        doThrow(new NoSuchAlgorithmException()).when(almacenLlaves).obtenerLlavePrivadaParaFirmar(caminoArchivo, CONTRASENIA);
        boolean existeLlave = firmaDigital.existeLlavePrivadaParaFirmar(caminoArchivo, CONTRASENIA);
        assertThat(existeLlave, is(false));
    }

    @Test
    public void debeRetornarFalsoCuandoNoEsPosibleAccederAlAlmacenDeLlaves() throws UnrecoverableKeyException, CertificateException, NoSuchAlgorithmException, KeyStoreException, IOException {
        doThrow(new KeyStoreException()).when(almacenLlaves).obtenerLlavePrivadaParaFirmar(caminoArchivo, CONTRASENIA);
        boolean existeLlave = firmaDigital.existeLlavePrivadaParaFirmar(caminoArchivo, CONTRASENIA);
        assertThat(existeLlave, is(false));
    }

    @Test
    public void debeRetornarFalsoCuandoNoEsPosibleAbrirElAlmacenDeLlaves() throws UnrecoverableKeyException, CertificateException, NoSuchAlgorithmException, KeyStoreException, IOException {
        doThrow(new IOException()).when(almacenLlaves).obtenerLlavePrivadaParaFirmar(caminoArchivo, CONTRASENIA);
        boolean existeLlave = firmaDigital.existeLlavePrivadaParaFirmar(caminoArchivo, CONTRASENIA);
        assertThat(existeLlave, is(false));
    }
}