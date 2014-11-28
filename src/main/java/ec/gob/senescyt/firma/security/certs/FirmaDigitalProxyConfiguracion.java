package ec.gob.senescyt.firma.security.certs;

import java.security.Security;

public class FirmaDigitalProxyConfiguracion {

    public void configurar() {
        Security.setProperty("ocsp.enable", "true");
        System.setProperty("com.sun.security.enableCRLDP", "true");
    }
}
