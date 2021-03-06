package ec.gob.senescyt.firma.resources;

import ec.gob.senescyt.firma.core.DocumentoFirmado;
import ec.gob.senescyt.firma.exceptions.AlmacenLlavesExcepcion;
import ec.gob.senescyt.firma.exceptions.FirmaDigitalExcepcion;
import ec.gob.senescyt.firma.services.ServicioFirmaDigital;
import ec.gob.senescyt.sniese.commons.core.InformacionFirma;
import ec.gob.senescyt.sniese.commons.security.PrincipalProvider;
import io.dropwizard.hibernate.UnitOfWork;

import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import static org.eclipse.jetty.http.HttpStatus.BAD_REQUEST_400;
import static org.eclipse.jetty.http.HttpStatus.CREATED_201;
import static org.eclipse.jetty.http.HttpStatus.NOT_FOUND_404;
import static org.eclipse.jetty.http.HttpStatus.OK_200;

@Path("/firmaDigital")
@Produces(MediaType.APPLICATION_JSON)
public class FirmaDigitalResource {
    private ServicioFirmaDigital servicioFirmaDigital;
    private PrincipalProvider principalProvider;

    public FirmaDigitalResource(ServicioFirmaDigital servicioFirmaDigital, PrincipalProvider principalProvider) {
        this.servicioFirmaDigital = servicioFirmaDigital;
        this.principalProvider = principalProvider;
    }

    @POST
    @UnitOfWork
    public Response crearFirmaDigital(InformacionFirma informacionFirma) throws FirmaDigitalExcepcion {
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

    @POST
    @Path("credenciales/validar")
    @UnitOfWork
    public Response validarCredenciales(String contrasenia) throws AlmacenLlavesExcepcion {
        boolean sonCredencialesValidas = servicioFirmaDigital.validarCredencialesFirma(
                principalProvider.obtenerUsuario().getNombreUsuario(), contrasenia);
        int codigoRespuesta = sonCredencialesValidas ? OK_200 : NOT_FOUND_404;
        return Response.status(codigoRespuesta).build();
    }
}
