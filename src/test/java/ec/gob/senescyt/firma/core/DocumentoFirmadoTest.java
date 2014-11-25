package ec.gob.senescyt.firma.core;

import org.junit.Before;
import org.junit.Test;

import static org.apache.commons.lang.RandomStringUtils.randomAlphabetic;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

public class DocumentoFirmadoTest {

    private DocumentoFirmado documentoFirmado;
    private String firmaDigital;
    private ConfiguracionFirma configuracionFirma;

    @Before
    public void setUp() {
        firmaDigital = randomAlphabetic(10);
        configuracionFirma = new ConfiguracionFirma(randomAlphabetic(10), randomAlphabetic(10));
        documentoFirmado = new DocumentoFirmado(firmaDigital, configuracionFirma);
    }

    @Test
    public void debeTenerLaFirmaDigital() {
        assertThat(documentoFirmado.getFirmaDigital(), is(firmaDigital));
    }

    @Test
    public void debeTenerLaConfiguracionDeFirma() {
        assertThat(documentoFirmado.getConfiguracionFirma(), is(configuracionFirma));
    }
}