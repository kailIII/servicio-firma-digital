package ec.gob.senescyt.firma.security;

import ec.gob.senescyt.firma.exceptions.AlmacenLlavesExcepcion;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;

public class AlmacenLlavesPkcs12Provider implements AlmacenLlavesProvider {
    public static final String ALMACEN_PKCS12 = "PKCS12";
    private KeyStore almacenLlaves;
    private AliasProvider aliasProvider;

    public AlmacenLlavesPkcs12Provider(AliasProvider aliasProvider) throws AlmacenLlavesExcepcion {
        this.aliasProvider = aliasProvider;
        try {
            this.almacenLlaves = KeyStore.getInstance(ALMACEN_PKCS12);
        } catch (KeyStoreException e) {
            throw new AlmacenLlavesExcepcion("Error al obtener el almacen de llaves", e);
        }
    }

    public PrivateKey obtenerLlavePrivadaParaFirmar(String caminoArchivo, String contrasenia) throws AlmacenLlavesExcepcion {
        try {
            cargarArchivosDeFirma(caminoArchivo, contrasenia);
            String aliasParaFirmar = aliasProvider.obtenerPrimerAliasParaFirmar(almacenLlaves);
            return (PrivateKey) almacenLlaves.getKey(aliasParaFirmar, contrasenia.toCharArray());
        } catch (IOException | NoSuchAlgorithmException | CertificateException | UnrecoverableKeyException | KeyStoreException e) {
            throw new AlmacenLlavesExcepcion("Error al obtener la llave privada para firmar", e);
        }
    }

    private void cargarArchivosDeFirma(String caminoArchivo, String contrasenia) throws IOException, NoSuchAlgorithmException, CertificateException {
        try(InputStream archivo = new FileInputStream(caminoArchivo)) {
            almacenLlaves.load(archivo, contrasenia.toCharArray());
        }
    }
}
