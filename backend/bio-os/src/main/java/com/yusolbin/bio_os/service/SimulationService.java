package com.yusolbin.bio_os.service;

import com.fasterxml.jackson.databind.JsonNode;

import com.yusolbin.bio_os.dto.SimulationLogResponse;
import com.yusolbin.bio_os.dto.SimulationRequest;
import com.yusolbin.bio_os.dto.SimulationResponse;
import com.yusolbin.bio_os.dto.CppEngineResult;
import com.yusolbin.bio_os.model.GeneRule;
import com.yusolbin.bio_os.model.SimulationLog;
import com.yusolbin.bio_os.repository.GeneRuleRepository;
import com.yusolbin.bio_os.repository.SimulationLogRepository;
import com.yusolbin.bio_os.repository.UserAccountRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
public class SimulationService {

    private final SimulationLogRepository simulationLogRepository;
    private final GeneRuleRepository geneRuleRepository;
    private final UserAccountRepository userAccountRepository;
    private final CppEngineBridgeService cppEngineBridgeService;

    private int tick = 0;

    public SimulationService(
            SimulationLogRepository simulationLogRepository,
            GeneRuleRepository geneRuleRepository,
            UserAccountRepository userAccountRepository,
            CppEngineBridgeService cppEngineBridgeService
    ) {
        this.simulationLogRepository = simulationLogRepository;
        this.geneRuleRepository = geneRuleRepository;
        this.userAccountRepository = userAccountRepository;
        this.cppEngineBridgeService = cppEngineBridgeService;
    }

    private static class RuleEvaluationResult {
        private final List<String> activeStates;
        private final List<String> matchedRules;
        private final double energyDelta;

        private RuleEvaluationResult(
                List<String> activeStates,
                List<String> matchedRules,
                double energyDelta
        ) {
            this.activeStates = activeStates;
            this.matchedRules = matchedRules;
            this.energyDelta = energyDelta;
        }
    }

    @Transactional
    public SimulationResponse runSimulation(SimulationRequest request) {
    tick++;

    double water = request.getWater();
    double light = request.getLight();
    double temperature = request.getTemperature();
    double humidity = request.getHumidity();

    CppEngineResult cppResult = cppEngineBridgeService.runEngine(
            water,
            light,
            temperature,
            humidity
    );

    if (cppResult.isSuccess() && cppResult.getSnapshot() != null) {
        return saveCppSimulationResult(
                request,
                cppResult,
                water,
                light,
                temperature,
                humidity
        );
    }

    return runJavaFallbackSimulation(
            request,
            water,
            light,
            temperature,
            humidity
    );
}

    @Transactional(readOnly = true)
    public List<SimulationLogResponse> getSimulationLogs(Long userId) {
        if (userId == null) {
            return simulationLogRepository.findAllByOrderByIdDesc()
                    .stream()
                    .map(SimulationLogResponse::new)
                    .toList();
        }

        return simulationLogRepository.findAllByUserAccount_IdOrderByIdDesc(userId)
                .stream()
                .map(SimulationLogResponse::new)
                .toList();
    }

    @Transactional
    public void clearSimulationLogs(Long userId) {
        if (userId == null) {
            simulationLogRepository.deleteAll();
            return;
        }

        simulationLogRepository.deleteByUserAccount_Id(userId);
    }

