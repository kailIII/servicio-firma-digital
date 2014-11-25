package ec.gob.senescyt.firma.security;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;

import static org.apache.commons.lang.RandomStringUtils.randomAlphabetic;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.MockitoAnnotations.initMocks;
import static org.powermock.api.mockito.PowerMockito.doThrow;
import static org.powermock.api.mockito.PowerMockito.mock;
import static org.powermock.api.mockito.PowerMockito.mockStatic;
import static org.powermock.api.mockito.PowerMockito.when;

@RunWith(PowerMockRunner.class)
@PrepareForTest({KeyStore.class, AlmacenLlavesPkcs12Provider.class})
public class AlmacenLlavesPkcs12ProviderTest {

    private static final String CONTRASENIA = "Password#1";
    KeyStore almacenLlaves;
    private AlmacenLlavesPkcs12Provider proveedorAlmacen;
    private FileInputStream archivoPrueba;
    private InputStream archivoMock;
    @Mock
    AliasProvider aliasProvider;
    private String aliasEsperado;

    @Before
    public void setUp() throws KeyStoreException, FileNotFoundException {
        initMocks(this);
        aliasEsperado = randomAlphabetic(10);
        mockStatic(KeyStore.class);
        almacenLlaves = mock(KeyStore.class);
        when(KeyStore.getInstance("PKCS12")).thenReturn(almacenLlaves);
        ClassLoader systemClassLoader = ClassLoader.getSystemClassLoader();
        URL resource = systemClassLoader.getResource("test.p12");
        archivoPrueba = new FileInputStream(resource.getPath());
        archivoMock = Mockito.mock(InputStream.class);
        proveedorAlmacen = new AlmacenLlavesPkcs12Provider(aliasProvider);
    }

    @Test
    public void debeCerrarElArchivoDespuesDeLaCarga()
            throws CertificateException, NoSuchAlgorithmException, IOException, UnrecoverableKeyException, KeyStoreException {
        proveedorAlmacen.obtenerLlavePrivadaParaFirmar(archivoMock, CONTRASENIA);
        verify(archivoMock, times(1)).close();
    }

    @Test
    public void debeCerrarElArchivoSiOcurreUnaExcepcionDuranteLaCarga()
            throws UnrecoverableKeyException, CertificateException, NoSuchAlgorithmException, KeyStoreException, IOException {
        doThrow(new NoSuchAlgorithmException()).when(almacenLlaves).load(archivoMock, CONTRASENIA.toCharArray());
        try {
            proveedorAlmacen.obtenerLlavePrivadaParaFirmar(archivoMock, CONTRASENIA);
        }
        catch (NoSuchAlgorithmException ex) {
            verify(archivoMock, times(1)).close();
        }
    }

    @Test
    public void debeRetornarLaLlavePrivadaAPartirDeUnArchivoPkcs12() throws KeyStoreException, CertificateException, NoSuchAlgorithmException, IOException, UnrecoverableKeyException {
        Mockito.when(aliasProvider.obtenerPrimerAliasParaFirmar(almacenLlaves)).thenReturn(aliasEsperado);
        PrivateKey llavePrivadaEsperada = Mockito.mock(PrivateKey.class);
        when(almacenLlaves.getKey(aliasEsperado, CONTRASENIA.toCharArray())).thenReturn(llavePrivadaEsperada);
        PrivateKey llavePrivada = proveedorAlmacen.obtenerLlavePrivadaParaFirmar(archivoPrueba, CONTRASENIA);
        assertThat(llavePrivada, is(llavePrivadaEsperada));
    }
}