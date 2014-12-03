package ec.gob.senescyt.firma.security;

import ec.gob.senescyt.firma.exceptions.AlmacenLlavesExcepcion;
import ec.gob.senescyt.firma.exceptions.FirmaDigitalExcepcion;
import ec.gob.senescyt.firma.exceptions.ValidacionCertificadoExcepcion;
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
    private AlmacenLlavesProvider almacenLlaves;
    private FirmaDigitalProxyConfiguracion configuracion;
    private final CertificateFactory fabricaCertificados;
    private final CertPathValidator validadorCertificados;

    public FirmaDigitalProxy(FirmaDigital firmaDigitalReal, CertificadosRaizFactory certificadosRaizFactory,
                             AlmacenLlavesProvider almacenLlaves, FirmaDigitalProxyConfiguracion configuracion) throws CertificateException, NoSuchAlgorithmException {
        this.firmaDigitalReal = firmaDigitalReal;
        this.certificadosRaizFactory = certificadosRaizFactory;
        this.almacenLlaves = almacenLlaves;
        this.configuracion = configuracion;
        fabricaCertificados = CertificateFactory.getInstance("X.509");
        validadorCertificados = CertPathValidator.getInstance("PKIX");
    }

    @Override
    public byte[] firmar(String cadenaAFirmar, String caminoArchivo, String contrasenia) throws FirmaDigitalExcepcion {
        try {
            validarCertificadoDigital(caminoArchivo, contrasenia);
        } catch (IOException | CertificateException | InvalidAlgorithmParameterException | CertPathValidatorException | AlmacenLlavesExcepcion  e) {
                throw new ValidacionCertificadoExcepcion(e);
        }
        return firmaDigitalReal.firmar(cadenaAFirmar, caminoArchivo, contrasenia);
    }

    @Override
    public boolean existeLlavePrivadaParaFirmar(String caminoArchivo, String contrasenia) throws AlmacenLlavesExcepcion {
        return firmaDigitalReal.existeLlavePrivadaParaFirmar(caminoArchivo, contrasenia);
    }

    private void validarCertificadoDigital(String caminoArchivo, String contrasenia) throws IOException, CertificateException, InvalidAlgorithmParameterException, CertPathValidatorException, AlmacenLlavesExcepcion {
        CertPath certificadosParaValidar = generarCertPath(caminoArchivo, contrasenia);
        TrustAnchor anchorRaiz = generarAnchor();
        PKIXParameters parametros = new PKIXParameters(newHashSet(anchorRaiz));
        configuracion.configurar();
        validadorCertificados.validate(certificadosParaValidar, parametros);
    }

    private CertPath generarCertPath(String caminoArchivo, String contrasenia) throws IOException, CertificateException, AlmacenLlavesExcepcion {
        X509Certificate certificadoSubordinado = certificadosRaizFactory.obtenerCertificadoRaiz(BCE_SUBORDINADO);
        X509Certificate certificadoHijo = almacenLlaves.obtenerCertificadoDeLaFirma(caminoArchivo, contrasenia);
        return fabricaCertificados.generateCertPath(newArrayList(certificadoHijo, certificadoSubordinado));
    }

    private TrustAnchor generarAnchor() throws IOException, CertificateException {
        X509Certificate certificadoRaiz = certificadosRaizFactory.obtenerCertificadoRaiz(BCE_RAIZ);
        return new TrustAnchor(certificadoRaiz, null);
    }
}
