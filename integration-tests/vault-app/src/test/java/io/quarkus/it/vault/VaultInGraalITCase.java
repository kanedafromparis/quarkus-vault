package io.quarkus.it.vault;

import org.junit.jupiter.api.condition.DisabledOnOs;
import org.junit.jupiter.api.condition.OS;

import io.quarkus.test.common.QuarkusTestResource;
import io.quarkus.test.junit.QuarkusIntegrationTest;
import io.quarkus.vault.test.VaultTestLifecycleManager;

@QuarkusIntegrationTest
//see TLS availability in native mode https://github.com/quarkusio/quarkus/issues/3797
@DisabledOnOs(value = { OS.WINDOWS, OS.MAC })
@QuarkusTestResource(VaultTestLifecycleManager.class)
public class VaultInGraalITCase extends VaultTest {

}
