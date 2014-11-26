package ec.gob.senescyt.firma.dao;

import ec.gob.senescyt.firma.FirmaDigitalBaseIntTest;
import ec.gob.senescyt.firma.core.ConfiguracionFirma;
import org.junit.Before;
import org.junit.Test;

import java.util.List;
import java.util.Optional;

import static org.apache.commons.lang.RandomStringUtils.randomAlphabetic;
import static org.hamcrest.CoreMatchers.hasItem;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

public class ConfiguracionFirmaDAOIntTest extends FirmaDigitalBaseIntTest {

    private ConfiguracionFirmaDAO configuracionFirmaDAO;
    private ConfiguracionFirma configuracionFirmaActiva;
    private String nombreUsuarioActivo;
    private String nombreUsuarioInactivo;

    @Override
    @Before
    public void setUp() {
        super.setUp();
        configuracionFirmaDAO = new ConfiguracionFirmaDAO(sessionFactory, defaultSchema);
        nombreUsuarioActivo = randomAlphabetic(15);
        nombreUsuarioInactivo = randomAlphabetic(15);
        configuracionFirmaActiva = new ConfiguracionFirma(nombreUsuarioActivo, randomAlphabetic(10));
        ConfiguracionFirma configuracionFirmaInactiva = new ConfiguracionFirma(nombreUsuarioInactivo, randomAlphabetic(10), false);
        configuracionFirmaDAO.guardar(configuracionFirmaActiva);
        configuracionFirmaDAO.guardar(configuracionFirmaInactiva);
    }

    @Test
    public void debeGuardarUnaConfiguracionDeFirma() {
        List<ConfiguracionFirma> configuracionesFirma = configuracionFirmaDAO.obtenerTodos();
        assertThat(configuracionesFirma, hasItem(configuracionFirmaActiva));
    }

    @Test
    public void debeObtenerLaConfiguracionActivaDadoElNombreDeUsuario() {
        Optional<ConfiguracionFirma> configuracionFirma = configuracionFirmaDAO.obtenerPorUsuario(nombreUsuarioActivo);
        assertThat(configuracionFirma.get(), is(configuracionFirmaActiva));
    }

    @Test
    public void debeObtenerVacioSiLaConfiguracionNoEstaActiva() {
        Optional<ConfiguracionFirma> configuracionFirma = configuracionFirmaDAO.obtenerPorUsuario(nombreUsuarioInactivo);
        assertThat(configuracionFirma.isPresent(), is(false));
    }
}