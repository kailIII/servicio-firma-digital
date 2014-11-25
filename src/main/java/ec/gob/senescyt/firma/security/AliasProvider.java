package ec.gob.senescyt.firma.security;

import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.cert.X509Certificate;
import java.util.Enumeration;

public class AliasProvider {

    private static final String ALIAS_VACIO = null;

    public String obtenerPrimerAliasParaFirmar(KeyStore keyStore) throws KeyStoreException {
        Enumeration<String> aliases = keyStore.aliases();
        while (aliases.hasMoreElements()) {
            String alias = aliases.nextElement();
            X509Certificate certificado = (X509Certificate) keyStore.getCertificate(alias);
            boolean[] usosLlave = certificado.getKeyUsage();
            if (usosLlave != null && usosLlave[0]) {
                return alias;
            }
        }
        return ALIAS_VACIO;
    }
}
