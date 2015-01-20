package ec.gob.senescyt.firma.services;

import ec.gob.senescyt.firma.core.ConfiguracionFirma;
import ec.gob.senescyt.firma.core.DocumentoFirmado;
import ec.gob.senescyt.firma.dao.ConfiguracionFirmaDAO;
import ec.gob.senescyt.firma.dao.DocumentoFirmadoDAO;
import ec.gob.senescyt.firma.exceptions.AlmacenLlavesExcepcion;
import ec.gob.senescyt.firma.exceptions.FirmaDigitalExcepcion;
import ec.gob.senescyt.firma.security.FirmaDigital;
import ec.gob.senescyt.sniese.commons.core.InformacionFirma;

import java.util.Base64;
import java.util.Optional;

public class ServicioFirmaDigital {
    private final FirmaDigital firmaDigital;
    private final ConfiguracionFirmaDAO configuracionFirmaDAO;
    private final DocumentoFirmadoDAO documentoFirmadoDAO;

    public ServicioFirmaDigital(FirmaDigital firmaDigital,
                                ConfiguracionFirmaDAO configuracionFirmaDAO,
                                DocumentoFirmadoDAO documentoFirmadoDAO) {
        this.firmaDigital = firmaDigital;
        this.configuracionFirmaDAO = configuracionFirmaDAO;
        this.documentoFirmadoDAO = documentoFirmadoDAO;
    }

    public DocumentoFirmado firmar(InformacionFirma informacionFirma) throws FirmaDigitalExcepcion {
        ConfiguracionFirma configuracionFirma = configuracionFirmaDAO.obtenerPorUsuario(informacionFirma.getNombreUsuario()).get();

        byte[] resultadoFirma = firmaDigital.firmar(informacionFirma.getTextoAFirmar(),
                                                       configuracionFirma.getCaminoArchivo(),
                                                       informacionFirma.getContrasenia());

        String resultadoFirmaDigital = Base64.getEncoder().encodeToString(resultadoFirma);
        DocumentoFirmado documentoFirmado = new DocumentoFirmado(informacionFirma.getTextoAFirmar(), resultadoFirmaDigital, configuracionFirma);
        return documentoFirmadoDAO.guardar(documentoFirmado);
    }

    public boolean validarCredencialesFirma(String nombreUsuario, String contrasenia) throws AlmacenLlavesExcepcion {
        Optional<ConfiguracionFirma> configuracionFirma = configuracionFirmaDAO.obtenerPorUsuario(nombreUsuario);
        return configuracionFirma.isPresent() &&
               firmaDigital.existeLlavePrivadaParaFirmar(configuracionFirma.get().getCaminoArchivo(), contrasenia);
    }
}
