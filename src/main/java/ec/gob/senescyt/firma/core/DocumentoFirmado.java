package ec.gob.senescyt.firma.core;

import com.fasterxml.jackson.annotation.JsonIgnore;
import ec.gob.senescyt.sniese.commons.core.EntidadBase;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

@Entity
@Table(name = "documentos_firmados")
public class DocumentoFirmado extends EntidadBase {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Transient
    private String textoAFirmar;
    private String firmaDigital;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "configuracion_firma_id")
    private ConfiguracionFirma configuracionFirma;

    protected DocumentoFirmado() {
        // PMD
    }

    public DocumentoFirmado(String textoAFirmar, String firmaDigital, ConfiguracionFirma configuracionFirma) {
        this.textoAFirmar = textoAFirmar;
        this.firmaDigital = firmaDigital;
        this.configuracionFirma = configuracionFirma;
    }

    public DocumentoFirmado(long idDocumentoFirmado, String textoAFirmar, String firmaDigital, ConfiguracionFirma configuracionFirma) {
        this.id = idDocumentoFirmado;
        this.textoAFirmar = textoAFirmar;
        this.firmaDigital = firmaDigital;
        this.configuracionFirma = configuracionFirma;
    }

    public long getId() {
        return id;
    }

    public String getFirmaDigital() {
        return firmaDigital;
    }

    public ConfiguracionFirma getConfiguracionFirma() {
        return configuracionFirma;
    }

    public String getTextoAFirmar() {
        return textoAFirmar;
    }
}
