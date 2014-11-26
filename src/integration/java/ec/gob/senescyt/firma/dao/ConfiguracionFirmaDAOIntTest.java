package ec.gob.senescyt.firma.dao;

import ec.gob.senescyt.firma.FirmaDigitalBaseIntTest;
import ec.gob.senescyt.firma.core.ConfiguracionFirma;
import org.junit.Before;
import org.junit.Test;

import java.util.List;

import static org.apache.commons.lang.RandomStringUtils.randomAlphabetic;
import static org.hamcrest.CoreMatchers.hasItem;
import static org.hamcrest.MatcherAssert.assertThat;

public class ConfiguracionFirmaDAOIntTest extends FirmaDigitalBaseIntTest {

    private ConfiguracionFirmaDAO configuracionFirmaDAO;

    @Override
    @Before
    public void setUp() {
        super.setUp();
        configuracionFirmaDAO = new ConfiguracionFirmaDAO(sessionFactory, defaultSchema);
    }

    @Test
    public void debeGuardarUnaConfiguracionDeFirma() {
        ConfiguracionFirma configuracionFirma = new ConfiguracionFirma(randomAlphabetic(10), randomAlphabetic(10));
        configuracionFirmaDAO.guardar(configuracionFirma);
        List<ConfiguracionFirma> configuracionesFirma = configuracionFirmaDAO.obtenerTodos();
        assertThat(configuracionesFirma, hasItem(configuracionFirma));
    }
}