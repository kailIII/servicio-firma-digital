package ec.gob.senescyt.firma.resources;

import com.sun.jersey.api.client.ClientResponse;
import ec.gob.senescyt.ServicioApplication;
import ec.gob.senescyt.firma.core.ConfiguracionFirma;
import ec.gob.senescyt.firma.dao.ConfiguracionFirmaDAO;
import ec.gob.senescyt.firma.exceptions.mappers.Errores;
import ec.gob.senescyt.microservicios.commons.core.InformacionFirma;
import ec.gob.senescyt.microservicios.commons.security.UsuarioAutenticado;
import ec.gob.senescyt.microservicios.tests.RecursoSeguroIntegracionTest;
import org.junit.Before;
import org.junit.Test;

import java.net.URL;

import static org.apache.commons.lang.RandomStringUtils.randomAlphabetic;
import static org.eclipse.jetty.http.HttpStatus.BAD_REQUEST_400;
import static org.eclipse.jetty.http.HttpStatus.CREATED_201;
import static org.eclipse.jetty.http.HttpStatus.INTERNAL_SERVER_ERROR_500;
import static org.eclipse.jetty.http.HttpStatus.NOT_FOUND_404;
import static org.eclipse.jetty.http.HttpStatus.OK_200;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.Mockito.when;

public class FirmaDigitalResourceIntTest extends RecursoSeguroIntegracionTest {

    private static final String CONTRASENIA = "Password#1";
    private String nombreUsuario;
    private ConfiguracionFirma configuracionFirma;
    private ConfiguracionFirmaDAO configuracionFirmaDAO;

    public FirmaDigitalResourceIntTest() {
        super(ServicioApplication.class);
    }

    @Override
    @Before
    public void setUp() {
        super.setUp();
        ClassLoader systemClassLoader = ClassLoader.getSystemClassLoader();
        URL resource = systemClassLoader.getResource("test.p12");
        String caminoArchivo = resource.getPath();
        configuracionFirmaDAO = new ConfiguracionFirmaDAO(sessionFactory, defaultSchema);
        nombreUsuario = randomAlphabetic(10);
        UsuarioAutenticado usuario = new UsuarioAutenticado(nombreUsuario, randomAlphabetic(10));
        when(principalProvider.obtenerUsuario()).thenReturn(usuario);
        configuracionFirma = new ConfiguracionFirma(nombreUsuario, caminoArchivo);
        configuracionFirmaDAO.guardar(configuracionFirma);
    }

    @Test
    public void debeRetornar201AlSolicitarLaFirmaDigital() {
        InformacionFirma informacionFirma = new InformacionFirma(randomAlphabetic(600), nombreUsuario, CONTRASENIA);
        ClientResponse respuesta = hacerPost("firmaDigital", informacionFirma);
        assertThat(respuesta.getStatus(), is(CREATED_201));
    }

    @Test
    public void debeRetornar400CuandoNoCoincideElNombreDeUsuario() {
        InformacionFirma informacionFirma = new InformacionFirma(randomAlphabetic(600), randomAlphabetic(10), CONTRASENIA);
        ClientResponse respuesta = hacerPost("firmaDigital", informacionFirma);
        assertThat(respuesta.getStatus(), is(BAD_REQUEST_400));
    }

    @Test
    public void debeRetornar500ConMensajeDeErrorAdecuadoCuandoLaFirmaFalla() {
        configuracionFirmaDAO.eliminar(configuracionFirma.getId());
        configuracionFirma = new ConfiguracionFirma(nombreUsuario, randomAlphabetic(50));
        configuracionFirmaDAO.guardar(configuracionFirma);
        session.flush();
        InformacionFirma informacionFirma = new InformacionFirma(randomAlphabetic(600), nombreUsuario, CONTRASENIA);
        ClientResponse respuesta = hacerPost("firmaDigital", informacionFirma);
        assertThat(respuesta.getStatus(), is(INTERNAL_SERVER_ERROR_500));
        assertThat(respuesta.getEntity(Errores.class).getErrores(), is(notNullValue()));
    }

    @Test
    public void debeRetornar200OkCuandoLasCredencialesSonValidas() {
        ClientResponse respuesta = hacerPost("firmaDigital/credenciales/validar", CONTRASENIA);
        assertThat(respuesta.getStatus(), is(OK_200));
    }

    @Test
    public void debeRetornar404NotFoundCuandoLasCredencialesSonInvalidas() {
        ClientResponse respuesta = hacerPost("firmaDigital/credenciales/validar", randomAlphabetic(12));
        assertThat(respuesta.getStatus(), is(NOT_FOUND_404));
    }
}