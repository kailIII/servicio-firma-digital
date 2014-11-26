package ec.gob.senescyt.firma.security;

import java.io.IOException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;

public interface AlmacenLlavesProvider {
    PrivateKey obtenerLlavePrivadaParaFirmar(String caminoArchivo, String contrasenia) throws KeyStoreException, CertificateException, NoSuchAlgorithmException, IOException, UnrecoverableKeyException;
}
