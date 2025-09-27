package dev.fincke.hopper.marketplace.client.config;

import dev.fincke.hopper.marketplace.client.MarketplaceClient;
import dev.fincke.hopper.marketplace.client.adapter.RemoteGoMarketplaceClient;
import dev.fincke.hopper.marketplace.client.adapter.StubMarketplaceClient;
import org.junit.jupiter.api.Test;
import org.springframework.boot.autoconfigure.AutoConfigurations;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;

import static org.assertj.core.api.Assertions.assertThat;

// * Tests
// Ensures Spring wiring selects the correct marketplace client implementation.
class MarketplaceClientConfigurationTest
{
    // * Context Harness
    // ApplicationContextRunner allows per-test property overrides without full app startup.
    private final ApplicationContextRunner contextRunner = new ApplicationContextRunner()
        .withConfiguration(AutoConfigurations.of(
            MarketplaceClientConfiguration.class,
            org.springframework.boot.autoconfigure.context.ConfigurationPropertiesAutoConfiguration.class
        ));

    // Default mode should wire the in-process stub implementation.
    @Test
    void defaultModeLoadsStub()
    {
        contextRunner.run(context ->
        {
            MarketplaceClient client = context.getBean("marketplaceClient", MarketplaceClient.class);
            assertThat(client).isInstanceOf(StubMarketplaceClient.class);
        });
    }

    // Switching the property to REMOTE must provide the HTTP adapter.
    @Test
    void remoteModeLoadsRemoteAdapter()
    {
        contextRunner.withPropertyValues("marketplace.client.mode=REMOTE").run(context ->
        {
            MarketplaceClient client = context.getBean("marketplaceClient", MarketplaceClient.class);
            assertThat(client).isInstanceOf(RemoteGoMarketplaceClient.class);
        });
    }
}
