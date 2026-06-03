package com.mz.fluxnova_testing.controller;

import com.mz.fluxnova_testing.service.LoadTestService;
import com.mz.fluxnova_testing.service.LoadTestService.TestRunResult;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/test")
@CrossOrigin(origins = "*")
public class TestHubController {

    private final LoadTestService loadTestService;

    public TestHubController(LoadTestService loadTestService) {
        this.loadTestService = loadTestService;
    }

    @GetMapping("/definitions")
    public List<Map<String, Object>> getDefinitions() {
        return loadTestService.getProcessDefinitions();
    }

    @GetMapping("/history")
    public List<TestRunResult> getHistory() {
        return loadTestService.getTestHistory();
    }

    @PostMapping("/run")
    public TestRunResult runTest(@RequestBody TestRequest request) {
        Map<String, Object> vars = request.variables();
        if (vars == null) {
            vars = new HashMap<>();
        }
        return loadTestService.runLoadTest(
                request.processKey(),
                request.count() <= 0 ? 10 : request.count(),
                vars
        );
    }

    @GetMapping("/stats")
    public Map<String, Object> getStats() {
        List<TestRunResult> history = loadTestService.getTestHistory();
        int totalTests = history.size();
        int totalInstances = 0;
        int totalSuccesses = 0;
        int totalFailures = 0;
        long totalDurationMs = 0;

        for (TestRunResult run : history) {
            totalInstances += run.totalRequested();
            totalSuccesses += run.successes();
            totalFailures += run.failures();
            totalDurationMs += run.durationMs();
        }

        double avgDuration = totalTests == 0 ? 0.0 : (double) totalDurationMs / totalTests;

        Map<String, Object> stats = new HashMap<>();
        stats.put("totalProcessDefinitions", loadTestService.getProcessDefinitions().size());
        stats.put("totalTestsRun", totalTests);
        stats.put("totalInstancesStarted", totalInstances);
        stats.put("totalSuccesses", totalSuccesses);
        stats.put("totalFailures", totalFailures);
        stats.put("averageDurationMs", Math.round(avgDuration * 10.0) / 10.0);

        return stats;
    }

    public record TestRequest(
            String processKey,
            int count,
            Map<String, Object> variables
    ) {}
}
