package ec.gob.senescyt.firma.core;

import org.junit.Before;
import org.junit.Test;

import static org.apache.commons.lang.RandomStringUtils.randomAlphabetic;
import static org.apache.commons.lang.math.RandomUtils.nextBoolean;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

public class ConfiguracionFirmaTest {

    private String nombreUsuario;
    private String caminoArchivo;
    private ConfiguracionFirma configuracionFirma;
    private boolean activa;

    @Before
    public void setUp() {
        nombreUsuario = randomAlphabetic(10);
        caminoArchivo = randomAlphabetic(10);
        activa = nextBoolean();
        configuracionFirma = new ConfiguracionFirma(nombreUsuario, caminoArchivo, activa);
    }

    @Test
    public void debeTenerElCaminoDelArchivo() {
        assertThat(configuracionFirma.getCaminoArchivo(), is(caminoArchivo));
    }

    @Test
    public void debeTenerNombreUsuario() {
        assertThat(configuracionFirma.getNombreUsuario(), is(nombreUsuario));
    }

    @Test
    public void debeTenerUnCampoIndicandoSiEstaActiva() {
        assertThat(configuracionFirma.getActiva(), is(activa));
    }

    @Test
    public void debeSerActivaPorDefecto() {
        configuracionFirma = new ConfiguracionFirma(nombreUsuario, caminoArchivo);
        assertThat(configuracionFirma.getActiva(), is(true));
    }
}