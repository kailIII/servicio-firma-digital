package ec.gob.senescyt.firma.exceptions;

public class ValidacionCertificadoExcepcion extends FirmaDigitalExcepcion {

    private static final String ERROR_VALIDACION_CERTIFICADO = "Error de validación del certificado";

    public ValidacionCertificadoExcepcion(Throwable excepcionHija) {
        super(ERROR_VALIDACION_CERTIFICADO, excepcionHija);
    }
}
