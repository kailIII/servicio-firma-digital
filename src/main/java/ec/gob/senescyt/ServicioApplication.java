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
import ec.gob.senescyt.firma.security.FirmaDigitalProxy;
import ec.gob.senescyt.firma.security.certs.CertificadosRaizFactory;
import ec.gob.senescyt.firma.security.certs.FirmaDigitalProxyConfiguracion;
import ec.gob.senescyt.firma.services.ServicioFirmaDigital;
import ec.gob.senescyt.sniese.commons.MicroservicioAplicacion;
import ec.gob.senescyt.sniese.commons.security.PrincipalProvider;
import ec.gob.senescyt.sniese.commons.security.PrincipalProviderImpl;
import io.dropwizard.db.DataSourceFactory;
import io.dropwizard.hibernate.HibernateBundle;
import io.dropwizard.jersey.setup.JerseyEnvironment;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;
import org.hibernate.SessionFactory;

import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.util.logging.Level;
import java.util.logging.Logger;

@SuppressWarnings("PMD.ExcessiveImports")
public class ServicioApplication extends MicroservicioAplicacion<ServicioConfiguration> {

    private final HibernateBundle<ServicioConfiguration> hibernate = new HibernateBundle<ServicioConfiguration>(
            ConfiguracionFirma.class, DocumentoFirmado.class) {
        @Override
        public DataSourceFactory getDataSourceFactory(ServicioConfiguration configuration) {
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
    public void inicializar(Bootstrap<ServicioConfiguration> bootstrap) {
        // no hace nada
    }

    @Override
    public void ejecutar(ServicioConfiguration configuracion, Environment environment) {
        JerseyEnvironment jerseyEnvironment = environment.jersey();
        this.defaultSchema = configuracion.getDefaultSchema();
        configurarRecursoFirmaDigital(configuracion, jerseyEnvironment);
        jerseyEnvironment.register(new FirmaDigitalExcepcionGeneralExceptionMapper());
    }

    private void configurarRecursoFirmaDigital(ServicioConfiguration configuracion, JerseyEnvironment jerseyEnvironment) {
        AliasProvider aliasProvider = new AliasProvider();
        FirmaDigital firmaDigital = getFirmaDigital(configuracion, aliasProvider);
        ConfiguracionFirmaDAO configuracionFirmaDAO = new ConfiguracionFirmaDAO(getSessionFactory(), defaultSchema);
        DocumentoFirmadoDAO documentoFirmadoDAO = new DocumentoFirmadoDAO(getSessionFactory(), defaultSchema);
        ServicioFirmaDigital servicioFirmaDigital = new ServicioFirmaDigital(firmaDigital, configuracionFirmaDAO, documentoFirmadoDAO);
        PrincipalProvider principalProvider = new PrincipalProviderImpl();
        FirmaDigitalResource firmaDigitalResource = new FirmaDigitalResource(servicioFirmaDigital, principalProvider);
        jerseyEnvironment.register(firmaDigitalResource);
    }

    private FirmaDigital getFirmaDigital(ServicioConfiguration configuracion, AliasProvider aliasProvider) {
        try {
            AlmacenLlavesProvider almacenLlavesProvider = new AlmacenLlavesPkcs12Provider(aliasProvider);
            FirmaDigitalImpl firmaDigital = new FirmaDigitalImpl(almacenLlavesProvider);
            if (configuracion.isActivarProduccion()) {
                return new FirmaDigitalProxy(firmaDigital, new CertificadosRaizFactory(), new AlmacenLlavesPkcs12Provider(aliasProvider), new FirmaDigitalProxyConfiguracion());
            }
            return firmaDigital;
        } catch (AlmacenLlavesExcepcion | NoSuchAlgorithmException | CertificateException e) {
            LOGGER.log(Level.FINEST, "Error al crear el almacen de llaves y la firma digital", e);
        }
        return null;
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

