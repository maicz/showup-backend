package com.mz.delegates;

import org.finos.fluxnova.bpm.engine.delegate.DelegateExecution;
import org.finos.fluxnova.bpm.engine.delegate.JavaDelegate;
import org.springframework.stereotype.Component;

@Component
public class GenerateClientIdDelegate implements JavaDelegate {
    private static final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(GenerateClientIdDelegate.class);

    @Override
    public void execute(DelegateExecution delegateExecution) throws Exception {
        String clientId = "client-" + new java.util.Random().nextInt(1000);
        log.info("Generated client id: {}", clientId);
        delegateExecution.setVariable("clientId", clientId);
    }
}
