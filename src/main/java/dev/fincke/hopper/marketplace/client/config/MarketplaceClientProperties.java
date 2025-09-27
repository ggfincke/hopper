package dev.fincke.hopper.marketplace.client.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.NestedConfigurationProperty;

import java.time.Duration;

// * Configuration Properties
// Exposes toggles for choosing stub vs remote adapter and remote HTTP settings.
@ConfigurationProperties(prefix = "marketplace.client")
public class MarketplaceClientProperties
{
    // Which implementation Spring should wire; defaults to the fast stub.
    private MarketplaceClientMode mode = MarketplaceClientMode.STUB;

    @NestedConfigurationProperty
    private final Remote remote = new Remote();

    // selected implementation for the marketplace client
    public MarketplaceClientMode getMode()
    {
        return mode;
    }

    // setter invoked by configuration binding
    public void setMode(MarketplaceClientMode mode)
    {
        this.mode = mode;
    }

    // remote connector settings
    public Remote getRemote()
    {
        return remote;
    }

    // * Remote Connector Settings
    // Holds base URL, auth, and timeout knobs for the Go marketplace connector.
    public static class Remote
    {
        private String baseUrl = "http://localhost:8081";
        private String bearerToken = "local-demo-token";
        private Duration connectTimeout = Duration.ofSeconds(3);
        private Duration readTimeout = Duration.ofSeconds(10);

        // base HTTP endpoint for the Go service
        public String getBaseUrl()
        {
            return baseUrl;
        }

        // setter invoked by configuration binding
        public void setBaseUrl(String baseUrl)
        {
            this.baseUrl = baseUrl;
        }

        // shared secret bearer token used by Java→Go calls (overridable without code changes)
        public String getBearerToken()
        {
            return bearerToken;
        }

        // setter invoked by configuration binding
        public void setBearerToken(String bearerToken)
        {
            this.bearerToken = bearerToken;
        }

        // connection timeout for the HTTP client
        public Duration getConnectTimeout()
        {
            return connectTimeout;
        }

        // setter invoked by configuration binding
        public void setConnectTimeout(Duration connectTimeout)
        {
            this.connectTimeout = connectTimeout;
        }

        // read timeout for the HTTP client
        public Duration getReadTimeout()
        {
            return readTimeout;
        }

        // setter invoked by configuration binding
        public void setReadTimeout(Duration readTimeout)
        {
            this.readTimeout = readTimeout;
        }
    }
}
