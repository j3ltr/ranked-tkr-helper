package me.j3ltr.rankedtkrhelper;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManagerFactory;
import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;
import java.security.*;
import java.security.cert.CertificateException;

public class Requester {
    public static final String PLAYER_DATA_URL = "https://data.heroku.com/dataclips/gupucelgdaeqhxyqleouxybmhdrh.json";

    static SSLContext sslContext;
    static {
        try {
            KeyStore myKeyStore = KeyStore.getInstance("JKS");
            myKeyStore.load(Requester.class.getResourceAsStream("/keystore.jks"), "changeit".toCharArray());
            KeyManagerFactory kmf = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
            TrustManagerFactory tmf = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
            kmf.init(myKeyStore, null);
            tmf.init(myKeyStore);
            sslContext = SSLContext.getInstance("TLS");
            sslContext.init(kmf.getKeyManagers(), tmf.getTrustManagers(), null);
        } catch (KeyStoreException | NoSuchAlgorithmException | KeyManagementException | UnrecoverableKeyException |
                 IOException | CertificateException e) {
            System.out.println("Failed to load keystore. A lot of API requests won't work");
            e.printStackTrace();
            sslContext = null;
        }
    }

    public static URLConnection openHTTPSConnection(URL url) throws Exception {
        URLConnection connection = url.openConnection();
        if (connection instanceof HttpsURLConnection && sslContext != null) {
            ((HttpsURLConnection) connection).setSSLSocketFactory(sslContext.getSocketFactory());
        }
        return connection;
    }
}
