package ec.gob.senescyt.firma.core;

import ec.gob.senescyt.microservicios.commons.core.EntidadBase;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;


@Entity
@Table(name = "configuraciones_firmas")
public class ConfiguracionFirma extends EntidadBase{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @SuppressWarnings("PMD.UnusedPrivateField")
    private long id;

    private String caminoArchivo;
    private String nombreUsuario;

    private ConfiguracionFirma() {
    }

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
