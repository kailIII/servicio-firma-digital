package ec.gob.senescyt.firma.security;

import ec.gob.senescyt.firma.security.certs.CertificadoFactory;
import ec.gob.senescyt.firma.security.certs.CertificadosRaizFactory;

import java.io.IOException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.SignatureException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertPath;
import java.security.cert.CertPathValidator;
import java.security.cert.CertPathValidatorException;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;

import static com.google.common.collect.Lists.newArrayList;
import static ec.gob.senescyt.firma.security.certs.TiposCertificadosRaiz.BCE_SUBORDINADO;

public class FirmaDigitalProxy implements FirmaDigital {

    private CertificadosRaizFactory certificadosRaizFactory;
    private CertificadoFactory certificadoFactory;

    public FirmaDigitalProxy(CertificadosRaizFactory certificadosRaizFactory, CertificadoFactory certificadoFactory) {
        this.certificadosRaizFactory = certificadosRaizFactory;
        this.certificadoFactory = certificadoFactory;
    }

    @Override
    public byte[] firmar(String cadenaAFirmar, String caminoArchivo, String contrasenia) throws IOException, UnrecoverableKeyException, CertificateException, NoSuchAlgorithmException, KeyStoreException, SignatureException, InvalidKeyException {
        X509Certificate certificadoSubordinado = certificadosRaizFactory.obtenerCertificadoRaiz(BCE_SUBORDINADO);
        X509Certificate certificadoHijo = certificadoFactory.obtenerCertificado(caminoArchivo);
        CertificateFactory fabrica = CertificateFactory.getInstance("X.509");
        CertPath certificadosParaValidar = fabrica.generateCertPath(newArrayList(certificadoHijo, certificadoSubordinado));
        CertPathValidator validador = CertPathValidator.getInstance("PKIX");
        try {
            validador.validate(certificadosParaValidar, null);
        } catch (CertPathValidatorException | InvalidAlgorithmParameterException e) {
            throw new UnsupportedOperationException(e.getMessage(), e);
        }
        return new byte[0];
    }

    @Override
    public boolean existeLlavePrivadaParaFirmar(String caminoArchivo, String contrasenia) throws CertificateException, IOException {
        return false;
    }
}
