package ec.gob.senescyt.firma.core;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.dropwizard.jackson.Jackson;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;

import static org.apache.commons.lang.RandomStringUtils.randomAlphabetic;
import static org.apache.commons.lang.math.RandomUtils.nextLong;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.MatcherAssert.assertThat;

public class DocumentoFirmadoTest {

    private DocumentoFirmado documentoFirmado;
    private String firmaDigital;
    private ConfiguracionFirma configuracionFirma;
    private long idDocumentoFirmado;

    @Before
    public void setUp() {
        firmaDigital = randomAlphabetic(10);
        configuracionFirma = new ConfiguracionFirma(randomAlphabetic(10), randomAlphabetic(10));
        idDocumentoFirmado = nextLong();
        documentoFirmado = new DocumentoFirmado(idDocumentoFirmado, firmaDigital, configuracionFirma);
    }

    @Test
    public void debeTenerUnaIdentificacion() {
        assertThat(documentoFirmado.getId(), is(idDocumentoFirmado));
    }

    @Test
    public void debeTenerLaFirmaDigital() {
        assertThat(documentoFirmado.getFirmaDigital(), is(firmaDigital));
    }

    @Test
    public void debeTenerLaConfiguracionDeFirma() {
        assertThat(documentoFirmado.getConfiguracionFirma(), is(configuracionFirma));
    }

    @Test
    public void debeSerializarYDeserializarUnDocumento() throws IOException {
        ObjectMapper mapper = Jackson.newObjectMapper();
        String entidadSerializada = mapper.writeValueAsString(documentoFirmado);
        DocumentoFirmado entidadDeserializada = mapper.readValue(entidadSerializada, DocumentoFirmado.class);
        assertThat(entidadDeserializada.getFirmaDigital(), is(firmaDigital));
        assertThat(entidadDeserializada.getConfiguracionFirma(), is(nullValue()));
    }
}