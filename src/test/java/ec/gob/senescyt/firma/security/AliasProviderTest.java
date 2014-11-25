package ec.gob.senescyt.firma.security;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.cert.X509Certificate;
import java.util.Enumeration;

import static org.apache.commons.lang.RandomStringUtils.randomAlphabetic;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.Matchers.isNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

@RunWith(PowerMockRunner.class)
@PrepareForTest({ KeyStore.class, AliasProvider.class })
public class AliasProviderTest {

    private boolean[] usosLlaveParaFirmar = new boolean[]{true, false};
    private boolean[] usosLlaveParaOtrosFines = new boolean[]{false, true};
    private boolean[] llavesSinUso = null;
    private KeyStore almacenLlaves;
    private AliasProvider aliasProvider;
    private Enumeration listadoAlias;
    private String aliasEsperado;
    private X509Certificate certificado;

    @Before
    public void setUp() throws Exception {
        initMocks(this);
        almacenLlaves = PowerMockito.mock(KeyStore.class);
        listadoAlias = PowerMockito.mock(Enumeration.class);
        certificado = mock(X509Certificate.class);
        aliasProvider = new AliasProvider();
        aliasEsperado = randomAlphabetic(10);

        when(listadoAlias.hasMoreElements()).thenReturn(true);
        when(listadoAlias.nextElement()).thenReturn(aliasEsperado);
        PowerMockito.when(almacenLlaves.aliases()).thenReturn(listadoAlias);
        PowerMockito.when(almacenLlaves.getCertificate(aliasEsperado)).thenReturn(certificado);
    }

    @Test
    public void debeRetornarElPrimerAliasDisponibleParaFirmar() throws KeyStoreException {
        when(certificado.getKeyUsage()).thenReturn(usosLlaveParaFirmar);
        String alias = aliasProvider.obtenerPrimerAliasParaFirmar(almacenLlaves);
        assertThat(alias, is(aliasEsperado));
    }

    @Test
    public void debeRetornarNullSiNoSeEncuentraUnAliasDisponibleParaFirmar() throws KeyStoreException {
        when(certificado.getKeyUsage()).thenReturn(usosLlaveParaOtrosFines);
        when(listadoAlias.hasMoreElements()).thenReturn(false);
        String alias = aliasProvider.obtenerPrimerAliasParaFirmar(almacenLlaves);
        assertThat(alias, is(isNull()));
    }

    @Test
    public void debeRetornarNullSiElCertificadoNoTieneUsosDefinidos() throws KeyStoreException {
        when(certificado.getKeyUsage()).thenReturn(llavesSinUso);
        when(listadoAlias.hasMoreElements()).thenReturn(false);
        String alias = aliasProvider.obtenerPrimerAliasParaFirmar(almacenLlaves);
        assertThat(alias, is(isNull()));
    }

    @Test
    public void debeRetornarElSegundoAliasCuandoElPrimeroNoEstaDisponibleParaFirmar() throws KeyStoreException {
        String segundoAlias = randomAlphabetic(10);
        when(certificado.getKeyUsage()).thenReturn(usosLlaveParaOtrosFines);
        when(listadoAlias.nextElement()).thenReturn(segundoAlias);
        when(almacenLlaves.getCertificate(segundoAlias)).thenReturn(certificado);
        when(certificado.getKeyUsage()).thenReturn(usosLlaveParaFirmar);
        String alias = aliasProvider.obtenerPrimerAliasParaFirmar(almacenLlaves);
        assertThat(alias, is(segundoAlias));
    }
}
