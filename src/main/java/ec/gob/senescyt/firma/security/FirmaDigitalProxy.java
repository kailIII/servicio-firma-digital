package ec.gob.senescyt.firma.security;

import ec.gob.senescyt.firma.exceptions.ValidacionCertificadoExcepcion;
import ec.gob.senescyt.firma.security.certs.CertificadoFactory;
import ec.gob.senescyt.firma.security.certs.CertificadosRaizFactory;
import ec.gob.senescyt.firma.security.certs.FirmaDigitalProxyConfiguracion;

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
import java.security.cert.PKIXParameters;
import java.security.cert.TrustAnchor;
import java.security.cert.X509Certificate;

import static com.google.common.collect.Lists.newArrayList;
import static com.google.common.collect.Sets.newHashSet;
import static ec.gob.senescyt.firma.security.certs.TiposCertificadosRaiz.BCE_RAIZ;
import static ec.gob.senescyt.firma.security.certs.TiposCertificadosRaiz.BCE_SUBORDINADO;

public class FirmaDigitalProxy implements FirmaDigital {

    private FirmaDigital firmaDigitalReal;
    private CertificadosRaizFactory certificadosRaizFactory;
    private CertificadoFactory certificadoFactory;
    private FirmaDigitalProxyConfiguracion configuracion;
    private final CertificateFactory fabricaCertificados;
    private final CertPathValidator validadorCertificados;

    public FirmaDigitalProxy(FirmaDigital firmaDigitalReal, CertificadosRaizFactory certificadosRaizFactory, CertificadoFactory certificadoFactory, FirmaDigitalProxyConfiguracion configuracion) throws CertificateException, NoSuchAlgorithmException {
        this.firmaDigitalReal = firmaDigitalReal;
        this.certificadosRaizFactory = certificadosRaizFactory;
        this.certificadoFactory = certificadoFactory;
        this.configuracion = configuracion;
        fabricaCertificados = CertificateFactory.getInstance("X.509");
        validadorCertificados = CertPathValidator.getInstance("PKIX");
    }

    @Override
    public byte[] firmar(String cadenaAFirmar, String caminoArchivo, String contrasenia) throws IOException, UnrecoverableKeyException, CertificateException, NoSuchAlgorithmException, KeyStoreException, SignatureException, InvalidKeyException, ValidacionCertificadoExcepcion {
        validarCertificadoDigital(caminoArchivo);
        return firmaDigitalReal.firmar(cadenaAFirmar, caminoArchivo, contrasenia);
    }

    @Override
    public boolean existeLlavePrivadaParaFirmar(String caminoArchivo, String contrasenia) throws CertificateException, IOException {
        return firmaDigitalReal.existeLlavePrivadaParaFirmar(caminoArchivo, contrasenia);
    }

    private void validarCertificadoDigital(String caminoArchivo) throws IOException, CertificateException, ValidacionCertificadoExcepcion {
        X509Certificate certificadoRaiz = certificadosRaizFactory.obtenerCertificadoRaiz(BCE_RAIZ);
        X509Certificate certificadoSubordinado = certificadosRaizFactory.obtenerCertificadoRaiz(BCE_SUBORDINADO);
        X509Certificate certificadoHijo = certificadoFactory.obtenerCertificado(caminoArchivo);
        CertPath certificadosParaValidar = fabricaCertificados.generateCertPath(newArrayList(certificadoHijo, certificadoSubordinado));
        TrustAnchor anchorRaiz = new TrustAnchor(certificadoRaiz, null);
        try {
            PKIXParameters parametros = new PKIXParameters(newHashSet(anchorRaiz));
            configuracion.configurar();
            validadorCertificados.validate(certificadosParaValidar, parametros);
        } catch (CertPathValidatorException | InvalidAlgorithmParameterException e) {
            throw new ValidacionCertificadoExcepcion("Error de validación del certificado", e);
        }
    }
}
