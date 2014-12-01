package ec.gob.senescyt.firma.exceptions;

import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.Mockito.mock;

public class ValidacionCertificadoExcepcionTest {

    private static final String ERROR_VALIDACION_CERTIFICADO = "Error de validación del certificado";

    @Test
    public void debeCrearUnaExcepcionConMensajePorDefecto() {
        Throwable excepcionHija = mock(Throwable.class);
        FirmaDigitalExcepcion excepcion = new ValidacionCertificadoExcepcion(excepcionHija);
        assertThat(excepcion.getMessage(), is(ERROR_VALIDACION_CERTIFICADO));
        assertThat(excepcion.getCause(), is(excepcionHija));
    }
}