package ec.gob.senescyt.firma.security;

import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.SignatureException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;

public interface FirmaDigital {

    byte[] firmar(String cadenaAFirmar, String caminoArchivo, String contrasenia) throws IOException, UnrecoverableKeyException, CertificateException, NoSuchAlgorithmException, KeyStoreException, SignatureException, InvalidKeyException;

    boolean existeLlavePrivadaParaFirmar(String caminoArchivo, String contrasenia) throws CertificateException, IOException;
}
