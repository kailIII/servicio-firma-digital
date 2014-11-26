package ec.gob.senescyt.firma.core;

import com.fasterxml.jackson.annotation.JsonIgnore;
import ec.gob.senescyt.microservicios.commons.core.EntidadBase;

import javax.persistence.Table;

@Table(name = "documentos_firmados")
public class DocumentoFirmado extends EntidadBase {
    private String firmaDigital;

    @JsonIgnore
    private ConfiguracionFirma configuracionFirma;

    private DocumentoFirmado() {
    }

    public DocumentoFirmado(String firmaDigital, ConfiguracionFirma configuracionFirma) {
        this.firmaDigital = firmaDigital;
        this.configuracionFirma = configuracionFirma;
    }

    public String getFirmaDigital() {
        return firmaDigital;
    }

    public ConfiguracionFirma getConfiguracionFirma() {
        return configuracionFirma;
    }
}
