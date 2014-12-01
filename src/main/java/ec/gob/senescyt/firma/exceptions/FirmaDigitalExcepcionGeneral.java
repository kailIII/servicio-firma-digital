package ec.gob.senescyt.firma.exceptions;

public class FirmaDigitalExcepcionGeneral extends Exception {
    public FirmaDigitalExcepcionGeneral(String mensaje, Throwable excepcionHija) {
        super(mensaje, excepcionHija);
    }
}
