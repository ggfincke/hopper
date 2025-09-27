package dev.fincke.hopper.batch.order;

import org.springframework.boot.context.properties.ConfigurationProperties;

// Exposes batch job tuning knobs via configuration properties.
// Lets operators adjust import throughput without code changes.
@ConfigurationProperties(prefix = "hopper.batch.order-import")
public class OrderImportJobProperties
{
    // * Configuration Values

    // Number of orders processed per chunk to balance throughput with memory usage.
    private int chunkSize = 100;

    public int getChunkSize()
    {
        return chunkSize;
    }

    public void setChunkSize(int chunkSize)
    {
        this.chunkSize = chunkSize;
    }
}
