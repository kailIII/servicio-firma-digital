package ec.gob.senescyt.firma.core;

import ec.gob.senescyt.microservicios.commons.core.EntidadBase;

public class ConfiguracionFirma extends EntidadBase{
    private String caminoArchivo;
    private String nombreUsuario;

    public ConfiguracionFirma(String nombreUsuario, String caminoArchivo) {
        this.nombreUsuario = nombreUsuario;
        this.caminoArchivo = caminoArchivo;
    }

    public String getCaminoArchivo() {
        return caminoArchivo;
    }

    public String getNombreUsuario() {
        return nombreUsuario;
    }
}
