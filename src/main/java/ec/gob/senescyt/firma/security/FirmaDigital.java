package ec.gob.senescyt.firma.security;

import ec.gob.senescyt.firma.exceptions.AlmacenLlavesExcepcion;
import ec.gob.senescyt.firma.exceptions.FirmaDigitalExcepcion;

public interface FirmaDigital {

    byte[] firmar(String cadenaAFirmar, String caminoArchivo, String contrasenia) throws FirmaDigitalExcepcion;

    boolean existeLlavePrivadaParaFirmar(String caminoArchivo, String contrasenia) throws AlmacenLlavesExcepcion;
}
