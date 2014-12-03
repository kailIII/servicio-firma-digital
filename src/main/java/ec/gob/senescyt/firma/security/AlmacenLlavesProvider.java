package ec.gob.senescyt.firma.security;

import ec.gob.senescyt.firma.exceptions.AlmacenLlavesExcepcion;

import java.security.PrivateKey;
import java.security.cert.X509Certificate;

public interface AlmacenLlavesProvider {

    PrivateKey obtenerLlavePrivadaParaFirmar(String caminoArchivo, String contrasenia) throws AlmacenLlavesExcepcion;
    X509Certificate obtenerCertificadoDeLaFirma(String caminoArchivo, String contrasenia) throws AlmacenLlavesExcepcion;
}
