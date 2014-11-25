package ec.gob.senescyt.firma.security;

import java.io.IOException;
import java.io.InputStream;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;

public class AlmacenLlavesPkcs12Provider {
    public static final String ALMACEN_PKCS12 = "PKCS12";
    private KeyStore almacenLlaves;
    private AliasProvider aliasProvider;

    public AlmacenLlavesPkcs12Provider(AliasProvider aliasProvider) throws KeyStoreException {
        this.aliasProvider = aliasProvider;
        this.almacenLlaves = KeyStore.getInstance(ALMACEN_PKCS12);
    }

    public PrivateKey obtenerLlavePrivadaParaFirmar(InputStream archivo, String contrasenia)
            throws IOException, CertificateException, NoSuchAlgorithmException, KeyStoreException, UnrecoverableKeyException {
        cargarDatosDeFirma(archivo, contrasenia);
        String aliasParaFirmar = aliasProvider.obtenerPrimerAliasParaFirmar(almacenLlaves);
        return (PrivateKey) almacenLlaves.getKey(aliasParaFirmar, contrasenia.toCharArray());
    }

    private void cargarDatosDeFirma(InputStream archivo, String contrasenia) throws IOException, NoSuchAlgorithmException, CertificateException {
        try {
            almacenLlaves.load(archivo, contrasenia.toCharArray());
        }
        finally {
            archivo.close();
        }
    }
}
