package ec.gob.senescyt.firma.dao;

import ec.gob.senescyt.firma.core.ConfiguracionFirma;
import ec.gob.senescyt.microservicios.commons.dao.AbstractServicioDAO;
import org.hibernate.SessionFactory;

public class ConfiguracionFirmaDAO extends AbstractServicioDAO<ConfiguracionFirma> {

    public ConfiguracionFirmaDAO(SessionFactory sessionFactory, String defaultSchema) {
        super(sessionFactory, defaultSchema);
    }

    public ConfiguracionFirma obtenerPorUsuario(String nombreUsuario) {
        return null;
    }
}
