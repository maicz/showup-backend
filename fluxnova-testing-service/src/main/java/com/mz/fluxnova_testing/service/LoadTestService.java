package com.mz.fluxnova_testing.service;

import org.finos.fluxnova.bpm.engine.RepositoryService;
import org.finos.fluxnova.bpm.engine.RuntimeService;
import org.finos.fluxnova.bpm.engine.repository.ProcessDefinition;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

@Service
public class LoadTestService {
    private static final Logger log = LoggerFactory.getLogger(LoadTestService.class);

    private final RuntimeService runtimeService;
    private final RepositoryService repositoryService;
    private final ExecutorService executorService = Executors.newCachedThreadPool(); // Compatible with Java 17 & 21
    
    private final List<TestRunResult> testHistory = new CopyOnWriteArrayList<>();

    public LoadTestService(RuntimeService runtimeService, RepositoryService repositoryService) {
        this.runtimeService = runtimeService;
        this.repositoryService = repositoryService;
    }

    public List<Map<String, Object>> getProcessDefinitions() {
        List<ProcessDefinition> list = repositoryService.createProcessDefinitionQuery()
                .latestVersion()
                .list();
        List<Map<String, Object>> result = new ArrayList<>();
        for (ProcessDefinition def : list) {
            Map<String, Object> map = new HashMap<>();
            map.put("id", def.getId());
            map.put("key", def.getKey());
            map.put("name", def.getName());
            map.put("version", def.getVersion());
            map.put("resourceName", def.getResourceName());
            result.add(map);
        }
        return result;
    }

    public TestRunResult runLoadTest(String processKey, int instanceCount, Map<String, Object> variables) {
        String testId = UUID.randomUUID().toString();
        Instant startTime = Instant.now();
        log.info("Starting load test [{}] for process key '{}' with {} instances...", testId, processKey, instanceCount);

        AtomicInteger successCount = new AtomicInteger(0);
        AtomicInteger failureCount = new AtomicInteger(0);
        
        List<CompletableFuture<Void>> futures = new ArrayList<>();
        
        for (int i = 0; i < instanceCount; i++) {
            futures.add(CompletableFuture.runAsync(() -> {
                try {
                    runtimeService.startProcessInstanceByKey(processKey, variables);
                    successCount.incrementAndGet();
                } catch (Exception e) {
                    failureCount.incrementAndGet();
                    log.error("Failed to start process instance", e);
                }
            }, executorService));
        }

        // Wait for all to complete
        try {
            CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).get(60, TimeUnit.SECONDS);
        } catch (Exception e) {
            log.error("Timeout or interruption during load test execution", e);
        }

        Instant endTime = Instant.now();
        long durationMs = java.time.Duration.between(startTime, endTime).toMillis();
        
        TestRunResult result = new TestRunResult(
                testId,
                processKey,
                instanceCount,
                successCount.get(),
                failureCount.get(),
                startTime.toString(),
                durationMs
        );
        
        testHistory.add(0, result); // Add to head of list
        return result;
    }

    public List<TestRunResult> getTestHistory() {
        return testHistory;
    }

    public record TestRunResult(
            String id,
            String processKey,
            int totalRequested,
            int successes,
            int failures,
            String startedAt,
            long durationMs
    ) {}
}
