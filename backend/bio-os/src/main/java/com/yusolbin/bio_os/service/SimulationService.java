package com.yusolbin.bio_os.service;

import com.yusolbin.bio_os.dto.SimulationRequest;
import com.yusolbin.bio_os.dto.SimulationResponse;
import com.yusolbin.bio_os.model.SimulationLog;
import com.yusolbin.bio_os.repository.SimulationLogRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class SimulationService {

    private final SimulationLogRepository simulationLogRepository;

    private int tick = 0;

    public SimulationService(SimulationLogRepository simulationLogRepository) {
        this.simulationLogRepository = simulationLogRepository;
    }

    public SimulationResponse runSimulation(SimulationRequest request) {
        tick++;

        double water = request.getWater();
        double light = request.getLight();
        double temperature = request.getTemperature();

        List<String> activeStates = evaluateActiveStates(water, light, temperature);

        double totalEnergy = calculateTotalEnergy(water, light, temperature, activeStates);

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

    private List<String> evaluateActiveStates(double water, double light, double temperature) {
        List<String> activeStates = new ArrayList<>();

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

        return activeStates;
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
}