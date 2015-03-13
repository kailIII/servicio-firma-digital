package ec.gob.senescyt.firma.security.certs;

import java.io.IOException;
import java.io.InputStream;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;

public class CertificadosRaizFactory {

    private final CertificateFactory fabrica;

    public CertificadosRaizFactory() throws CertificateException {
        fabrica = CertificateFactory.getInstance("X.509");
    }

    public X509Certificate obtenerCertificadoRaiz(TiposCertificadosRaiz tiposCertificadosRaiz) throws IOException, CertificateException {
        try (InputStream archivoCertificado = getClass().getResourceAsStream(tiposCertificadosRaiz.getNombreArchivo())) {
            return (X509Certificate) fabrica.generateCertificate(archivoCertificado);
        }
    }
}
