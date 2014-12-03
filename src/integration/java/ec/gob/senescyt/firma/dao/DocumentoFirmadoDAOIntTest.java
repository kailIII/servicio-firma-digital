package ec.gob.senescyt.firma.dao;

import ec.gob.senescyt.firma.FirmaDigitalBaseIntTest;
import ec.gob.senescyt.firma.core.ConfiguracionFirma;
import ec.gob.senescyt.firma.core.DocumentoFirmado;
import org.junit.Before;
import org.junit.Test;

import java.util.List;

import static org.apache.commons.lang.RandomStringUtils.randomAlphabetic;
import static org.hamcrest.CoreMatchers.hasItem;
import static org.hamcrest.MatcherAssert.assertThat;

public class DocumentoFirmadoDAOIntTest extends FirmaDigitalBaseIntTest{

    private DocumentoFirmadoDAO documentoFirmadoDAO;
    private ConfiguracionFirma configuracionFirma;

    @Override
    @Before
    public void setUp() {
        super.setUp();
        documentoFirmadoDAO = new DocumentoFirmadoDAO(sessionFactory, defaultSchema);
        configuracionFirma = new ConfiguracionFirma(randomAlphabetic(10), randomAlphabetic(10));
        ConfiguracionFirmaDAO configuracionFirmaDAO = new ConfiguracionFirmaDAO(sessionFactory, defaultSchema);
        configuracionFirmaDAO.guardar(configuracionFirma);

    }

    @Test
    public void debeGuardarUnaConfiguracionDeFirma() {
        DocumentoFirmado documentoFirmado = new DocumentoFirmado(randomAlphabetic(10), randomAlphabetic(100), configuracionFirma);
        documentoFirmadoDAO.guardar(documentoFirmado);
        List<DocumentoFirmado> documentosFirmados = documentoFirmadoDAO.obtenerTodos();
        assertThat(documentosFirmados, hasItem(documentoFirmado));
    }
}