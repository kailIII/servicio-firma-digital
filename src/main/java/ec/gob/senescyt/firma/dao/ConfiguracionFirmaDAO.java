package ec.gob.senescyt.firma.dao;

import ec.gob.senescyt.firma.core.ConfiguracionFirma;
import ec.gob.senescyt.sniese.commons.dao.AbstractServicioDAO;
import org.hibernate.Criteria;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Restrictions;

import java.util.Optional;

public class ConfiguracionFirmaDAO extends AbstractServicioDAO<ConfiguracionFirma> {

    public ConfiguracionFirmaDAO(SessionFactory sessionFactory, String defaultSchema) {
        super(sessionFactory, defaultSchema);
    }

    public Optional<ConfiguracionFirma> obtenerPorUsuario(String nombreUsuario) {
        Criteria criteria = obtenerCriteria();
        criteria.add(Restrictions.eq("nombreUsuario", nombreUsuario));
        criteria.add(Restrictions.eq("activa", true));
        criteria.setMaxResults(1);
        return criteria.list().stream().findFirst();
    }
}
