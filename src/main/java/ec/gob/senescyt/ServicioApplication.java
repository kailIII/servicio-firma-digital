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
import ec.gob.senescyt.sniese.commons.applications.AplicacionPersistente;
import ec.gob.senescyt.sniese.commons.applications.AplicacionSegura;
import ec.gob.senescyt.sniese.commons.applications.AplicacionSniese;
import ec.gob.senescyt.sniese.commons.security.PrincipalProvider;
import ec.gob.senescyt.sniese.commons.security.PrincipalProviderImpl;
import io.dropwizard.Application;
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
public class ServicioApplication extends Application<ServicioConfiguration> {

    private final AplicacionSniese aplicacion;
    private final HibernateBundle<ServicioConfiguration> hibernate = new HibernateBundle<ServicioConfiguration>(
            ConfiguracionFirma.class, DocumentoFirmado.class) {
        @Override
        public DataSourceFactory getDataSourceFactory(ServicioConfiguration configuration) {
            return configuration.getConfiguracionPersistente().getDatabase();
        }
    };
    private String defaultSchema;
    private static final Logger LOGGER = Logger.getLogger(ServicioApplication.class.getName());

    public ServicioApplication() {
        aplicacion = new AplicacionSniese(
                new AplicacionPersistente(
                        new AplicacionSegura(
                                null,
                                new PrincipalProviderImpl()),
                        hibernate));
    }

    @SuppressWarnings("PMD.SignatureDeclareThrowsException")
    public static void main(String[] args) throws Exception {
        new ServicioApplication().run(args);
    }

    @Override
    public String getName() {
        return "servicio-firma-digital";
    }

    @Override
    public void initialize(Bootstrap<ServicioConfiguration> bootstrap) {
        aplicacion.initialize(bootstrap);
    }

    @Override
    public void run(ServicioConfiguration configuracion, Environment ambiente) {
        JerseyEnvironment jerseyEnvironment = ambiente.jersey();
        this.defaultSchema = configuracion.getConfiguracionPersistente().getDefaultSchema();
        configurarRecursoFirmaDigital(configuracion, jerseyEnvironment);
        jerseyEnvironment.register(new FirmaDigitalExcepcionGeneralExceptionMapper());
        aplicacion.run(configuracion, ambiente);
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

    @VisibleForTesting
    public SessionFactory getSessionFactory() {
        return hibernate.getSessionFactory();
    }
}

