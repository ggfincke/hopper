package dev.fincke.hopper.batch.order;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Configuration properties for the order import batch job.
 */
@ConfigurationProperties(prefix = "hopper.batch.order-import")
public class OrderImportJobProperties
{
    /**
     * Chunk size used for the order import step.
     */
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
