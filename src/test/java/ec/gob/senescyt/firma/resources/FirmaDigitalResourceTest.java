package ec.gob.senescyt.firma.resources;

import ec.gob.senescyt.firma.core.DocumentoFirmado;
import ec.gob.senescyt.firma.exceptions.AlmacenLlavesExcepcion;
import ec.gob.senescyt.firma.exceptions.FirmaDigitalExcepcion;
import ec.gob.senescyt.firma.services.ServicioFirmaDigital;
import ec.gob.senescyt.sniese.commons.core.InformacionFirma;
import ec.gob.senescyt.sniese.commons.security.PrincipalProvider;
import ec.gob.senescyt.sniese.commons.security.Usuario;
import ec.gob.senescyt.sniese.commons.tests.builders.UsuarioAutenticadoBuilder;
import org.eclipse.jetty.http.HttpStatus;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;

import javax.ws.rs.core.Response;

import static org.apache.commons.lang.RandomStringUtils.randomAlphabetic;
import static org.eclipse.jetty.http.HttpStatus.CREATED_201;
import static org.eclipse.jetty.http.HttpStatus.NOT_FOUND_404;
import static org.eclipse.jetty.http.HttpStatus.OK_200;
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
    private String contrasenia;

    @Before
    public void setUp() throws FirmaDigitalExcepcion {
        initMocks(this);
        recurso = new FirmaDigitalResource(servicioFirmaDigital, principalProvider);
        usuario = UsuarioAutenticadoBuilder.nuevo().generar();
        contrasenia = randomAlphabetic(10);
        informacionFirma = new InformacionFirma(randomAlphabetic(300), usuario.getNombreUsuario(), contrasenia);
        when(servicioFirmaDigital.firmar(informacionFirma)).thenReturn(documentoFirmado);
        when(principalProvider.obtenerUsuario()).thenReturn(usuario);
    }

    @Test
    public void debeRetornar201CreadorCuandoRecibeUnaSolicitudDeFirma() throws FirmaDigitalExcepcion {
        Response respuesta = recurso.crearFirmaDigital(informacionFirma);
        assertThat(respuesta.getStatus(), is(CREATED_201));
    }

    @Test
    public void debeRetornarDocumentoFirmadoCuandoRecibeUnaSolicitudDeFirma() throws FirmaDigitalExcepcion {
        Response respuesta = recurso.crearFirmaDigital(informacionFirma);
        assertThat(respuesta.getEntity(), instanceOf(DocumentoFirmado.class));
    }

    @Test
    public void debeRetornarLaInstanciaDelDocumentoFirmado() throws FirmaDigitalExcepcion {
        Response respuesta = recurso.crearFirmaDigital(informacionFirma);
        assertThat(respuesta.getEntity(), is(documentoFirmado));
    }

    @Test
    public void debeAsignarElNombreDeUsuarioALaInformacionDeFirmaAntesDeEnviarAFirmar() throws FirmaDigitalExcepcion {
        recurso.crearFirmaDigital(informacionFirma);
        ArgumentCaptor<InformacionFirma> capturador = ArgumentCaptor.forClass(InformacionFirma.class);
        verify(servicioFirmaDigital, times(1)).firmar(capturador.capture());
        assertThat(capturador.getValue().getNombreUsuario(), is(usuario.getNombreUsuario()));
    }

    @Test
    public void debeRetornar400ProhibidoSiElNombreDeUsuarioNoCoincideConElDeLaInformacion() throws FirmaDigitalExcepcion {
        informacionFirma = new InformacionFirma(randomAlphabetic(10), randomAlphabetic(10), randomAlphabetic(10));
        Response respuesta = recurso.crearFirmaDigital(informacionFirma);
        assertThat(respuesta.getStatus(), is(HttpStatus.BAD_REQUEST_400));
    }

    @Test
    public void debeRetornar200OkSiLasCredencialesSonValidas() throws AlmacenLlavesExcepcion {
        when(servicioFirmaDigital.validarCredencialesFirma(usuario.getNombreUsuario(), contrasenia)).thenReturn(true);
        Response respuesta = recurso.validarCredenciales(contrasenia);
        assertThat(respuesta.getStatus(), is(OK_200));
    }

    @Test
    public void debeRetornar404NotFoundCuandoLasCredencialesSonInvalidas() throws AlmacenLlavesExcepcion {
        when(servicioFirmaDigital.validarCredencialesFirma(usuario.getNombreUsuario(), contrasenia)).thenReturn(false);
        Response respuesta = recurso.validarCredenciales(contrasenia);
        assertThat(respuesta.getStatus(), is(NOT_FOUND_404));
    }
}