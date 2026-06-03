package com.mz.delegates;


import org.finos.fluxnova.bpm.engine.delegate.DelegateExecution;
import org.finos.fluxnova.bpm.engine.delegate.JavaDelegate;
import org.springframework.stereotype.Component;

@Component("getClientDataDelegate")
public class GetClientData implements JavaDelegate {
    private static final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(GetClientData.class);
    @Override
    public void execute(DelegateExecution delegateExecution) throws Exception {
            log.info("Getting client data for client id: {}", delegateExecution.getVariable("clientId"));
            delegateExecution.setVariable("clientData","test client data");
    }
}
