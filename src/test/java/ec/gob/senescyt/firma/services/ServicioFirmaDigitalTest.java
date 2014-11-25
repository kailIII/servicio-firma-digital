package ec.gob.senescyt.firma.services;

import ec.gob.senescyt.firma.core.ConfiguracionFirma;
import ec.gob.senescyt.firma.core.DocumentoFirmado;
import ec.gob.senescyt.firma.dao.ConfiguracionFirmaDAO;
import ec.gob.senescyt.firma.dao.DocumentoFirmadoDAO;
import ec.gob.senescyt.firma.security.FirmaDigital;
import ec.gob.senescyt.microservicios.commons.core.InformacionFirma;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;

import java.io.IOException;
import java.nio.charset.Charset;
import java.security.InvalidKeyException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.SignatureException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;

import static org.apache.commons.lang.RandomStringUtils.randomAlphabetic;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

public class ServicioFirmaDigitalTest {

    @Mock
    private FirmaDigital firmaDigital;
    @Mock
    private DocumentoFirmadoDAO documentoFirmadoDAO;
    @Mock
    private ConfiguracionFirmaDAO configuracionFirmaDAO;
    @Mock
    private DocumentoFirmado documentoFirmado;

    private ServicioFirmaDigital servicio;
    private InformacionFirma informacionFirma;
    private ConfiguracionFirma configuracionFirma;
    private byte[] firmaEsperada;

    @Before
    public void setUp() throws IOException, CertificateException, UnrecoverableKeyException, NoSuchAlgorithmException, KeyStoreException, SignatureException, InvalidKeyException {
        initMocks(this);
        informacionFirma = new InformacionFirma(randomAlphabetic(300), randomAlphabetic(10), randomAlphabetic(10));
        servicio = new ServicioFirmaDigital(firmaDigital, configuracionFirmaDAO, documentoFirmadoDAO);
        configuracionFirma = new ConfiguracionFirma(randomAlphabetic(10), informacionFirma.getNombreUsuario());
        firmaEsperada = randomAlphabetic(10).getBytes("UTF-8");
        when(configuracionFirmaDAO.obtenerPorUsuario(informacionFirma.getNombreUsuario())).thenReturn(configuracionFirma);
        when(firmaDigital.firmar(informacionFirma.getTextoAFirmar(),configuracionFirma.getCaminoArchivo(), informacionFirma.getContrasenia())).thenReturn(firmaEsperada);
    }

    @Test
    public void debeRetornarElDocumentoFirmadoGuardadoDadaLaInformacionDeFirma() throws CertificateException, UnrecoverableKeyException, NoSuchAlgorithmException, IOException, KeyStoreException, SignatureException, InvalidKeyException {
        when(documentoFirmadoDAO.guardar(any(DocumentoFirmado.class))).thenReturn(documentoFirmado);
        DocumentoFirmado documentoActual = servicio.firmar(informacionFirma);
        assertThat(documentoActual, is(documentoFirmado));
    }

    @Test
    public void debeContenerElResultadoDeLaFirmaDigitalAlGuardar() throws IOException, CertificateException, UnrecoverableKeyException, NoSuchAlgorithmException, KeyStoreException, SignatureException, InvalidKeyException {
        servicio.firmar(informacionFirma);
        ArgumentCaptor<DocumentoFirmado> capturador = ArgumentCaptor.forClass(DocumentoFirmado.class);
        verify(documentoFirmadoDAO, times(1)).guardar(capturador.capture());
        assertThat(capturador.getValue().getFirmaDigital(), is(new String(firmaEsperada, Charset.forName("UTF-8"))));
    }

    @Test
    public void debeContenerLaConfiguracionConLaQueSeRealizaLaFirmaAlGuardar() throws IOException, CertificateException, UnrecoverableKeyException, NoSuchAlgorithmException, KeyStoreException, SignatureException, InvalidKeyException {
        servicio.firmar(informacionFirma);
        ArgumentCaptor<DocumentoFirmado> capturador = ArgumentCaptor.forClass(DocumentoFirmado.class);
        verify(documentoFirmadoDAO, times(1)).guardar(capturador.capture());
        assertThat(capturador.getValue().getConfiguracionFirma(), is(configuracionFirma));
    }
}