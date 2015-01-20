package ec.gob.senescyt.firma.exceptions.mappers;


import ec.gob.senescyt.sniese.commons.core.EntidadBase;

public class Errores extends EntidadBase {
    private String errors;

    protected Errores() {
        // PMD
    }

    public Errores(String errores) {
        this.errors = errores;
    }

    public String getErrors() {
        return errors;
    }
}