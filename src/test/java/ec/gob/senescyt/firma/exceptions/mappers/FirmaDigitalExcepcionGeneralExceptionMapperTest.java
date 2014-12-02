package ec.gob.senescyt.firma.exceptions.mappers;

import ec.gob.senescyt.firma.exceptions.FirmaDigitalExcepcionGeneral;
import org.junit.Before;
import org.junit.Test;

import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;

import static org.apache.commons.lang.RandomStringUtils.randomAlphabetic;
import static org.eclipse.jetty.http.HttpStatus.INTERNAL_SERVER_ERROR_500;
import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.Mockito.mock;

public class FirmaDigitalExcepcionGeneralExceptionMapperTest {


    private ExceptionMapper capturadorExcepcion;
    private String mensaje;
    private FirmaDigitalExcepcionGeneral excepcionGeneral;

    @Before
    public void setUp() {
        capturadorExcepcion = new FirmaDigitalExcepcionGeneralExceptionMapper();
        Throwable excepcionHija = mock(Throwable.class);
        mensaje = randomAlphabetic(10);
        excepcionGeneral = new FirmaDigitalExcepcionGeneral(mensaje, excepcionHija);
    }

    @Test
    public void debeRetornarCodigoDeError500ErrorInterno() {
        Response respuesta = capturadorExcepcion.toResponse(excepcionGeneral);
        assertThat(respuesta.getStatus(), is(INTERNAL_SERVER_ERROR_500));
    }

    @Test
    public void debeRetornarUnaInstanciaDeLaClaseErrores() {
        Response respuesta = capturadorExcepcion.toResponse(excepcionGeneral);
        assertThat(respuesta.getEntity(), instanceOf(Errores.class));
    }

    @Test
    public void debeRetornarElMensajeAmigableComoParteDelContenido() {
        Response respuesta = capturadorExcepcion.toResponse(excepcionGeneral);
        Errores errores = (Errores) respuesta.getEntity();
        assertThat(errores.getErrors(), is(mensaje));
    }
}