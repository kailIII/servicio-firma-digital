package ec.gob.senescyt.firma.security.certs;

import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

public class TiposCertificadosRaizTest {

    @Test
    public void debeRetornarElNombreDelArchivoParaElCertificadoRaiz() {
        String nombreArchivo = TiposCertificadosRaiz.BCE_RAIZ.getNombreArchivo();
        assertThat(nombreArchivo, is("certBceRaiz.cer"));
    }

    @Test
    public void debeRetornarElNombreDelArchivoParaElCertificadoSubordinado() {
        String nombreArchivo = TiposCertificadosRaiz.BCE_SUBORDINADO.getNombreArchivo();
        assertThat(nombreArchivo, is("certBceSubordinado.cer"));
    }
}