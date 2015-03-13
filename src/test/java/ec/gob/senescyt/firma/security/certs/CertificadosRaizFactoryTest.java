package ec.gob.senescyt.firma.security.certs;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;

import static ec.gob.senescyt.firma.security.certs.TiposCertificadosRaiz.BCE_RAIZ;
import static ec.gob.senescyt.firma.security.certs.TiposCertificadosRaiz.BCE_SUBORDINADO;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

@RunWith(PowerMockRunner.class)
@PrepareForTest({CertificadosRaizFactory.class})
public class CertificadosRaizFactoryTest {

    @Test
    public void debeRetornarUnaInstanciaDelCertificadoRaizSubordinadoParaBCE() throws CertificateException, IOException {
        CertificadosRaizFactory fabrica = new CertificadosRaizFactory();
        X509Certificate certificado = fabrica.obtenerCertificadoRaiz(BCE_SUBORDINADO);
        assertThat(certificado, is(obtenerCertificado(BCE_SUBORDINADO.getNombreArchivo())));
    }

    @Test
    public void debeRetornarUnaInstanciaDelCertificadoRaizParaBCE() throws CertificateException, IOException {
        CertificadosRaizFactory fabrica = new CertificadosRaizFactory();
        X509Certificate certificado = fabrica.obtenerCertificadoRaiz(BCE_RAIZ);
        assertThat(certificado, is(obtenerCertificado(BCE_RAIZ.getNombreArchivo())));
    }

    private X509Certificate obtenerCertificado(String nombreArchivo) throws CertificateException, IOException {
        URL recurso = CertificadosRaizFactoryTest.class.getResource(nombreArchivo);
        CertificateFactory fabrica = CertificateFactory.getInstance("X.509");
        try (InputStream archivoCertificado = new FileInputStream(recurso.getPath())) {
            return (X509Certificate) fabrica.generateCertificate(archivoCertificado);
        }
    }
}
