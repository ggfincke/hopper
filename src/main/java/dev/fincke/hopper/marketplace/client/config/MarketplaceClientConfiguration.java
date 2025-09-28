package dev.fincke.hopper.marketplace.client.config;

import dev.fincke.hopper.marketplace.client.MarketplaceClient;
import dev.fincke.hopper.marketplace.client.adapter.RemoteGoMarketplaceClient;
import dev.fincke.hopper.marketplace.client.adapter.StubMarketplaceClient;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.web.client.RestClient;
import org.springframework.http.client.SimpleClientHttpRequestFactory;

// * Configuration
// Provides stub/remote marketplace clients based on application properties.
@Configuration
// Enables binding of marketplace.client.* properties into MarketplaceClientProperties.
@EnableConfigurationProperties(MarketplaceClientProperties.class)
public class MarketplaceClientConfiguration
{
    @Bean
    // Chooses stub or remote implementation depending on `marketplace.client.mode`.
    public MarketplaceClient marketplaceClient(
        MarketplaceClientProperties properties,
        StubMarketplaceClient stub,
        RemoteGoMarketplaceClient remote)
    {
        return properties.getMode() == MarketplaceClientMode.REMOTE ? remote : stub;
    }

    @Bean
    // Stub adapter used for local demos and tests.
    public StubMarketplaceClient stubMarketplaceClient()
    {
        return new StubMarketplaceClient();
    }

    @Bean
    // Remote adapter configured with timeouts and bearer token for the Go connector.
    public RemoteGoMarketplaceClient remoteGoMarketplaceClient(MarketplaceClientProperties properties)
    {
        MarketplaceClientProperties.Remote remoteProps = properties.getRemote();
        SimpleClientHttpRequestFactory requestFactory = new SimpleClientHttpRequestFactory();
        // Configure HTTP client timeouts using property values.
        requestFactory.setConnectTimeout(Math.toIntExact(remoteProps.getConnectTimeout().toMillis()));
        requestFactory.setReadTimeout(Math.toIntExact(remoteProps.getReadTimeout().toMillis()));

        RestClient.Builder builder = RestClient.builder()
            .baseUrl(remoteProps.getBaseUrl())
            .requestFactory(requestFactory);

        if (remoteProps.getBearerToken() != null && !remoteProps.getBearerToken().isBlank())
        {
            // Attach shared-secret bearer token for the Go connector.
            builder.defaultHeader(HttpHeaders.AUTHORIZATION, "Bearer " + remoteProps.getBearerToken());
        }

        RestClient restClient = builder.build();

        return new RemoteGoMarketplaceClient(restClient);
    }
}
