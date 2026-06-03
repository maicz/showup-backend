package com.mz.delegates;

import org.finos.fluxnova.bpm.engine.delegate.DelegateExecution;
import org.finos.fluxnova.bpm.engine.delegate.JavaDelegate;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

@Component("validateOrderDelegate")
public class ValidateOrderDelegate implements JavaDelegate {
    private static final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(ValidateOrderDelegate.class);

    // Record showcase
    public record OrderItem(String name, double price, int quantity) {
        public double total() {
            return price * quantity;
        }
    }

    @Override
    public void execute(DelegateExecution execution) throws Exception {
        log.info("Validating order items...");

        // Dummy data - in a real scenario this might come from a process variable
        List<OrderItem> items = List.of(
            new OrderItem("Laptop", 1200.0, 1),
            new OrderItem("Mouse", 25.0, 2),
            new OrderItem("Keyboard", 75.0, 1)
        );

        // Streams showcase
        double grandTotal = items.stream()
            .filter(item -> item.quantity() > 0)
            .mapToDouble(OrderItem::total)
            .sum();

        log.info("Calculated grand total for {} items: {}", items.size(), grandTotal);

        execution.setVariable("orderItemsCount", items.size());
        execution.setVariable("orderTotal", grandTotal);
        execution.setVariable("isValidated", true);
    }
}
