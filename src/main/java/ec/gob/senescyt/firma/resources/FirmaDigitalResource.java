package ec.gob.senescyt.firma.resources;

import ec.gob.senescyt.firma.core.DocumentoFirmado;
import ec.gob.senescyt.firma.core.InformacionFirma;
import ec.gob.senescyt.firma.services.ServicioFirmaDigital;
import ec.gob.senescyt.microservicios.commons.filters.RecursoSeguro;
import ec.gob.senescyt.microservicios.commons.security.PrincipalProvider;

import javax.ws.rs.core.Response;

import static org.eclipse.jetty.http.HttpStatus.CREATED_201;

@RecursoSeguro
public class FirmaDigitalResource {
    private ServicioFirmaDigital servicioFirmaDigital;
    private PrincipalProvider principalProvider;

    public FirmaDigitalResource(ServicioFirmaDigital servicioFirmaDigital, PrincipalProvider principalProvider) {
        this.servicioFirmaDigital = servicioFirmaDigital;
        this.principalProvider = principalProvider;
    }

    public Response crearFirmaDigital(InformacionFirma informacionFirma) {
        String nombreUsuario = principalProvider.obtenerUsuario().getNombreUsuario();
        informacionFirma.setNombreUsuario(nombreUsuario);
        DocumentoFirmado documentoFirmado = servicioFirmaDigital.firmar(informacionFirma);
        return Response.status(CREATED_201).entity(documentoFirmado).build();
    }
}
