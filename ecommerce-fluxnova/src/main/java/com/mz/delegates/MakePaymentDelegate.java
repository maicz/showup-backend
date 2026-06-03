package com.mz.delegates;

import org.finos.fluxnova.bpm.engine.delegate.DelegateExecution;
import org.finos.fluxnova.bpm.engine.delegate.JavaDelegate;
import org.springframework.stereotype.Component;

@Component("makePaymentDelegate")
public class MakePaymentDelegate implements JavaDelegate {
    private static final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(MakePaymentDelegate.class);
    @Override
    public void execute(DelegateExecution delegateExecution) throws Exception {
        log.info("Making payment for order id: {}", delegateExecution.getVariable("orderId"));
        delegateExecution.setVariable("balance", (int) delegateExecution.getVariable("balance") / 2);
        delegateExecution.setVariable("paymentId", "payment-" + new java.util.Random().nextInt(1000));
        delegateExecution.setVariable("paymentStatus", "success");
        delegateExecution.setVariable("orderStatus", "paid");
    }
}
