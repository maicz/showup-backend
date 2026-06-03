package com.mz.delegates;

import org.finos.fluxnova.bpm.engine.delegate.DelegateExecution;
import org.finos.fluxnova.bpm.engine.delegate.JavaDelegate;
import org.springframework.stereotype.Component;

@Component("calculateDiscountDelegate")
public class CalculateDiscountDelegate implements JavaDelegate {
    private static final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(CalculateDiscountDelegate.class);

    // Sealed interface and records for pattern matching showcase
    sealed interface Customer permits RegularCustomer, VIPCustomer, NewCustomer {}
    record RegularCustomer(String name, int yearsActive) implements Customer {}
    record VIPCustomer(String name, String tier) implements Customer {}
    record NewCustomer(String name) implements Customer {}

    @Override
    public void execute(DelegateExecution execution) throws Exception {
        log.info("Calculating discount...");

        // Simulate getting customer data
        Customer customer = new VIPCustomer("John Doe", "GOLD");
        double orderTotal = (double) execution.getVariable("orderTotal");

        // Switch Expression with Pattern Matching (Java 21)
        double discount = switch (customer) {
            case RegularCustomer rc when rc.yearsActive() > 5 -> orderTotal * 0.10;
            case RegularCustomer rc -> orderTotal * 0.05;
            case VIPCustomer vc -> switch (vc.tier()) {
                case "GOLD" -> orderTotal * 0.20;
                case "SILVER" -> orderTotal * 0.15;
                default -> orderTotal * 0.10;
            };
            case NewCustomer nc -> 0.0;
        };

        log.info("Applied discount: {} for customer type: {}", discount, customer.getClass().getSimpleName());
        
        double finalPrice = orderTotal - discount;
        execution.setVariable("discount", discount);
        execution.setVariable("finalPrice", finalPrice);
        
        // Updating balance to be used by check_balance
        execution.setVariable("balance", (int)finalPrice + 100); // ensure it's enough or not based on logic
    }
}
