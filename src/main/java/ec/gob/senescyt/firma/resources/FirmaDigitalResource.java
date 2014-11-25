package ec.gob.senescyt.firma.resources;

import ec.gob.senescyt.firma.core.DocumentoFirmado;
import ec.gob.senescyt.firma.services.ServicioFirmaDigital;
import ec.gob.senescyt.microservicios.commons.core.InformacionFirma;
import ec.gob.senescyt.microservicios.commons.filters.RecursoSeguro;
import ec.gob.senescyt.microservicios.commons.security.PrincipalProvider;

import javax.ws.rs.core.Response;
import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.SignatureException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;

import static org.eclipse.jetty.http.HttpStatus.BAD_REQUEST_400;
import static org.eclipse.jetty.http.HttpStatus.CREATED_201;

@RecursoSeguro
public class FirmaDigitalResource {
    private ServicioFirmaDigital servicioFirmaDigital;
    private PrincipalProvider principalProvider;

    public FirmaDigitalResource(ServicioFirmaDigital servicioFirmaDigital, PrincipalProvider principalProvider) {
        this.servicioFirmaDigital = servicioFirmaDigital;
        this.principalProvider = principalProvider;
    }

    public Response crearFirmaDigital(InformacionFirma informacionFirma) throws CertificateException, UnrecoverableKeyException, NoSuchAlgorithmException, IOException, KeyStoreException, SignatureException, InvalidKeyException {
        if (coincideUsuarioConLaInformacion(informacionFirma)) {
            DocumentoFirmado documentoFirmado = servicioFirmaDigital.firmar(informacionFirma);
            return Response.status(CREATED_201).entity(documentoFirmado).build();
        }
        return Response.status(BAD_REQUEST_400).build();
    }

    private boolean coincideUsuarioConLaInformacion(InformacionFirma informacionFirma) {
        String nombreUsuario = principalProvider.obtenerUsuario().getNombreUsuario();
        return nombreUsuario.equals(informacionFirma.getNombreUsuario());
    }

}
