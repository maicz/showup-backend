package com.mz.delegates;

import org.finos.fluxnova.bpm.engine.delegate.DelegateExecution;
import org.finos.fluxnova.bpm.engine.delegate.JavaDelegate;
import org.springframework.stereotype.Component;

@Component("cancelOrderDelegate")
public class CancelOrderDelegate implements JavaDelegate {
    private static final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(CancelOrderDelegate.class);
    @Override
    public void execute(DelegateExecution delegateExecution) throws Exception {
        log.info("Cancelling order for order id: {}", delegateExecution.getVariable("orderId"));
        delegateExecution.setVariable("orderStatus", "cancelled");
    }
}
