package ec.gob.senescyt.firma.services;

import ec.gob.senescyt.firma.core.ConfiguracionFirma;
import ec.gob.senescyt.firma.core.DocumentoFirmado;
import ec.gob.senescyt.firma.dao.ConfiguracionFirmaDAO;
import ec.gob.senescyt.firma.dao.DocumentoFirmadoDAO;
import ec.gob.senescyt.firma.security.FirmaDigital;
import ec.gob.senescyt.microservicios.commons.core.InformacionFirma;

import java.io.IOException;
import java.nio.charset.Charset;
import java.security.InvalidKeyException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.SignatureException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;

public class ServicioFirmaDigital {
    private final FirmaDigital firmaDigital;
    private ConfiguracionFirmaDAO configuracionFirmaDAO;
    private final DocumentoFirmadoDAO documentoFirmadoDAO;

    public ServicioFirmaDigital(FirmaDigital firmaDigital,
                                ConfiguracionFirmaDAO configuracionFirmaDAO,
                                DocumentoFirmadoDAO documentoFirmadoDAO) {
        this.firmaDigital = firmaDigital;
        this.configuracionFirmaDAO = configuracionFirmaDAO;
        this.documentoFirmadoDAO = documentoFirmadoDAO;
    }

    public DocumentoFirmado firmar(InformacionFirma informacionFirma) throws CertificateException, UnrecoverableKeyException, NoSuchAlgorithmException, IOException, KeyStoreException, SignatureException, InvalidKeyException {
        ConfiguracionFirma configuracionFirma = configuracionFirmaDAO.obtenerPorUsuario(informacionFirma.getNombreUsuario());

        byte[] firmaDigital = this.firmaDigital.firmar(informacionFirma.getTextoAFirmar(),
                                                       configuracionFirma.getCaminoArchivo(),
                                                       informacionFirma.getContrasenia());

        String resultadoFirmaDigital = new String(firmaDigital, Charset.forName("UTF-8"));
        DocumentoFirmado documentoFirmado = new DocumentoFirmado(resultadoFirmaDigital, configuracionFirma);
        return documentoFirmadoDAO.guardar(documentoFirmado);
    }
}
