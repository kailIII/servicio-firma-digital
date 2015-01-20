package ec.gob.senescyt;

import com.google.common.annotations.VisibleForTesting;
import ec.gob.senescyt.firma.core.ConfiguracionFirma;
import ec.gob.senescyt.firma.core.DocumentoFirmado;
import ec.gob.senescyt.firma.dao.ConfiguracionFirmaDAO;
import ec.gob.senescyt.firma.dao.DocumentoFirmadoDAO;
import ec.gob.senescyt.firma.exceptions.AlmacenLlavesExcepcion;
import ec.gob.senescyt.firma.exceptions.mappers.FirmaDigitalExcepcionGeneralExceptionMapper;
import ec.gob.senescyt.firma.resources.FirmaDigitalResource;
import ec.gob.senescyt.firma.security.AliasProvider;
import ec.gob.senescyt.firma.security.AlmacenLlavesPkcs12Provider;
import ec.gob.senescyt.firma.security.AlmacenLlavesProvider;
import ec.gob.senescyt.firma.security.FirmaDigital;
import ec.gob.senescyt.firma.security.FirmaDigitalImpl;
import ec.gob.senescyt.firma.services.ServicioFirmaDigital;
import ec.gob.senescyt.sniese.commons.MicroservicioAplicacion;
import ec.gob.senescyt.sniese.commons.MicroservicioConfiguracion;
import ec.gob.senescyt.sniese.commons.security.PrincipalProvider;
import ec.gob.senescyt.sniese.commons.security.PrincipalProviderImpl;
import io.dropwizard.db.DataSourceFactory;
import io.dropwizard.hibernate.HibernateBundle;
import io.dropwizard.jersey.setup.JerseyEnvironment;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;
import org.hibernate.SessionFactory;

import java.security.NoSuchAlgorithmException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ServicioApplication extends MicroservicioAplicacion<MicroservicioConfiguracion> {

    private final HibernateBundle<MicroservicioConfiguracion> hibernate = new HibernateBundle<MicroservicioConfiguracion>(
            ConfiguracionFirma.class, DocumentoFirmado.class) {
        @Override
        public DataSourceFactory getDataSourceFactory(MicroservicioConfiguracion configuration) {
            return configuration.getDataSourceFactory();
        }
    };
    private String defaultSchema;
    private static final Logger LOGGER = Logger.getLogger(ServicioApplication.class.getName());

    @SuppressWarnings("PMD.SignatureDeclareThrowsException")
    public static void main(String[] args) throws Exception {
        new ServicioApplication().run(args);
    }

    @Override
    public String getName() {
        return "servicio";
    }

    @Override
    public void inicializar(Bootstrap<MicroservicioConfiguracion> bootstrap) {
        // no hace nada
    }

    @Override
    public void ejecutar(MicroservicioConfiguracion servicioConfiguration, Environment environment) {
        JerseyEnvironment jerseyEnvironment = environment.jersey();
        this.defaultSchema = servicioConfiguration.getDefaultSchema();
        configurarRecursoFirmaDigital(jerseyEnvironment);
        jerseyEnvironment.register(new FirmaDigitalExcepcionGeneralExceptionMapper());
    }

    private void configurarRecursoFirmaDigital(JerseyEnvironment jerseyEnvironment) {
        AliasProvider aliasProvider = new AliasProvider();
        FirmaDigital firmaDigital = null;
        try {
            AlmacenLlavesProvider almacenLlavesProvider = new AlmacenLlavesPkcs12Provider(aliasProvider);
            firmaDigital = new FirmaDigitalImpl(almacenLlavesProvider);
        } catch (AlmacenLlavesExcepcion | NoSuchAlgorithmException e) {
            LOGGER.log(Level.FINEST, "Error al crear el almacen de llaves y la firma digital", e);
        }
        ConfiguracionFirmaDAO configuracionFirmaDAO = new ConfiguracionFirmaDAO(getSessionFactory(), defaultSchema);
        DocumentoFirmadoDAO documentoFirmadoDAO = new DocumentoFirmadoDAO(getSessionFactory(), defaultSchema);
        ServicioFirmaDigital servicioFirmaDigital = new ServicioFirmaDigital(firmaDigital, configuracionFirmaDAO, documentoFirmadoDAO);
        PrincipalProvider principalProvider = new PrincipalProviderImpl();
        FirmaDigitalResource firmaDigitalResource = new FirmaDigitalResource(servicioFirmaDigital, principalProvider);
        jerseyEnvironment.register(firmaDigitalResource);
    }

    @Override
    protected HibernateBundle<MicroservicioConfiguracion> getHibernate() {
        return hibernate;
    }

    @VisibleForTesting
    public SessionFactory getSessionFactory() {
        return hibernate.getSessionFactory();
    }
}