    private SimulationResponse saveCppSimulationResult(
        SimulationRequest request,
        CppEngineResult cppResult,
        double water,
        double light,
        double temperature,
        double humidity
) {
    JsonNode snapshot = cppResult.getSnapshot();
    JsonNode summary = snapshot.path("summary");

    double totalEnergy = summary.path("totalEnergy").asDouble(0.0);
    String lastAction = summary.path("lastAction").asText("Stable");

    List<String> activeStates = extractActiveStatesFromCppSnapshot(snapshot);
    List<String> matchedRules = extractLogMessagesFromCppSnapshot(snapshot);

    double energyDelta = 0.0;

    String visualState = decideVisualState(activeStates, lastAction, totalEnergy);
    String riskLevel = decideRiskLevel(totalEnergy, activeStates);
    String recommendation = makeRecommendation(riskLevel, activeStates, lastAction);

    String activeStatesText = String.join(",", activeStates);
    String matchedRulesText = String.join("|", matchedRules);

    SimulationLog log = new SimulationLog(
            tick,
            water,
            light,
            temperature,
            humidity,
            totalEnergy,
            lastAction,
            visualState,
            activeStatesText,
            energyDelta,
            matchedRulesText,
            riskLevel,
            recommendation
            
    );

    if (request.getUserId() != null) {
        userAccountRepository.findById(request.getUserId())
                .ifPresent(log::setUserAccount);
    }

    SimulationLog savedLog = simulationLogRepository.save(log);

    return new SimulationResponse(
            savedLog.getId(),
            tick,
            water,
            light,
            temperature,
            humidity,
            totalEnergy,
            lastAction,
            visualState,
            activeStates,
            energyDelta,
            matchedRules,
            riskLevel,
            recommendation,
            cppResult.getEngineSource()
    );
}

private SimulationResponse runJavaFallbackSimulation(
        SimulationRequest request,
        double water,
        double light,
        double temperature,
        double humidity
) {
    RuleEvaluationResult ruleResult = evaluateRules(water, light, temperature);

    List<String> activeStates = ruleResult.activeStates;
    List<String> matchedRules = ruleResult.matchedRules;
    double energyDelta = ruleResult.energyDelta;

    double totalEnergy = calculateTotalEnergy(water, light, temperature, activeStates);
    totalEnergy += energyDelta;
    totalEnergy = clampEnergy(totalEnergy);

    String lastAction = decideLastAction(activeStates, totalEnergy);
    String visualState = decideVisualState(activeStates, lastAction, totalEnergy);
    String riskLevel = decideRiskLevel(totalEnergy, activeStates);
    String recommendation = makeRecommendation(riskLevel, activeStates, lastAction);

    String activeStatesText = String.join(",", activeStates);
    String matchedRulesText = String.join("|", matchedRules);

    SimulationLog log = new SimulationLog(
            tick,
            water,
            light,
            temperature,
            humidity,
            totalEnergy,
            lastAction,
            visualState,
            activeStatesText,
            energyDelta,
            matchedRulesText,
            riskLevel,
            recommendation
    );

    if (request.getUserId() != null) {
        userAccountRepository.findById(request.getUserId())
                .ifPresent(log::setUserAccount);
    }

    SimulationLog savedLog = simulationLogRepository.save(log);

    return new SimulationResponse(
            savedLog.getId(),
            tick,
            water,
            light,
            temperature,
            humidity,
            totalEnergy,
            lastAction,
            visualState,
            activeStates,
            energyDelta,
            matchedRules,
            riskLevel,
            recommendation,
            "JAVA_FALLBACK"
    );
}

private List<String> extractActiveStatesFromCppSnapshot(JsonNode snapshot) {
    List<String> activeStates = new ArrayList<>();

    JsonNode statesNode = snapshot.path("states");

    if (statesNode.isObject()) {
        statesNode.fieldNames().forEachRemaining(stateName -> {
            JsonNode stateValue = statesNode.path(stateName);

            if (stateValue.asBoolean(false)) {
                activeStates.add(stateName);
            }
        });
    }

    return activeStates;
}

private List<String> extractLogMessagesFromCppSnapshot(JsonNode snapshot) {
    List<String> messages = new ArrayList<>();

    JsonNode logsNode = snapshot.path("logs");

    if (logsNode.isArray()) {
        for (JsonNode logNode : logsNode) {
            String type = logNode.path("type").asText("");
            String message = logNode.path("message").asText("");

            if (!message.isBlank()) {
                if (type.isBlank()) {
                    messages.add(message);
                } else {
                    messages.add(type + ": " + message);
                }
            }
        }
    }

    return messages;
}

    private RuleEvaluationResult evaluateRules(double water, double light, double temperature) {
        List<String> activeStates = new ArrayList<>();
        List<String> matchedRules = new ArrayList<>();
        double energyDelta = 0.0;

        List<GeneRule> activeRules = geneRuleRepository.findByActiveTrue();

        if (activeRules.isEmpty()) {
            addDefaultRules(activeStates, water, light, temperature);
            return new RuleEvaluationResult(activeStates, matchedRules, energyDelta);
        }

        for (GeneRule rule : activeRules) {
            double value = getEnvironmentValue(
                    rule.getFieldName(),
                    water,
                    light,
                    temperature
            );

            if (evaluateCondition(value, rule.getOperator(), rule.getThreshold())) {
                if (!activeStates.contains(rule.getTargetState())) {
                    activeStates.add(rule.getTargetState());
                }

                energyDelta += rule.getEnergyEffect();

                matchedRules.add(
                        "IF "
                                + rule.getFieldName()
                                + " "
                                + formatOperator(rule.getOperator())
                                + " "
                                + rule.getThreshold()
                                + " THEN "
                                + rule.getTargetState()
                                + " = ON / Effect "
                                + rule.getEnergyEffect()
                );
            }
        }

        return new RuleEvaluationResult(activeStates, matchedRules, energyDelta);
    }

