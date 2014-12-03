package ec.gob.senescyt.firma.security;

import ec.gob.senescyt.firma.exceptions.AlmacenLlavesExcepcion;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.io.FileInputStream;
import java.io.IOException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

import static org.apache.commons.lang.RandomStringUtils.randomAlphabetic;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.MockitoAnnotations.initMocks;
import static org.powermock.api.mockito.PowerMockito.doThrow;
import static org.powermock.api.mockito.PowerMockito.mock;
import static org.powermock.api.mockito.PowerMockito.mockStatic;
import static org.powermock.api.mockito.PowerMockito.when;
import static org.powermock.api.mockito.PowerMockito.whenNew;

@RunWith(PowerMockRunner.class)
@PrepareForTest({KeyStore.class, AlmacenLlavesPkcs12Provider.class})
public class AlmacenLlavesPkcs12ProviderTest {

    @Rule
    public final ExpectedException excepcionEsperada = ExpectedException.none();

    private static final String CONTRASENIA = "Password#1";
    KeyStore almacenLlaves;
    private AlmacenLlavesProvider proveedorAlmacen;
    @Mock
    AliasProvider aliasProvider;
    @Mock
    private FileInputStream archivo;
    private String aliasEsperado;
    private String caminoArchivo;

    @Before
    public void setUp() throws Exception {
        initMocks(this);
        aliasEsperado = randomAlphabetic(10);
        mockStatic(KeyStore.class);
        almacenLlaves = mock(KeyStore.class);
        when(KeyStore.getInstance("PKCS12")).thenReturn(almacenLlaves);
        proveedorAlmacen = new AlmacenLlavesPkcs12Provider(aliasProvider);
        caminoArchivo = randomAlphabetic(10);
        whenNew(FileInputStream.class).withArguments(caminoArchivo).thenReturn(archivo);
    }

    @Test
    public void debeCerrarElArchivoDespuesDeLaCarga()
            throws CertificateException, NoSuchAlgorithmException, IOException, UnrecoverableKeyException, KeyStoreException, AlmacenLlavesExcepcion {
        proveedorAlmacen.obtenerLlavePrivadaParaFirmar(caminoArchivo, CONTRASENIA);
        verify(archivo, times(1)).close();
    }

    @Test
    public void debeCerrarElArchivoSiOcurreUnaExcepcionDuranteLaCarga() throws UnrecoverableKeyException, CertificateException, NoSuchAlgorithmException, KeyStoreException, IOException {
        doThrow(new NoSuchAlgorithmException()).when(almacenLlaves).load(archivo, CONTRASENIA.toCharArray());
        try {
            proveedorAlmacen.obtenerLlavePrivadaParaFirmar(caminoArchivo, CONTRASENIA);
        }
        catch (AlmacenLlavesExcepcion e) {
            verify(archivo, times(1)).close();
        }
    }

    @Test
    @SuppressWarnings("PMD.SignatureDeclareThrowsException")
    public void noDebeCerrarElArchivoSiEsteEsNull() throws Exception {
        whenNew(FileInputStream.class).withArguments(caminoArchivo).thenReturn(null);
        proveedorAlmacen.obtenerLlavePrivadaParaFirmar(caminoArchivo, CONTRASENIA);
        verify(archivo, never()).close();
    }

    @Test
    public void debeRetornarLaLlavePrivadaAPartirDeUnArchivoPkcs12() throws KeyStoreException, UnrecoverableKeyException, NoSuchAlgorithmException, AlmacenLlavesExcepcion {
        Mockito.when(aliasProvider.obtenerPrimerAliasParaFirmar(almacenLlaves)).thenReturn(aliasEsperado);
        PrivateKey llavePrivadaEsperada = Mockito.mock(PrivateKey.class);
        when(almacenLlaves.getKey(aliasEsperado, CONTRASENIA.toCharArray())).thenReturn(llavePrivadaEsperada);
        PrivateKey llavePrivada = proveedorAlmacen.obtenerLlavePrivadaParaFirmar(caminoArchivo, CONTRASENIA);
        assertThat(llavePrivada, is(llavePrivadaEsperada));
    }

    @Test
    public void debeObtenerElCertificadoAPartirDelArchivoPkcs12() throws KeyStoreException, AlmacenLlavesExcepcion {
        Mockito.when(aliasProvider.obtenerPrimerAliasParaFirmar(almacenLlaves)).thenReturn(aliasEsperado);
        X509Certificate certificadoEsperado = Mockito.mock(X509Certificate.class);
        when(almacenLlaves.getCertificate(aliasEsperado)).thenReturn(certificadoEsperado);
        X509Certificate certificadoActual = proveedorAlmacen.obtenerCertificadoDeLaFirma(caminoArchivo, CONTRASENIA);
        assertThat(certificadoActual, is(certificadoEsperado));
    }

    @Test
    public void debeLanzarUnaExcepcionDeTipoAlmacenDeLlavesCuandoOcurreUnError() throws KeyStoreException, AlmacenLlavesExcepcion {
        KeyStoreException excepcionEsperada = new KeyStoreException();
        Mockito.when(aliasProvider.obtenerPrimerAliasParaFirmar(almacenLlaves)).thenThrow(excepcionEsperada);
        try {
            proveedorAlmacen.obtenerCertificadoDeLaFirma(caminoArchivo, CONTRASENIA);
        } catch (AlmacenLlavesExcepcion almacenLlavesExcepcion) {
            assertThat(almacenLlavesExcepcion.getMessage(), is("Error obteniendo el certificado digital"));
            assertThat(almacenLlavesExcepcion.getCause(), is(excepcionEsperada));
        }
    }
}