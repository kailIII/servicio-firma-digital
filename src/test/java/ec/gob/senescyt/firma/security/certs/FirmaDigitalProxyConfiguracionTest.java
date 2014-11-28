package ec.gob.senescyt.firma.security.certs;

import org.junit.Test;

import java.security.Security;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;

public class FirmaDigitalProxyConfiguracionTest {

    @Test
    public void debeActivarLaConfiguracionOcsp() {
        FirmaDigitalProxyConfiguracion configuracion = new FirmaDigitalProxyConfiguracion();
        configuracion.configurar();
        assertThat(Security.getProperty("ocsp.enable"), is("true"));
    }

    @Test
    public void debeActivarLaValidacionCRL() {
        FirmaDigitalProxyConfiguracion configuracion = new FirmaDigitalProxyConfiguracion();
        configuracion.configurar();
        assertThat(System.getProperty("com.sun.security.enableCRLDP"), is("true"));
    }
}
