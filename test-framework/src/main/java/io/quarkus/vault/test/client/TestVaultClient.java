package io.quarkus.vault.test.client;

import static io.quarkus.vault.runtime.config.VaultBootstrapConfig.DEFAULT_CONFIG_ORDINAL;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import io.quarkus.runtime.TlsConfig;
import io.quarkus.vault.runtime.client.VertxVaultClient;
import io.quarkus.vault.runtime.client.dto.transit.VaultTransitRandomBody;
import io.quarkus.vault.test.client.dto.VaultAppRoleRoleId;
import io.quarkus.vault.test.client.dto.VaultAppRoleSecretId;
import io.quarkus.vault.test.client.dto.VaultTransitHash;
import io.quarkus.vault.test.client.dto.VaultTransitHashBody;
import io.quarkus.vault.test.client.dto.VaultTransitRandom;
import io.smallrye.config.ConfigSourceContext;
import io.smallrye.config.ConfigValue;

public class TestVaultClient extends VertxVaultClient {

    //    private static VaultConfigHolder getConfigHolder() {
    //        return Arc.container().instance(VaultConfigHolder.class).get();
    //    }
    //
    //    public TestVaultClient() {
    //        this(getConfigHolder());
    //    }
    //
    //    public TestVaultClient(VaultConfigHolder configHolder) {
    //        super(configHolder, new TlsConfig());
    //        init();
    //    }

    public TestVaultClient() {
        this(new HashMap<>());
    }

    public TestVaultClient(Map<String, String> map) {
        super(new TlsConfig());
        init(createContext(map));
    }

    private static ConfigSourceContext createContext(Map<String, String> map) {
        return new ConfigSourceContext() {
            @Override
            public io.smallrye.config.ConfigValue getValue(String name) {
                return ConfigValue.builder()
                        .withConfigSourceName("test")
                        .withName(name)
                        .withValue(map.get(name))
                        .withConfigSourceOrdinal(Integer.parseInt(DEFAULT_CONFIG_ORDINAL))
                        .build();
            }

            @Override
            public List<String> getProfiles() {
                return Collections.emptyList();
            }

            @Override
            public Iterator<String> iterateNames() {
                return new ArrayList<>(map.keySet()).iterator();
            }
        };
    }

    public VaultAppRoleSecretId generateAppRoleSecretId(String token, String roleName) {
        return post("auth/approle/role/" + roleName + "/secret-id", token, null, VaultAppRoleSecretId.class);
    }

    public VaultAppRoleRoleId getAppRoleRoleId(String token, String role) {
        return get("auth/approle/role/" + role + "/role-id", token, VaultAppRoleRoleId.class);
    }

    public void rotate(String token, String keyName) {
        post("transit/keys/" + keyName + "/rotate", token, null, null, 204);
    }

    public VaultTransitRandom generateRandom(String token, int byteCount, VaultTransitRandomBody body) {
        return post("transit/random/" + byteCount, token, body, VaultTransitRandom.class);
    }

    public VaultTransitHash hash(String token, String hashAlgorithm, VaultTransitHashBody body) {
        return post("transit/hash/" + hashAlgorithm, token, body, VaultTransitHash.class);
    }
}
