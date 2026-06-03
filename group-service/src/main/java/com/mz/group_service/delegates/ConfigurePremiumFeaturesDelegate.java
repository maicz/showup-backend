package com.mz.group_service.delegates;

import org.finos.fluxnova.bpm.engine.delegate.DelegateExecution;
import org.finos.fluxnova.bpm.engine.delegate.JavaDelegate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component("configurePremiumFeaturesDelegate")
public class ConfigurePremiumFeaturesDelegate implements JavaDelegate {
    private static final Logger log = LoggerFactory.getLogger(ConfigurePremiumFeaturesDelegate.class);

    @Override
    public void execute(DelegateExecution execution) throws Exception {
        log.info("Executing ConfigurePremiumFeaturesDelegate for process: {}", execution.getProcessInstanceId());
    }
}
