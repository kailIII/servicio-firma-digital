package ec.gob.senescyt.firma.security;

import java.io.IOException;
import java.nio.charset.Charset;
import java.security.InvalidKeyException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.Signature;
import java.security.SignatureException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;

public class FirmaDigitalImpl implements FirmaDigital {

    private static final String SHA_1_WITH_RSA = "SHA256withRSA";
    private AlmacenLlavesProvider almacenLlaves;
    private Signature signature;

    public FirmaDigitalImpl(AlmacenLlavesProvider almacenLlaves) throws NoSuchAlgorithmException {
        this.almacenLlaves = almacenLlaves;
        signature = Signature.getInstance(SHA_1_WITH_RSA);
    }

    public byte[] firmar(String cadenaAFirmar, String caminoArchivo, String contrasenia) throws IOException, UnrecoverableKeyException, CertificateException, NoSuchAlgorithmException, KeyStoreException, SignatureException, InvalidKeyException {
        PrivateKey privateKey = almacenLlaves.obtenerLlavePrivadaParaFirmar(caminoArchivo, contrasenia);
        signature.initSign(privateKey);
        signature.update(cadenaAFirmar.getBytes(Charset.forName("UTF-8")));
        return signature.sign();
    }

    public boolean existeLlavePrivadaParaFirmar(String caminoArchivo, String contrasenia) throws CertificateException, IOException {
        try {
            PrivateKey privateKey = almacenLlaves.obtenerLlavePrivadaParaFirmar(caminoArchivo, contrasenia);
            return privateKey != null;
        }
        catch (UnrecoverableKeyException | NoSuchAlgorithmException | KeyStoreException | IOException ex) {
            return false;
        }
    }
}
