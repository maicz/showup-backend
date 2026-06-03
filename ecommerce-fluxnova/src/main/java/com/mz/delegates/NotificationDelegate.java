package com.mz.delegates;

import org.finos.fluxnova.bpm.engine.delegate.DelegateExecution;
import org.finos.fluxnova.bpm.engine.delegate.JavaDelegate;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component("notificationDelegate")
public class NotificationDelegate implements JavaDelegate {
    private static final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(NotificationDelegate.class);

    @Override
    public void execute(DelegateExecution execution) throws Exception {
        log.info("Sending notification...");

        // Optional showcase
        Optional<String> email = Optional.ofNullable((String) execution.getVariable("customerEmail"));
        String orderId = (String) execution.getVariable("orderId");

        email.ifPresentOrElse(
            e -> log.info("Sending confirmation email to {} for order {}", e, orderId),
            () -> log.warn("No email found for order {}. Sending SMS instead (mocked).", orderId)
        );

        // Functional style with Optional
        String status = Optional.ofNullable((String) execution.getVariable("paymentStatus"))
            .map(s -> "Order " + orderId + " payment status: " + s)
            .orElse("Payment status unknown for order " + orderId);

        log.info("Notification status: {}", status);
        execution.setVariable("notificationSent", true);
    }
}
