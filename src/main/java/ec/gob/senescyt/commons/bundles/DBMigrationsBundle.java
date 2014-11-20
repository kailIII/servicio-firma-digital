package ec.gob.senescyt.commons.bundles;

import ec.gob.senescyt.ServicioConfiguration;
import io.dropwizard.ConfiguredBundle;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;
import org.flywaydb.core.Flyway;

public class DBMigrationsBundle implements ConfiguredBundle<ServicioConfiguration> {
    @Override
    public void run(ServicioConfiguration configuration, Environment environment) {
        Flyway flyway = new Flyway();
        flyway.setDataSource(configuration.getDataSourceFactory().getUrl(), configuration.getDataSourceFactory().getUser(), configuration.getDataSourceFactory().getPassword());
        flyway.setSchemas(configuration.getDefaultSchema());
        flyway.migrate();
    }

    @Override
    public void initialize(Bootstrap<?> bootstrap) {
        // Do nothing.
    }
}
