package ec.gob.senescyt.firma.security.certs;

import org.junit.Test;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

public class CertificadoFactoryTest {

    @Test
    public void debeRetornarUnaInstanciaDelCertificadoIndicado() throws CertificateException, IOException {
        CertificadoFactory fabrica = new CertificadoFactory();
        URL recurso = getClass().getResource("certBceRaiz.cer");
        X509Certificate certificado = fabrica.obtenerCertificado(recurso.getPath());
        assertThat(certificado, is(obtenerCertificado(recurso.getPath())));
    }


    private X509Certificate obtenerCertificado(String nombreArchivo) throws CertificateException, IOException {
        CertificateFactory fabrica = CertificateFactory.getInstance("X.509");
        try(InputStream archivoCertificado = new FileInputStream(nombreArchivo)) {
            return (X509Certificate) fabrica.generateCertificate(archivoCertificado);
        }
    }
}
