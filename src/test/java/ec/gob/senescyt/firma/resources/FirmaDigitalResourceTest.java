package ec.gob.senescyt.firma.resources;

import ec.gob.senescyt.firma.core.DocumentoFirmado;
import ec.gob.senescyt.firma.services.ServicioFirmaDigital;
import ec.gob.senescyt.microservicios.commons.core.InformacionFirma;
import ec.gob.senescyt.microservicios.commons.filters.RecursoSeguro;
import ec.gob.senescyt.microservicios.commons.security.PrincipalProvider;
import ec.gob.senescyt.microservicios.commons.security.Usuario;
import ec.gob.senescyt.microservicios.commons.security.UsuarioAutenticado;
import org.eclipse.jetty.http.HttpStatus;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;

import javax.ws.rs.core.Response;

import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.SignatureException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;

import static org.apache.commons.lang.RandomStringUtils.randomAlphabetic;
import static org.eclipse.jetty.http.HttpStatus.CREATED_201;
import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

public class FirmaDigitalResourceTest {

    @Mock
    private DocumentoFirmado documentoFirmado;
    @Mock
    private PrincipalProvider principalProvider;
    @Mock
    private ServicioFirmaDigital servicioFirmaDigital;
    private Usuario usuario;
    private InformacionFirma informacionFirma;
    private FirmaDigitalResource recurso;

    @Before
    public void setUp() throws CertificateException, UnrecoverableKeyException, NoSuchAlgorithmException, IOException, KeyStoreException, SignatureException, InvalidKeyException {
        initMocks(this);
        recurso = new FirmaDigitalResource(servicioFirmaDigital, principalProvider);
        usuario = new UsuarioAutenticado(randomAlphabetic(10), randomAlphabetic(10));
        informacionFirma = new InformacionFirma(randomAlphabetic(300), usuario.getNombreUsuario(), randomAlphabetic(10));
        when(servicioFirmaDigital.firmar(informacionFirma)).thenReturn(documentoFirmado);
        when(principalProvider.obtenerUsuario()).thenReturn(usuario);
    }

    @Test
    public void debeRetornar201CreadorCuandoRecibeUnaSolicitudDeFirma() throws CertificateException, UnrecoverableKeyException, NoSuchAlgorithmException, IOException, KeyStoreException, SignatureException, InvalidKeyException {
        Response respuesta = recurso.crearFirmaDigital(informacionFirma);
        assertThat(respuesta.getStatus(), is(CREATED_201));
    }

    @Test
    public void debeRetornarDocumentoFirmadoCuandoRecibeUnaSolicitudDeFirma() throws CertificateException, UnrecoverableKeyException, NoSuchAlgorithmException, IOException, KeyStoreException, SignatureException, InvalidKeyException {
        Response respuesta = recurso.crearFirmaDigital(informacionFirma);
        assertThat(respuesta.getEntity(), instanceOf(DocumentoFirmado.class));
    }

    @Test
    public void debeRetornarLaInstanciaDelDocumentoFirmado() throws CertificateException, UnrecoverableKeyException, NoSuchAlgorithmException, IOException, KeyStoreException, SignatureException, InvalidKeyException {
        Response respuesta = recurso.crearFirmaDigital(informacionFirma);
        assertThat(respuesta.getEntity(), is(documentoFirmado));
    }

    @Test
    public void debeAsignarElNombreDeUsuarioALaInformacionDeFirmaAntesDeEnviarAFirmar() throws CertificateException, UnrecoverableKeyException, NoSuchAlgorithmException, IOException, KeyStoreException, SignatureException, InvalidKeyException {
        recurso.crearFirmaDigital(informacionFirma);
        ArgumentCaptor<InformacionFirma> capturador = ArgumentCaptor.forClass(InformacionFirma.class);
        verify(servicioFirmaDigital, times(1)).firmar(capturador.capture());
        assertThat(capturador.getValue().getNombreUsuario(), is(usuario.getNombreUsuario()));
    }

    @Test
    public void debeRetornar400ProhibidoSiElNombreDeUsuarioNoCoincideConElDeLaInformacion() throws CertificateException, UnrecoverableKeyException, NoSuchAlgorithmException, IOException, KeyStoreException, SignatureException, InvalidKeyException {
        informacionFirma.setNombreUsuario(randomAlphabetic(10));
        Response respuesta = recurso.crearFirmaDigital(informacionFirma);
        assertThat(respuesta.getStatus(), is(HttpStatus.BAD_REQUEST_400));
    }

    @Test
    public void debeEstarProtegidoElAccesoALosRecursos() {
        boolean esSeguro = recurso.getClass().isAnnotationPresent(RecursoSeguro.class);
        assertThat(esSeguro, is(true));
    }
}