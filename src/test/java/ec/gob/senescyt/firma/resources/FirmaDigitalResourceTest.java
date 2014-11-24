package ec.gob.senescyt.firma.resources;

import ec.gob.senescyt.firma.core.DocumentoFirmado;
import ec.gob.senescyt.firma.services.ServicioFirmaDigital;
import ec.gob.senescyt.microservicios.commons.core.InformacionFirma;
import ec.gob.senescyt.microservicios.commons.filters.RecursoSeguro;
import ec.gob.senescyt.microservicios.commons.security.PrincipalProvider;
import ec.gob.senescyt.microservicios.commons.security.Usuario;
import ec.gob.senescyt.microservicios.commons.security.UsuarioAutenticado;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;

import javax.ws.rs.core.Response;

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
    public void setUp() {
        initMocks(this);
        recurso = new FirmaDigitalResource(servicioFirmaDigital, principalProvider);
        informacionFirma = new InformacionFirma(randomAlphabetic(300), randomAlphabetic(10), randomAlphabetic(10));
        usuario = new UsuarioAutenticado(randomAlphabetic(10), randomAlphabetic(10));
        when(servicioFirmaDigital.firmar(informacionFirma)).thenReturn(documentoFirmado);
        when(principalProvider.obtenerUsuario()).thenReturn(usuario);
    }

    @Test
    public void debeRetornar201CreadorCuandoRecibeUnaSolicitudDeFirma() {
        Response respuesta = recurso.crearFirmaDigital(informacionFirma);
        assertThat(respuesta.getStatus(), is(CREATED_201));
    }

    @Test
    public void debeRetornarDocumentoFirmadoCuandoRecibeUnaSolicitudDeFirma() {
        Response respuesta = recurso.crearFirmaDigital(informacionFirma);
        assertThat(respuesta.getEntity(), instanceOf(DocumentoFirmado.class));
    }

    @Test
    public void debeRetornarLaInstanciaDelDocumentoFirmado() {
        Response respuesta = recurso.crearFirmaDigital(informacionFirma);
        assertThat(respuesta.getEntity(), is(documentoFirmado));
    }

    @Test
    public void debeAsignarElNombreDeUsuarioALaInformacionDeFirmaAntesDeEnviarAFirmar() {
        recurso.crearFirmaDigital(informacionFirma);
        ArgumentCaptor<InformacionFirma> capturador = ArgumentCaptor.forClass(InformacionFirma.class);
        verify(servicioFirmaDigital, times(1)).firmar(capturador.capture());
        assertThat(capturador.getValue().getNombreUsuario(), is(usuario.getNombreUsuario()));
    }

    @Test
    public void debeEstarProtegidoElAccesoALosRecursos() {
        boolean esSeguro = recurso.getClass().isAnnotationPresent(RecursoSeguro.class);
        assertThat(esSeguro, is(true));
    }
}