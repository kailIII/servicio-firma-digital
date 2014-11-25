package ec.gob.senescyt.firma.core;

import ec.gob.senescyt.microservicios.commons.core.EntidadBase;

public class DocumentoFirmado extends EntidadBase {
    private String firmaDigital;
    private ConfiguracionFirma configuracionFirma;

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
