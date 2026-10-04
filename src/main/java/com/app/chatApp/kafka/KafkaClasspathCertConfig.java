package com.app.chatApp.kafka;

import java.io.File;
import java.io.InputStream;
import java.nio.file.Files;
import java.security.KeyStore;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import jakarta.annotation.PostConstruct;

@Configuration
public class KafkaClasspathCertConfig {

    @PostConstruct
    public void configureGlobalTruststore() {
        try {
            ClassPathResource caResource = new ClassPathResource("certs/ca.pem");
            if (!caResource.exists()) {
                return;
            }

            CertificateFactory cf = CertificateFactory.getInstance("X.509");
            X509Certificate caCert;
            try (InputStream in = caResource.getInputStream()) {
                caCert = (X509Certificate) cf.generateCertificate(in);
            }

            KeyStore trustStore = KeyStore.getInstance(KeyStore.getDefaultType());
            trustStore.load(null, null);
            trustStore.setCertificateEntry("aiven-ca", caCert);

            // Load default JVM root certificates so other HTTPS calls don't break
            String defaultTrustStorePath = System.getProperty("java.home") + "/lib/security/cacerts";
            File defaultTrustFile = new File(defaultTrustStorePath);
            if (defaultTrustFile.exists()) {
                try (InputStream defaultIn = Files.newInputStream(defaultTrustFile.toPath())) {
                    KeyStore defaultTrustStore = KeyStore.getInstance(KeyStore.getDefaultType());
                    defaultTrustStore.load(defaultIn, "changeit".toCharArray());
                    var aliases = defaultTrustStore.aliases();
                    while (aliases.hasMoreElements()) {
                        String alias = aliases.nextElement();
                        if (defaultTrustStore.isCertificateEntry(alias)) {
                            trustStore.setCertificateEntry(alias, defaultTrustStore.getCertificate(alias));
                        }
                    }
                }
            }

            File tempTrustStore = File.createTempFile("kafka_truststore", ".jks");
            tempTrustStore.deleteOnExit();
            try (var out = Files.newOutputStream(tempTrustStore.toPath())) {
                trustStore.store(out, "secret".toCharArray());
            }

            System.setProperty("javax.net.ssl.trustStore", tempTrustStore.getAbsolutePath());
            System.setProperty("javax.net.ssl.trustStorePassword", "secret");

        } catch (Exception e) {
            System.err.println("Notice: Kafka Aiven CA truststore configuration skipped: " + e.getMessage());
        }
    }
}
