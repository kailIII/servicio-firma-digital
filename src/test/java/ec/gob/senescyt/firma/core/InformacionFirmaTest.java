package ec.gob.senescyt.firma.core;

import org.junit.Before;
import org.junit.Test;

import static org.apache.commons.lang.RandomStringUtils.randomAlphabetic;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;


public class InformacionFirmaTest {

    private String nombreUsuario;
    private String contrasenia;
    private InformacionFirma informacionFirma;
    private String textoAFirmar;

    @Before
    public void setUp() {
        textoAFirmar = randomAlphabetic(10);
        nombreUsuario = randomAlphabetic(10);
        contrasenia = randomAlphabetic(10);
        informacionFirma = new InformacionFirma(textoAFirmar, nombreUsuario, contrasenia);
    }

    @Test
    public void debeTenerNombreDeUsuario() {
        assertThat(informacionFirma.getNombreUsuario(), is(nombreUsuario));
    }

    @Test
    public void debeTenerTextoAFirmar() {
        assertThat(informacionFirma.getTextoAFirmar(), is(textoAFirmar));
    }

    @Test
    public void debeTenerContrasenia() {
        assertThat(informacionFirma.getContrasenia(), is(contrasenia));
    }
}