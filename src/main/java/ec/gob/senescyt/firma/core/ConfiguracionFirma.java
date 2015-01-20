package ec.gob.senescyt.firma.core;


import ec.gob.senescyt.sniese.commons.core.EntidadBase;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;


@Entity
@Table(name = "configuraciones_firmas")
public class ConfiguracionFirma extends EntidadBase {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    private String caminoArchivo;
    private String nombreUsuario;
    private boolean activa;

    protected ConfiguracionFirma() {
        // PMD
    }

    public ConfiguracionFirma(String nombreUsuario, String caminoArchivo, boolean activa) {
        this.nombreUsuario = nombreUsuario;
        this.caminoArchivo = caminoArchivo;
        this.activa = activa;
    }

    public ConfiguracionFirma(String nombreUsuario, String caminoArchivo) {
        this.nombreUsuario = nombreUsuario;
        this.caminoArchivo = caminoArchivo;
        this.activa = true;
    }

    public String getCaminoArchivo() {
        return caminoArchivo;
    }

    public String getNombreUsuario() {
        return nombreUsuario;
    }

    public boolean isActiva() {
        return activa;
    }

    public long getId() {
        return id;
    }
}
