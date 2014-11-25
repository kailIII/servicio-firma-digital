package ec.gob.senescyt.firma.core;

import org.junit.Before;
import org.junit.Test;

import static org.apache.commons.lang.RandomStringUtils.randomAlphabetic;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

public class ConfiguracionFirmaTest {

    private String nombreUsuario;
    private String caminoArchivo;
    private ConfiguracionFirma configuracionFirma;

    @Before
    public void setUp() {
        nombreUsuario = randomAlphabetic(10);
        caminoArchivo = randomAlphabetic(10);
        configuracionFirma = new ConfiguracionFirma(nombreUsuario, caminoArchivo);
    }

    @Test
    public void debeTenerElCaminoDelArchivo() {
        assertThat(configuracionFirma.getCaminoArchivo(), is(caminoArchivo));
    }

    @Test
    public void debeTenerNombreUsuario() {
        assertThat(configuracionFirma.getNombreUsuario(), is(nombreUsuario));
    }
}