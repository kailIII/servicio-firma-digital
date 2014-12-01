package ec.gob.senescyt.firma.exceptions;

import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.Mockito.mock;

public class FirmaDigitalExcepcionTest {

    private static final String ERROR_AL_REALIZAR_LA_FIRMA_DIGITAL = "Error al realizar la firma digital";

    @Test
    public void debeCrearUnaExcepcionConMensajePorDefecto() {
        Throwable excepcionHija = mock(Throwable.class);
        FirmaDigitalExcepcionGeneral excepcion = new FirmaDigitalExcepcion(excepcionHija);
        assertThat(excepcion.getMessage(), is(ERROR_AL_REALIZAR_LA_FIRMA_DIGITAL));
        assertThat(excepcion.getCause(), is(excepcionHija));
    }
}