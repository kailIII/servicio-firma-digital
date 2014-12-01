package ec.gob.senescyt.firma.exceptions.mappers;


import ec.gob.senescyt.firma.exceptions.FirmaDigitalExcepcionGeneral;

import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;

import static org.eclipse.jetty.http.HttpStatus.INTERNAL_SERVER_ERROR_500;

public class FirmaDigitalExcepcionGeneralExceptionMapper implements ExceptionMapper<FirmaDigitalExcepcionGeneral> {
    @Override
    public Response toResponse(FirmaDigitalExcepcionGeneral exception) {
        return Response.status(INTERNAL_SERVER_ERROR_500)
                .entity(new Errores(exception.getMessage()))
                .build();
    }
}
