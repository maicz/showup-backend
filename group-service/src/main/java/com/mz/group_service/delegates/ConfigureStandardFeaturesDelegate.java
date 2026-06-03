package com.mz.group_service.delegates;

import org.finos.fluxnova.bpm.engine.delegate.DelegateExecution;
import org.finos.fluxnova.bpm.engine.delegate.JavaDelegate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component("configureStandardFeaturesDelegate")
public class ConfigureStandardFeaturesDelegate implements JavaDelegate {
    private static final Logger log = LoggerFactory.getLogger(ConfigureStandardFeaturesDelegate.class);

    @Override
    public void execute(DelegateExecution execution) throws Exception {
        log.info("Executing ConfigureStandardFeaturesDelegate for process: {}", execution.getProcessInstanceId());
    }
}
