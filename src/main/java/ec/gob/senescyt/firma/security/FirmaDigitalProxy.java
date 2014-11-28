package ec.gob.senescyt.firma.security;

import ec.gob.senescyt.firma.exceptions.AlmacenLlavesExcepcion;
import ec.gob.senescyt.firma.exceptions.FirmaDigitalExcepcion;
import ec.gob.senescyt.firma.exceptions.ValidacionCertificadoExcepcion;
import ec.gob.senescyt.firma.security.certs.CertificadoFactory;
import ec.gob.senescyt.firma.security.certs.CertificadosRaizFactory;
import ec.gob.senescyt.firma.security.certs.FirmaDigitalProxyConfiguracion;

import java.io.IOException;
import java.security.InvalidAlgorithmParameterException;
import java.security.NoSuchAlgorithmException;
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
    public byte[] firmar(String cadenaAFirmar, String caminoArchivo, String contrasenia) throws FirmaDigitalExcepcion {
        try {
            validarCertificadoDigital(caminoArchivo);
        } catch (IOException | CertificateException | InvalidAlgorithmParameterException | CertPathValidatorException e) {
            throw new ValidacionCertificadoExcepcion("Error de validación del certificado", e);
        }
        return firmaDigitalReal.firmar(cadenaAFirmar, caminoArchivo, contrasenia);
    }

    @Override
    public boolean existeLlavePrivadaParaFirmar(String caminoArchivo, String contrasenia) throws AlmacenLlavesExcepcion {
        return firmaDigitalReal.existeLlavePrivadaParaFirmar(caminoArchivo, contrasenia);
    }

    private void validarCertificadoDigital(String caminoArchivo) throws IOException, CertificateException, InvalidAlgorithmParameterException, CertPathValidatorException {
        CertPath certificadosParaValidar = generarCertPath(caminoArchivo);
        TrustAnchor anchorRaiz = generarAnchor();
        PKIXParameters parametros = new PKIXParameters(newHashSet(anchorRaiz));
        configuracion.configurar();
        validadorCertificados.validate(certificadosParaValidar, parametros);
    }

    private CertPath generarCertPath(String caminoArchivo) throws IOException, CertificateException {
        X509Certificate certificadoSubordinado = certificadosRaizFactory.obtenerCertificadoRaiz(BCE_SUBORDINADO);
        X509Certificate certificadoHijo = certificadoFactory.obtenerCertificado(caminoArchivo);
        return fabricaCertificados.generateCertPath(newArrayList(certificadoHijo, certificadoSubordinado));
    }

    private TrustAnchor generarAnchor() throws IOException, CertificateException {
        X509Certificate certificadoRaiz = certificadosRaizFactory.obtenerCertificadoRaiz(BCE_RAIZ);
        return new TrustAnchor(certificadoRaiz, null);
    }
}
