package com.mz.media_service.delegates;

import org.finos.fluxnova.bpm.engine.delegate.DelegateExecution;
import org.finos.fluxnova.bpm.engine.delegate.JavaDelegate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component("storeMetadataDelegate")
public class StoreMetadataDelegate implements JavaDelegate {
    private static final Logger log = LoggerFactory.getLogger(StoreMetadataDelegate.class);

    @Override
    public void execute(DelegateExecution execution) throws Exception {
        log.info("Executing StoreMetadataDelegate for process: {}", execution.getProcessInstanceId());
        
        String fileUrl = (String) execution.getVariable("fileUrl");
        String fileType = (String) execution.getVariable("fileType");
        
        if (fileUrl == null) fileUrl = "http://storage.showup.internal/photos/default.jpg";
        if (fileType == null) fileType = "image/jpeg";
        
        log.info("Stored media metadata: URL='{}', Type='{}'", fileUrl, fileType);
        
        execution.setVariable("metadataStatus", "STORED");
    }
}
