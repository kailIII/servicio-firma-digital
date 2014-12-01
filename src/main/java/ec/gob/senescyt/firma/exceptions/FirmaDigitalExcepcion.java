package ec.gob.senescyt.firma.exceptions;

public class FirmaDigitalExcepcion extends FirmaDigitalExcepcionGeneral {

    private static final String ERROR_AL_REALIZAR_LA_FIRMA_DIGITAL = "Error al realizar la firma digital";

    public FirmaDigitalExcepcion(String message, Throwable cause) {
        super(message, cause);
    }

    public FirmaDigitalExcepcion(Throwable excepcionHija) {
        super(ERROR_AL_REALIZAR_LA_FIRMA_DIGITAL, excepcionHija);
    }
}
