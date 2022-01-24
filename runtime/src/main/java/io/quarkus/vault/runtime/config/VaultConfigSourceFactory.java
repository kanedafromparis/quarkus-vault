package io.quarkus.vault.runtime.config;

import java.time.Duration;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import org.eclipse.microprofile.config.spi.ConfigSource;

import io.quarkus.arc.Arc;
import io.quarkus.runtime.configuration.DurationConverter;
import io.quarkus.vault.runtime.client.VertxVaultClient;
import io.smallrye.config.ConfigSourceContext;
import io.smallrye.config.ConfigSourceFactory;

public class VaultConfigSourceFactory implements ConfigSourceFactory {

    public static Duration getDurationFromConfig(ConfigSourceContext context, String property, String defaultValue) {
        return new DurationConverter().convert(getValueFromConfig(context, property, defaultValue));
    }

    public static int getIntegerFromConfig(ConfigSourceContext context, String property, int defaultValue) {
        return Integer.parseInt(getValueFromConfig(context, property, "" + defaultValue));
    }

    public static String getUrlFromConfig(ConfigSourceContext context) {
        return getValueFromConfig(context, "quarkus.vault.url", null);
    }

    public static String getValueFromConfig(ConfigSourceContext context, String property, String defaultValue) {
        return Optional.ofNullable(context.getValue(property).getValue()).orElse(defaultValue);
    }

    // ---

    @Override
    public Iterable<ConfigSource> getConfigSources(ConfigSourceContext context) {
        boolean enabled = getUrlFromConfig(context) != null;
        if (enabled) {
            Arc.container().instance(VertxVaultClient.class).get().init(context);
            return List.of(new VaultConfigSource(context));
        } else {
            return Collections.emptyList();
        }
    }
}
