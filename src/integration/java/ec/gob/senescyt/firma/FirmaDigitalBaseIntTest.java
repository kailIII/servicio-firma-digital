package ec.gob.senescyt.firma;

import ec.gob.senescyt.ServicioApplication;
import ec.gob.senescyt.sniese.commons.tests.RecursoSeguroIntTest;
import org.hibernate.SessionFactory;

public class FirmaDigitalBaseIntTest extends RecursoSeguroIntTest {

    public FirmaDigitalBaseIntTest() {
        super(ServicioApplication.class);
    }

    @Override
    protected SessionFactory getSessionFactory() {
        return ((ServicioApplication) getRule().getApplication()).getSessionFactory();
    }
}
