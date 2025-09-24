package dev.fincke.hopper.batch.order;

import dev.fincke.hopper.order.order.OrderService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.item.Chunk;
import org.springframework.batch.item.ItemWriter;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Persists normalized orders using the domain services.
 */
@Component
public class OrderImportItemWriter implements ItemWriter<OrderImportRequest>
{
    private static final Logger logger = LoggerFactory.getLogger(OrderImportItemWriter.class);

    private final OrderService orderService;

    public OrderImportItemWriter(OrderService orderService)
    {
        this.orderService = orderService;
    }

    @Override
    public void write(Chunk<? extends OrderImportRequest> chunk)
    {
        if (chunk == null || chunk.isEmpty())
        {
            return;
        }

        List<? extends OrderImportRequest> items = chunk.getItems();
        logger.info("Order import writer received {} orders (stub implementation)", items.size());
        // Map OrderImportRequest into the appropriate service call when the integration client is implemented.
    }
}
