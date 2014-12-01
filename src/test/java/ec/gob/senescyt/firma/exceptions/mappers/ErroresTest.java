package ec.gob.senescyt.firma.exceptions.mappers;

import ec.gob.senescyt.microservicios.tests.SerializacionBaseTest;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;

import static org.apache.commons.lang.RandomStringUtils.randomAlphabetic;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

public class ErroresTest extends SerializacionBaseTest {

    private String mensajeError;
    private Errores errores;

    @Before
    public void setUp() {
        mensajeError = randomAlphabetic(10);
        errores = new Errores(mensajeError);
    }

    @Test
    public void debeDebeTenerUnTextoDeErrores() {
        assertThat(errores.getErrores(), is(mensajeError));
    }

    @Test
    public void debeSerializarYDeserializarUnObjetoErrores() throws IOException {
        verificarSerializacion(errores, Errores.class);
    }
}