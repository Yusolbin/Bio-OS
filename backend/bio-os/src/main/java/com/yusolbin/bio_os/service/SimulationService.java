package com.yusolbin.bio_os.service;

import com.yusolbin.bio_os.dto.SimulationRequest;
import com.yusolbin.bio_os.dto.SimulationResponse;
import com.yusolbin.bio_os.dto.SimulationLogResponse;
import com.yusolbin.bio_os.model.SimulationLog;
import com.yusolbin.bio_os.model.GeneRule;
import com.yusolbin.bio_os.repository.GeneRuleRepository;
import com.yusolbin.bio_os.repository.SimulationLogRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class SimulationService {

    private final SimulationLogRepository simulationLogRepository;
    private final GeneRuleRepository geneRuleRepository;

    private int tick = 0;

    public SimulationService(
        SimulationLogRepository simulationLogRepository,
        GeneRuleRepository geneRuleRepository
    ) {
        this.simulationLogRepository = simulationLogRepository;
        this.geneRuleRepository = geneRuleRepository;
    }

    public SimulationResponse runSimulation(SimulationRequest request) {
        tick++;

        double water = request.getWater();
        double light = request.getLight();
        double temperature = request.getTemperature();

        List<String> activeStates = evaluateActiveStates(water, light, temperature);

        double totalEnergy = calculateTotalEnergy(water, light, temperature, activeStates);

        double ruleEnergyEffect = calculateRuleEnergyEffect(water, light, temperature);

        totalEnergy += ruleEnergyEffect;

        if (totalEnergy < 0) {
            totalEnergy = 0;
        }

        if (totalEnergy > 160) {
            totalEnergy = 160;
        }

        String lastAction = decideLastAction(activeStates, totalEnergy);

        String visualState = decideVisualState(activeStates, lastAction, totalEnergy);

        String activeStatesText = String.join(",", activeStates);

        SimulationLog log = new SimulationLog(
                tick,
                water,
                light,
                temperature,
                totalEnergy,
                lastAction,
                visualState,
                activeStatesText
        );

        SimulationLog savedLog = simulationLogRepository.save(log);

        return new SimulationResponse(
                savedLog.getId(),
                tick,
                water,
                light,
                temperature,
                totalEnergy,
                lastAction,
                visualState,
                activeStates
        );
    }

    public List<SimulationLogResponse> getSimulationLogs() {
    return simulationLogRepository.findAllByOrderByIdDesc()
            .stream()
            .map(SimulationLogResponse::new)
            .toList();
    }

    public void clearSimulationLogs() {
        simulationLogRepository.deleteAll();
    }

    private List<String> evaluateActiveStates(double water, double light, double temperature) {
    List<String> activeStates = new ArrayList<>();

    List<GeneRule> activeRules = geneRuleRepository.findByActiveTrue();

    if (activeRules.isEmpty()) {
        addDefaultRules(activeStates, water, light, temperature);
        return activeStates;
    }

    for (GeneRule rule : activeRules) {
        double value = getEnvironmentValue(rule.getFieldName(), water, light, temperature);

        if (evaluateCondition(value, rule.getOperator(), rule.getThreshold())) {
            activeStates.add(rule.getTargetState());
        }
    }

        return activeStates;
    }

    private void addDefaultRules(
        List<String> activeStates,
        double water,
        double light,
        double temperature
) {
    if (water < 30) {
        activeStates.add("DroughtMode");
    }

    if (light > 70) {
        activeStates.add("PhotosynthesisBoost");
    }

    if (temperature > 35) {
        activeStates.add("HeatStress");
    }

    if (water < 10) {
        activeStates.add("PruningMode");
    }

    if (water > 100) {
        activeStates.add("RecoveryMode");
    }
}

private double getEnvironmentValue(
        String fieldName,
        double water,
        double light,
        double temperature
) {
    if ("Water".equalsIgnoreCase(fieldName)) {
        return water;
    }

    if ("Light".equalsIgnoreCase(fieldName)) {
        return light;
    }

    if ("Temperature".equalsIgnoreCase(fieldName)) {
        return temperature;
    }

    return 0;
}

private boolean evaluateCondition(double value, String operator, double threshold) {
    if ("<".equals(operator) || "LT".equalsIgnoreCase(operator)) {
        return value < threshold;
    }

    if (">".equals(operator) || "GT".equalsIgnoreCase(operator)) {
        return value > threshold;
    }

    if ("<=".equals(operator) || "LTE".equalsIgnoreCase(operator)) {
        return value <= threshold;
    }

    if (">=".equals(operator) || "GTE".equalsIgnoreCase(operator)) {
        return value >= threshold;
    }

    if ("==".equals(operator) || "=".equals(operator) || "EQ".equalsIgnoreCase(operator)) {
        return value == threshold;
    }

        return false;
    }

    private double calculateTotalEnergy(
            double water,
            double light,
            double temperature,
            List<String> activeStates
    ) {
        double totalEnergy = 100.0;

        totalEnergy += light * 0.35;
        totalEnergy -= Math.max(0, 30 - water) * 1.4;
        totalEnergy -= Math.max(0, temperature - 32) * 2.2;

        if (activeStates.contains("RecoveryMode")) {
            totalEnergy += 25;
        }

        if (activeStates.contains("HeatStress")) {
            totalEnergy -= 20;
        }

        if (activeStates.contains("DroughtMode")) {
            totalEnergy -= 30;
        }

        if (totalEnergy < 0) {
            totalEnergy = 0;
        }

        if (totalEnergy > 160) {
            totalEnergy = 160;
        }

        return totalEnergy;
    }

    private String decideLastAction(List<String> activeStates, double totalEnergy) {
        if (activeStates.contains("PruningMode")) {
            return tick % 2 == 0 ? "PruningAlreadyExecuted" : "Pruning";
        }

        if (totalEnergy < 25) {
            return "Pruning";
        }

        return "Stable";
    }

    private String decideVisualState(List<String> activeStates, String lastAction, double totalEnergy) {
        if (totalEnergy <= 5) {
            return "dead_critical";
        }

        if (activeStates.contains("RecoveryMode")) {
            return "recovery_mode";
        }

        if (activeStates.contains("HeatStress")) {
            return "heat_stress";
        }

        if (activeStates.contains("DroughtMode")) {
            return "drought_mode";
        }

        if ("PruningAlreadyExecuted".equals(lastAction)) {
            return "pruning_already_executed";
        }

        if ("Pruning".equals(lastAction)) {
            return "pruned";
        }

        if (activeStates.contains("PhotosynthesisBoost")) {
            return "photosynthesis_boost";
        }

        if (totalEnergy < 40) {
            return "low_energy";
        }

        return "stable";
    }

    private double calculateRuleEnergyEffect(double water, double light, double temperature) {
        double totalEffect = 0.0;

        List<GeneRule> activeRules = geneRuleRepository.findByActiveTrue();

        for (GeneRule rule : activeRules) {
            double value = getEnvironmentValue(rule.getFieldName(), water, light, temperature);

            if (evaluateCondition(value, rule.getOperator(), rule.getThreshold())) {
                totalEffect += rule.getEnergyEffect();
            }
        }

        return totalEffect;
    }


}