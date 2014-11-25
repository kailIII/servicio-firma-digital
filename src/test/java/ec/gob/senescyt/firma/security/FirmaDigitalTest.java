package ec.gob.senescyt.firma.security;

import ec.gob.senescyt.microservicios.commons.core.InformacionFirma;
import org.junit.Test;

import static org.apache.commons.lang.RandomStringUtils.randomAlphabetic;
import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.MatcherAssert.assertThat;

public class FirmaDigitalTest {
    @Test
    public void debeRetornarLaFirmaDigitalDadoUnaInformacionFirma() {
        FirmaDigital firmaDigital = new FirmaDigital();
        InformacionFirma informacionFirma = new InformacionFirma(randomAlphabetic(10), randomAlphabetic(10), randomAlphabetic(10));
        assertThat(firmaDigital.firmar(informacionFirma), instanceOf(byte[].class));
    }
}