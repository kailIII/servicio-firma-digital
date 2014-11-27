package ec.gob.senescyt.firma.security.certs;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;

public class CertificadoFactory {

    private final CertificateFactory fabrica;

    public CertificadoFactory() throws CertificateException {
        fabrica = CertificateFactory.getInstance("X.509");
    }

    public X509Certificate obtenerCertificado(String nombreArchivo) throws IOException, CertificateException {
        try(InputStream archivoCertificado = new FileInputStream(nombreArchivo)) {
            return (X509Certificate) fabrica.generateCertificate(archivoCertificado);
        }
    }
}
