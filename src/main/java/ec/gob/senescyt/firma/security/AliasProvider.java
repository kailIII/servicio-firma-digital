package ec.gob.senescyt.firma.security;

import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.cert.X509Certificate;
import java.util.Enumeration;

public class AliasProvider {

    private static final String ALIAS_VACIO = null;
    private static final int POSICION_FIRMA_DIGITAL = 0;

    public String obtenerPrimerAliasParaFirmar(KeyStore keyStore) throws KeyStoreException {
        Enumeration<String> aliases = keyStore.aliases();
        while (aliases.hasMoreElements()) {
            String alias = validarSiAliasPermiteFirma(aliases.nextElement(), keyStore);
            if (!alias.equals(ALIAS_VACIO)) {
                return alias;
            }
        }
        return ALIAS_VACIO;
    }

    private String validarSiAliasPermiteFirma(String alias, KeyStore keyStore) throws KeyStoreException {
        boolean[] usosLlave = obtenerUsosLlavesParaAlias(alias, keyStore);
        if (permiteLaLLaveFirmaDigital(usosLlave)) {
            return alias;
        }
        return ALIAS_VACIO;
    }

    private boolean permiteLaLLaveFirmaDigital(boolean[] usosLlave) {
        return usosLlave != null && usosLlave[POSICION_FIRMA_DIGITAL];
    }

    protected boolean[] obtenerUsosLlavesParaAlias(String alias, KeyStore keyStore) throws KeyStoreException {
        X509Certificate certificado = (X509Certificate) keyStore.getCertificate(alias);
        return certificado.getKeyUsage();
    }
}
