package com.mz.delegates;

import org.finos.fluxnova.bpm.engine.delegate.DelegateExecution;
import org.finos.fluxnova.bpm.engine.delegate.JavaDelegate;
import org.springframework.stereotype.Component;

@Component("checkBalanceDelegate")
public class CheckBalanceDelegate implements JavaDelegate {
    private static final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(CheckBalanceDelegate.class);
    @Override
    public void execute(DelegateExecution delegateExecution) throws Exception {
        log.info("Checking balance for client id: {}", delegateExecution.getVariable("clientId"));
       delegateExecution.setVariable("balance", 400 + new java.util.Random().nextInt(601));
    }
}
