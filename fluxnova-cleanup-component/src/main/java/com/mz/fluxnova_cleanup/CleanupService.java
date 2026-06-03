package com.mz.fluxnova_cleanup;

import org.finos.fluxnova.bpm.engine.HistoryService;
import org.finos.fluxnova.bpm.engine.RepositoryService;
import org.finos.fluxnova.bpm.engine.RuntimeService;
import org.finos.fluxnova.bpm.engine.history.HistoricProcessInstance;
import org.finos.fluxnova.bpm.engine.repository.ProcessDefinition;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class CleanupService {
    private static final Logger log = LoggerFactory.getLogger(CleanupService.class);

    private final RepositoryService repositoryService;
    private final RuntimeService runtimeService;
    private final HistoryService historyService;

    @Value("${cleanup.hanging-instances.threshold-days:30}")
    private int thresholdDays;

    @Value("${cleanup.old-deployments.keep-versions:3}")
    private int keepVersions;

    public CleanupService(RepositoryService repositoryService, RuntimeService runtimeService, HistoryService historyService) {
        this.repositoryService = repositoryService;
        this.runtimeService = runtimeService;
        this.historyService = historyService;
    }

    @EventListener(ApplicationReadyEvent.class)
    public void onStartup() {
        log.info("Fluxnova Cleanup Service initialized. Running initial database cleanup...");
        runCleanup();
    }

    // Run periodically (every hour)
    @Scheduled(cron = "0 0 * * * ?")
    public void scheduledCleanup() {
        log.info("Running scheduled database cleanup...");
        runCleanup();
    }

    public void runCleanup() {
        try {
            cleanupHangingProcessInstances();
            cleanupOldDeployments();
        } catch (Exception e) {
            log.error("Error occurred during database cleanup execution", e);
        }
    }

    private void cleanupHangingProcessInstances() {
        log.info("Checking for hanging process instances active longer than {} days...", thresholdDays);
        Date cutoffDate = Date.from(Instant.now().minus(thresholdDays, ChronoUnit.DAYS));

        List<HistoricProcessInstance> hangingInstances = historyService.createHistoricProcessInstanceQuery()
                .unfinished()
                .startedBefore(cutoffDate)
                .list();

        log.info("Found {} hanging process instances to clean up.", hangingInstances.size());
        for (HistoricProcessInstance instance : hangingInstances) {
            try {
                log.info("Deleting hanging process instance: ID={}, Key={}, Started={}", 
                        instance.getId(), instance.getProcessDefinitionKey(), instance.getStartTime());
                runtimeService.deleteProcessInstance(instance.getId(), 
                        "Cleaned up by dedicated Fluxnova Cleanup Component (running longer than threshold)");
            } catch (Exception e) {
                log.error("Failed to delete process instance " + instance.getId(), e);
            }
        }
    }

    private void cleanupOldDeployments() {
        log.info("Checking for old deployments (keeping only latest {} versions per process definition)...", keepVersions);

        List<ProcessDefinition> processDefinitions = repositoryService.createProcessDefinitionQuery().list();

        // Group definitions by process key
        Map<String, List<ProcessDefinition>> groupedDefinitions = processDefinitions.stream()
                .collect(Collectors.groupingBy(ProcessDefinition::getKey));

        int totalDeleted = 0;

        for (Map.Entry<String, List<ProcessDefinition>> entry : groupedDefinitions.entrySet()) {
            String processKey = entry.getKey();
            List<ProcessDefinition> definitions = entry.getValue();

            // Sort descending by version number
            definitions.sort(Comparator.comparingInt(ProcessDefinition::getVersion).reversed());

            if (definitions.size() > keepVersions) {
                log.info("Process '{}' has {} deployed versions. Keeping latest {} versions.", 
                        processKey, definitions.size(), keepVersions);

                // Identify definitions to delete (everything index >= keepVersions)
                List<ProcessDefinition> toDelete = definitions.subList(keepVersions, definitions.size());

                for (ProcessDefinition def : toDelete) {
                    try {
                        log.info("Deleting old deployment: ID={}, Version={}, Key={}", 
                                def.getDeploymentId(), def.getVersion(), def.getKey());
                        
                        // Cascade delete will delete definition, historical data, and active instances
                        repositoryService.deleteDeployment(def.getDeploymentId(), true);
                        totalDeleted++;
                    } catch (Exception e) {
                        log.error("Failed to delete deployment " + def.getDeploymentId() + " for process " + processKey, e);
                    }
                }
            }
        }
        log.info("Cleanup of old deployments completed. Total old deployments deleted: {}", totalDeleted);
    }
}
