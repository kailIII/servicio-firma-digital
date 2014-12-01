package ec.gob.senescyt.firma.security;

import ec.gob.senescyt.firma.exceptions.AlmacenLlavesExcepcion;
import ec.gob.senescyt.firma.exceptions.FirmaDigitalExcepcion;

import java.nio.charset.Charset;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.Signature;
import java.security.SignatureException;

public class FirmaDigitalImpl implements FirmaDigital {

    private static final String SHA_1_WITH_RSA = "SHA256withRSA";
    private AlmacenLlavesProvider almacenLlaves;
    private Signature signature;

    public FirmaDigitalImpl(AlmacenLlavesProvider almacenLlaves) throws NoSuchAlgorithmException {
        this.almacenLlaves = almacenLlaves;
        signature = Signature.getInstance(SHA_1_WITH_RSA);
    }

    public byte[] firmar(String cadenaAFirmar, String caminoArchivo, String contrasenia) throws FirmaDigitalExcepcion {
        try {
            PrivateKey privateKey = almacenLlaves.obtenerLlavePrivadaParaFirmar(caminoArchivo, contrasenia);
            signature.initSign(privateKey);
            signature.update(cadenaAFirmar.getBytes(Charset.forName("UTF-8")));
            return signature.sign();
        } catch (SignatureException | AlmacenLlavesExcepcion | InvalidKeyException e) {
            throw new FirmaDigitalExcepcion(e);
        }
    }

    public boolean existeLlavePrivadaParaFirmar(String caminoArchivo, String contrasenia) {
        try {
            PrivateKey privateKey = almacenLlaves.obtenerLlavePrivadaParaFirmar(caminoArchivo, contrasenia);
            return privateKey != null;
        } catch (AlmacenLlavesExcepcion e) {
            return false;
        }
    }
}
