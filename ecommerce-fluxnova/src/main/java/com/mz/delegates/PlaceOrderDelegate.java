package com.mz.delegates;

import org.finos.fluxnova.bpm.engine.delegate.DelegateExecution;
import org.finos.fluxnova.bpm.engine.delegate.JavaDelegate;
import org.springframework.stereotype.Component;

@Component("placeOrderDelegate")
public class PlaceOrderDelegate implements JavaDelegate {
    private static final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(PlaceOrderDelegate.class);
    @Override
    public void execute(DelegateExecution delegateExecution) throws Exception {
        log.info("Placing order for client id: {}", delegateExecution.getVariable("clientId"));
         delegateExecution.setVariable("orderId", "order-" + new java.util.Random().nextInt(1000));
    }
}
