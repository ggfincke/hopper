package dev.fincke.hopper.batch.order;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;

// Normalizes marketplace records into the command shape consumed by the domain importer.
// Keeps the batch pipeline decoupled from marketplace-specific payloads.
@Component
public class ExternalOrderItemProcessor implements ItemProcessor<ExternalOrderRecord, OrderImportRequest>
{
    private static final Logger logger = LoggerFactory.getLogger(ExternalOrderItemProcessor.class);

    @Override
    public OrderImportRequest process(@NonNull ExternalOrderRecord item)
    {
        if (item == null)
        {
            return null;
        }

        logger.debug("Processing external order {} from platform {}", item.externalOrderId(), item.platformCode());

        return new OrderImportRequest(
            item.platformCode(),
            item.externalOrderId(),
            item.buyerId(),
            item.totalAmount(),
            item.orderDate()
        );
    }
}
