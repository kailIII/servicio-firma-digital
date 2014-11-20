package ec.gob.senescyt;

import com.google.common.annotations.VisibleForTesting;
import ec.gob.senescyt.commons.bundles.DBMigrationsBundle;
import ec.gob.senescyt.ejemplos.core.Ejemplo;
import ec.gob.senescyt.ejemplos.resources.EjemploResource;
import ec.gob.senescyt.microservicios.commons.MicroservicioAplicacion;
import io.dropwizard.db.DataSourceFactory;
import io.dropwizard.hibernate.HibernateBundle;
import io.dropwizard.jersey.setup.JerseyEnvironment;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;
import org.hibernate.SessionFactory;

public class ServicioApplication extends MicroservicioAplicacion<ServicioConfiguration> {

    private final DBMigrationsBundle flywayBundle = new DBMigrationsBundle();
    private final HibernateBundle<ServicioConfiguration> hibernate = new HibernateBundle<ServicioConfiguration>(Ejemplo.class) {
        @Override
        public DataSourceFactory getDataSourceFactory(ServicioConfiguration configuration) {
            return configuration.getDataSourceFactory();
        }
    };

    @SuppressWarnings("PMD.SignatureDeclareThrowsException")
    public static void main(String[] args) throws Exception {
        new ServicioApplication().run(args);
    }

    @Override
    public String getName() {
        return "servicio";
    }

    @Override
    public void inicializar(Bootstrap<ServicioConfiguration> bootstrap) {
        bootstrap.addBundle(flywayBundle);
    }

    @Override
    public void ejecutar(ServicioConfiguration servicioConfiguration, Environment environment) {
        JerseyEnvironment jerseyEnvironment = environment.jersey();

        EjemploResource ejemploResource = new EjemploResource();
        jerseyEnvironment.register(ejemploResource);
    }

    @Override
    protected HibernateBundle<ServicioConfiguration> getHibernate() {
        return hibernate;
    }

    @VisibleForTesting
    public SessionFactory getSessionFactory() {
        return hibernate.getSessionFactory();
    }
}

