package dev.fincke.hopper.batch.order;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.item.ItemReader;
import org.springframework.stereotype.Component;

/**
 * Placeholder reader that will fetch orders from an external marketplace API.
 */
@Component
public class ExternalOrderItemReader implements ItemReader<ExternalOrderRecord>
{
    private static final Logger logger = LoggerFactory.getLogger(ExternalOrderItemReader.class);

    @Override
    public ExternalOrderRecord read()
    {
        logger.debug("Order import reader reached end of data (stub implementation)");
        return null; // returning null signals no more records for the step
    }
}