    private void addDefaultRules(
            List<String> activeStates,
            double water,
            double light,
            double temperature
    ) {
        if (water < 30 && !activeStates.contains("DroughtMode")) {
            activeStates.add("DroughtMode");
        }

        if (light > 70 && !activeStates.contains("PhotosynthesisBoost")) {
            activeStates.add("PhotosynthesisBoost");
        }

        if (temperature > 35 && !activeStates.contains("HeatStress")) {
            activeStates.add("HeatStress");
        }

        if (water < 10 && !activeStates.contains("PruningMode")) {
            activeStates.add("PruningMode");
        }

        if (water > 100 && !activeStates.contains("RecoveryMode")) {
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

    private String formatOperator(String operator) {
        if ("LT".equalsIgnoreCase(operator) || "<".equals(operator)) {
            return "<";
        }

        if ("GT".equalsIgnoreCase(operator) || ">".equals(operator)) {
            return ">";
        }

        if ("LTE".equalsIgnoreCase(operator) || "<=".equals(operator)) {
            return "<=";
        }

        if ("GTE".equalsIgnoreCase(operator) || ">=".equals(operator)) {
            return ">=";
        }

        if ("EQ".equalsIgnoreCase(operator) || "=".equals(operator) || "==".equals(operator)) {
            return "=";
        }

        return operator;
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

        return clampEnergy(totalEnergy);
    }

    private double clampEnergy(double totalEnergy) {
        if (totalEnergy < 0) {
            return 0;
        }

        if (totalEnergy > 160) {
            return 160;
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

    private String decideVisualState(
            List<String> activeStates,
            String lastAction,
            double totalEnergy
    ) {
        if (totalEnergy <= 5) {
            return "dead_critical";
        }

        if (totalEnergy < 35) {
            return "low_energy";
        }

        if ("PruningAlreadyExecuted".equals(lastAction)) {
            return "pruning_already_executed";
        }

        if ("Pruning".equals(lastAction)
                || "PruningFailed".equals(lastAction)
                || activeStates.contains("PruningMode")) {
            return "pruned";
        }

        if (activeStates.contains("ExtremeDroughtMode")) {
            return "drought_mode";
        }

        if (activeStates.contains("HeatStress")) {
            return "heat_stress";
        }

        if (activeStates.contains("DroughtMode")) {
            return "drought_mode";
        }

        if (activeStates.contains("RecoveryMode")) {
            return "recovery_mode";
        }

        if (activeStates.contains("PhotosynthesisBoost")) {
            return "photosynthesis_boost";
        }

        if (activeStates.contains("ColdStress")) {
            return "cold_stress";
        }

        return "stable";
    }

    private String decideRiskLevel(double totalEnergy, List<String> activeStates) {
        if (totalEnergy <= 5) {
            return "CRITICAL";
        }

        if (totalEnergy < 35) {
            return "HIGH";
        }

        if (activeStates.contains("HeatStress")
                || activeStates.contains("DroughtMode")
                || activeStates.contains("ExtremeDroughtMode")
                || activeStates.contains("PruningMode")) {
            return "MEDIUM";
        }

        return "LOW";
    }

    private String makeRecommendation(
            String riskLevel,
            List<String> activeStates,
            String lastAction
    ) {
        if ("CRITICAL".equals(riskLevel)) {
            return "Energy level is critically low. Increase water input and reduce stress conditions immediately.";
        }

        if ("HIGH".equals(riskLevel)) {
            return "The plant is under severe stress. Review matched rules and adjust water, light, or temperature.";
        }

        if (activeStates.contains("DroughtMode")
                || activeStates.contains("ExtremeDroughtMode")) {
            return "Water-related stress is detected. Increase water input gradually.";
        }

        if (activeStates.contains("HeatStress")) {
            return "Heat stress is detected. Lower the temperature below the configured threshold.";
        }

        if ("Pruning".equals(lastAction)) {
            return "Pruning was triggered. Stabilize the environment before running additional ticks.";
        }

        if (activeStates.contains("RecoveryMode")) {
            return "Recovery mode is active. Maintain stable light and temperature conditions.";
        }

        return "Current environment is stable. Maintain the present condition.";
    }
}