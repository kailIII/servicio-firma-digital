package ec.gob.senescyt.firma.resources;

import com.sun.jersey.api.client.ClientResponse;
import ec.gob.senescyt.firma.FirmaDigitalBaseIntTest;
import ec.gob.senescyt.firma.core.ConfiguracionFirma;
import ec.gob.senescyt.firma.dao.ConfiguracionFirmaDAO;
import ec.gob.senescyt.firma.exceptions.mappers.Errores;
import ec.gob.senescyt.sniese.commons.core.InformacionFirma;
import ec.gob.senescyt.sniese.commons.security.UsuarioAutenticado;
import org.junit.Before;
import org.junit.Ignore;
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

public class FirmaDigitalResourceIntTest extends FirmaDigitalBaseIntTest {

    private static final String CONTRASENIA = "Password#1";
    private static final String CONTRASENIA_CERTIFICADO_INVALIDO = "Password#2";
    private static final String CERTIFICADO_VALIDO = "test.p12";
    private static final String CERTIFICADO_INVALIDO = "certificadoInvalido.p12";
    private static final String RECURSO_FIRMADO_DIGITAL = "firmaDigital";
    private static final String RECURSO_VALIDACION_CONFIGURACION_FIRMA = "firmaDigital/credenciales/validar";
    private String nombreUsuario;

    @Override
    @Before
    public void setUp() throws Exception {
        super.setUp();
        nombreUsuario = randomAlphabetic(10);
        configurarUsuarioConPermisos(new UsuarioAutenticado(nombreUsuario, randomAlphabetic(5)));
    }

    @Test
    public void debeRetornar201AlSolicitarLaFirmaDigital() {
        configurarFirmaConUsuarioYArchivo(nombreUsuario, CERTIFICADO_VALIDO);
        InformacionFirma informacionFirma = new InformacionFirma(randomAlphabetic(600), nombreUsuario, CONTRASENIA);
        ClientResponse respuesta = hacerPost(RECURSO_FIRMADO_DIGITAL, informacionFirma);
        assertThat(respuesta.getStatus(), is(CREATED_201));
    }

    @Test
    public void debeRetornar400CuandoNoCoincideElNombreDeUsuario() {
        configurarFirmaConUsuarioYArchivo(nombreUsuario, CERTIFICADO_VALIDO);
        InformacionFirma informacionFirma = new InformacionFirma(randomAlphabetic(600), randomAlphabetic(10), CONTRASENIA);
        ClientResponse respuesta = hacerPost(RECURSO_FIRMADO_DIGITAL, informacionFirma);
        assertThat(respuesta.getStatus(), is(BAD_REQUEST_400));
    }

    @Test
    public void debeRetornar500ConMensajeDeErrorAdecuadoCuandoLaFirmaFalla() {
        configurarFirmaConUsuarioYArchivo(nombreUsuario, randomAlphabetic(10));
        InformacionFirma informacionFirma = new InformacionFirma(randomAlphabetic(600), nombreUsuario, CONTRASENIA);
        ClientResponse respuesta = hacerPost(RECURSO_FIRMADO_DIGITAL, informacionFirma);
        assertThat(respuesta.getStatus(), is(INTERNAL_SERVER_ERROR_500));
        assertThat(respuesta.getEntity(Errores.class).getErrors(), is(notNullValue()));
    }

    @Test
    @Ignore
    public void debeRetornar500ConMensajeDeErrorAdecuadoAlFirmarSiElCertificadoNoEsValido() {
        configurarFirmaConUsuarioYArchivo(nombreUsuario, CERTIFICADO_INVALIDO);
        InformacionFirma informacionFirma = new InformacionFirma(randomAlphabetic(600), nombreUsuario, CONTRASENIA_CERTIFICADO_INVALIDO);
        ClientResponse respuesta = hacerPost(RECURSO_FIRMADO_DIGITAL, informacionFirma);
        assertThat(respuesta.getStatus(), is(INTERNAL_SERVER_ERROR_500));
        assertThat(respuesta.getEntity(Errores.class).getErrors(), is(notNullValue()));
    }

    @Test
    public void debeRetornar200OkCuandoLasCredencialesSonValidas() {
        configurarFirmaConUsuarioYArchivo(nombreUsuario, CERTIFICADO_VALIDO);
        ClientResponse respuesta = hacerPost(RECURSO_VALIDACION_CONFIGURACION_FIRMA, CONTRASENIA);
        assertThat(respuesta.getStatus(), is(OK_200));
    }

    @Test
    public void debeRetornar404NotFoundCuandoLasCredencialesSonInvalidas() {
        configurarFirmaConUsuarioYArchivo(nombreUsuario, CERTIFICADO_VALIDO);
        ClientResponse respuesta = hacerPost(RECURSO_VALIDACION_CONFIGURACION_FIRMA, randomAlphabetic(12));
        assertThat(respuesta.getStatus(), is(NOT_FOUND_404));
    }

    private void configurarFirmaConUsuarioYArchivo(String nombreUsuario, String archivo) {
        ConfiguracionFirmaDAO configuracionFirmaDAO = new ConfiguracionFirmaDAO(sessionFactory, defaultSchema);
        ClassLoader systemClassLoader = ClassLoader.getSystemClassLoader();
        URL resource = systemClassLoader.getResource(archivo);
        String caminoArchivo = resource != null ? resource.getPath(): archivo;
        ConfiguracionFirma configuracionFirma = new ConfiguracionFirma(nombreUsuario, caminoArchivo);
        configuracionFirmaDAO.guardar(configuracionFirma);
    }
}