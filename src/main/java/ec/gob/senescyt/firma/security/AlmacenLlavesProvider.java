package ec.gob.senescyt.firma.security;

import ec.gob.senescyt.firma.exceptions.AlmacenLlavesExcepcion;

import java.security.PrivateKey;

public interface AlmacenLlavesProvider {

    PrivateKey obtenerLlavePrivadaParaFirmar(String caminoArchivo, String contrasenia) throws AlmacenLlavesExcepcion;
}
