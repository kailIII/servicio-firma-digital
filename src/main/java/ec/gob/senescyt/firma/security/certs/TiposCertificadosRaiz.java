package ec.gob.senescyt.firma.security.certs;

public enum TiposCertificadosRaiz {
    BCE_RAIZ("certBceRaiz.cer"),
    BCE_SUBORDINADO("certBceSubordinado.cer");

    private String nombreArchivo;

    TiposCertificadosRaiz(String nombreArchivo) {
        this.nombreArchivo = nombreArchivo;
    }

    public String getNombreArchivo() {
        return nombreArchivo;
    }
}
