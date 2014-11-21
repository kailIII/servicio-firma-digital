package ec.gob.senescyt.firma.core;

public class InformacionFirma {
    private String nombreUsuario;
    private String textoAFirmar;
    private String contrasenia;

    public InformacionFirma(String textoAFirmar,  String nombreUsuario, String contrasenia) {
        this.textoAFirmar = textoAFirmar;
        this.nombreUsuario = nombreUsuario;
        this.contrasenia = contrasenia;
    }

    public String getNombreUsuario() {
        return nombreUsuario;
    }

    public void setNombreUsuario(String nombreUsuario) {
        this.nombreUsuario = nombreUsuario;
    }

    public String getTextoAFirmar() {
        return textoAFirmar;
    }

    public String getContrasenia() {
        return contrasenia;
    }
}
