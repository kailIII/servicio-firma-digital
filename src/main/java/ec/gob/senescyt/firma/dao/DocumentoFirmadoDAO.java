package ec.gob.senescyt.firma.dao;

import ec.gob.senescyt.firma.core.DocumentoFirmado;
import ec.gob.senescyt.sniese.commons.dao.AbstractServicioDAO;
import org.hibernate.SessionFactory;

public class DocumentoFirmadoDAO extends AbstractServicioDAO<DocumentoFirmado> {

    public DocumentoFirmadoDAO(SessionFactory sessionFactory, String defaultSchema) {
        super(sessionFactory, defaultSchema);
    }
}
