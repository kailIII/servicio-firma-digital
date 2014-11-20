package ec.gob.senescyt;

import com.fasterxml.jackson.annotation.JsonProperty;
import ec.gob.senescyt.microservicios.commons.MicroservicioConfiguracion;
import io.dropwizard.db.DataSourceFactory;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

public class ServicioConfiguration extends MicroservicioConfiguracion {

    @Valid
    @NotNull
    @JsonProperty("database")
    private final DataSourceFactory database = new DataSourceFactory();

    public DataSourceFactory getDataSourceFactory() {
        return database;
    }

    public String getDefaultSchema() {
        return database.getProperties().get("hibernate.default_schema");
    }
}
