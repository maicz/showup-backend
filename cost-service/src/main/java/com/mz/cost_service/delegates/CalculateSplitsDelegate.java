package com.mz.cost_service.delegates;

import org.finos.fluxnova.bpm.engine.delegate.DelegateExecution;
import org.finos.fluxnova.bpm.engine.delegate.JavaDelegate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component("calculateSplitsDelegate")
public class CalculateSplitsDelegate implements JavaDelegate {
    private static final Logger log = LoggerFactory.getLogger(CalculateSplitsDelegate.class);

    @Override
    public void execute(DelegateExecution execution) throws Exception {
        log.info("Executing CalculateSplitsDelegate for process: {}", execution.getProcessInstanceId());
        
        Double totalCost = (Double) execution.getVariable("totalCost");
        Integer headcount = (Integer) execution.getVariable("headcount");
        
        if (totalCost == null) totalCost = 100.0;
        if (headcount == null || headcount <= 0) headcount = 5;
        
        double costPerPerson = totalCost / headcount;
        log.info("Calculated cost splitting: Total={} split among Headcount={} -> CostPerPerson={}", 
                totalCost, headcount, costPerPerson);
                
        execution.setVariable("costPerPerson", costPerPerson);
    }
}
