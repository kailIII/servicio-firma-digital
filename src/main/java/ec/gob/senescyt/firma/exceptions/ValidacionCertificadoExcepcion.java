package ec.gob.senescyt.firma.exceptions;

import java.security.GeneralSecurityException;

public class ValidacionCertificadoExcepcion extends Throwable {
    public ValidacionCertificadoExcepcion(String mensaje, GeneralSecurityException e) {
        super(mensaje, e);
    }
}
