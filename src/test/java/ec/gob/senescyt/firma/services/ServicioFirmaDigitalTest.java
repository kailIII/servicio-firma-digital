package ec.gob.senescyt.firma.services;

import ec.gob.senescyt.firma.core.ConfiguracionFirma;
import ec.gob.senescyt.firma.core.DocumentoFirmado;
import ec.gob.senescyt.firma.dao.ConfiguracionFirmaDAO;
import ec.gob.senescyt.firma.dao.DocumentoFirmadoDAO;
import ec.gob.senescyt.firma.exceptions.AlmacenLlavesExcepcion;
import ec.gob.senescyt.firma.exceptions.FirmaDigitalExcepcion;
import ec.gob.senescyt.firma.security.FirmaDigitalImpl;
import ec.gob.senescyt.microservicios.commons.core.InformacionFirma;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.cert.CertificateException;
import java.util.Base64;
import java.util.Optional;

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
    private FirmaDigitalImpl firmaDigital;
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
    private String nombreUsuario;
    private String contrasenia;

    @Before
    public void setUp() throws UnsupportedEncodingException, FirmaDigitalExcepcion {
        initMocks(this);
        String textoAFirmar = randomAlphabetic(300);
        nombreUsuario = randomAlphabetic(10);
        contrasenia = randomAlphabetic(10);
        informacionFirma = new InformacionFirma(textoAFirmar, nombreUsuario, contrasenia);
        servicio = new ServicioFirmaDigital(firmaDigital, configuracionFirmaDAO, documentoFirmadoDAO);
        configuracionFirma = new ConfiguracionFirma(randomAlphabetic(10), informacionFirma.getNombreUsuario());
        firmaEsperada = randomAlphabetic(10).getBytes("UTF-8");
        when(configuracionFirmaDAO.obtenerPorUsuario(informacionFirma.getNombreUsuario())).thenReturn(Optional.of(configuracionFirma));
        when(firmaDigital.firmar(informacionFirma.getTextoAFirmar(),configuracionFirma.getCaminoArchivo(), informacionFirma.getContrasenia())).thenReturn(firmaEsperada);
    }

    @Test
    public void debeRetornarElDocumentoFirmadoGuardadoDadaLaInformacionDeFirma() throws FirmaDigitalExcepcion {
        when(documentoFirmadoDAO.guardar(any(DocumentoFirmado.class))).thenReturn(documentoFirmado);
        DocumentoFirmado documentoActual = servicio.firmar(informacionFirma);
        assertThat(documentoActual, is(documentoFirmado));
    }

    @Test
    public void debeContenerElResultadoDeLaFirmaDigitalAlGuardar() throws FirmaDigitalExcepcion {
        servicio.firmar(informacionFirma);
        ArgumentCaptor<DocumentoFirmado> capturador = ArgumentCaptor.forClass(DocumentoFirmado.class);
        verify(documentoFirmadoDAO, times(1)).guardar(capturador.capture());
        assertThat(capturador.getValue().getFirmaDigital(), is(Base64.getEncoder().encodeToString(firmaEsperada)));
    }

    @Test
    public void debeContenerLaConfiguracionConLaQueSeRealizaLaFirmaAlGuardar() throws FirmaDigitalExcepcion {
        servicio.firmar(informacionFirma);
        ArgumentCaptor<DocumentoFirmado> capturador = ArgumentCaptor.forClass(DocumentoFirmado.class);
        verify(documentoFirmadoDAO, times(1)).guardar(capturador.capture());
        assertThat(capturador.getValue().getConfiguracionFirma(), is(configuracionFirma));
    }

    @Test
    public void debeRetornarVerdaderoSiLasCredencialesNoSonValidasParaFirmar() throws AlmacenLlavesExcepcion {
        when(firmaDigital.existeLlavePrivadaParaFirmar(configuracionFirma.getCaminoArchivo(), contrasenia)).thenReturn(true);
        boolean sonValidas = servicio.validarCredencialesFirma(nombreUsuario, contrasenia);
        assertThat(sonValidas, is(true));
    }

    @Test
    public void debeRetornarFalsoSiLasCredencialesNoSonValidasParaFirmar() throws CertificateException, IOException, AlmacenLlavesExcepcion {
        when(firmaDigital.existeLlavePrivadaParaFirmar(configuracionFirma.getCaminoArchivo(), contrasenia)).thenReturn(false);
        boolean sonValidas = servicio.validarCredencialesFirma(nombreUsuario, contrasenia);
        assertThat(sonValidas, is(false));
    }

    @Test
    public void debeRetornarFalsoSiNoExisteConfiguracionParaElUsuario() throws CertificateException, IOException, AlmacenLlavesExcepcion {
        when(configuracionFirmaDAO.obtenerPorUsuario(informacionFirma.getNombreUsuario())).thenReturn(Optional.empty());
        boolean sonValidas = servicio.validarCredencialesFirma(nombreUsuario, contrasenia);
        assertThat(sonValidas, is(false));
    }
}