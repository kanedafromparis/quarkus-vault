package io.quarkus.vault.runtime.client;

import static io.quarkus.vault.runtime.config.VaultBootstrapConfig.DEFAULT_CONNECT_TIMEOUT;
import static io.quarkus.vault.runtime.config.VaultBootstrapConfig.DEFAULT_READ_TIMEOUT;

import java.time.Duration;
import java.util.Optional;

import org.jboss.logging.Logger;

import io.quarkus.runtime.TlsConfig;
import io.quarkus.vault.runtime.config.VaultConfigSourceFactory;
import io.smallrye.config.ConfigSourceContext;
import io.vertx.core.net.PemTrustOptions;
import io.vertx.ext.web.client.WebClientOptions;
import io.vertx.mutiny.core.Vertx;
import io.vertx.mutiny.ext.web.client.WebClient;

public class MutinyVertxClientFactory {

    private static final Logger log = Logger.getLogger(MutinyVertxClientFactory.class.getName());

    public static WebClient createHttpClient(Vertx vertx, ConfigSourceContext context, TlsConfig tlsConfig) {

        Duration connectTimeout = VaultConfigSourceFactory.getDurationFromConfig(context, "quarkus.vault.connect-timeout",
                DEFAULT_CONNECT_TIMEOUT);
        Duration readTimeout = VaultConfigSourceFactory.getDurationFromConfig(context, "quarkus.vault.read-timeout",
                DEFAULT_READ_TIMEOUT);

        WebClientOptions options = new WebClientOptions()
                .setConnectTimeout((int) connectTimeout.toMillis())
                .setIdleTimeout((int) readTimeout.getSeconds() * 2);

        // TODO factory
        //        if (vaultBootstrapConfig.nonProxyHosts.isPresent()) {
        //            options.setNonProxyHosts(vaultBootstrapConfig.nonProxyHosts.get());
        //        }

        boolean trustAll = Boolean.parseBoolean(
                Optional.ofNullable(VaultConfigSourceFactory.getValueFromConfig(context, "quarkus.vault.tls.skip-verify", null))
                        .orElseGet(() -> "" + tlsConfig.trustAll));
        if (trustAll) {
            skipVerify(options);
        } else if (getCaCert(context).isPresent()) {
            cacert(options, getCaCert(context).get());
            // TODO factory
            //        } else if (vaultBootstrapConfig.getAuthenticationType() == KUBERNETES
            //                && vaultBootstrapConfig.tls.useKubernetesCaCert) {
            //            cacert(options, KUBERNETES_CACERT);
        }

        return WebClient.create(vertx, options);
    }

    private static Optional<String> getCaCert(ConfigSourceContext context) {
        return Optional.ofNullable(VaultConfigSourceFactory.getValueFromConfig(context, "quarkus.vault.tls.ca-cert", null));
    }

    private static void cacert(WebClientOptions options, String cacert) {
        log.debug("configure tls with " + cacert);
        options.setTrustOptions(new PemTrustOptions().addCertPath(cacert));
    }

    private static void skipVerify(WebClientOptions options) {
        log.debug("configure tls with skip-verify");
        options.setTrustAll(true);
        options.setVerifyHost(false);
    }
}
