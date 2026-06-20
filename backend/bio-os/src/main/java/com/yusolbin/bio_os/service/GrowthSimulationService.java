package com.yusolbin.bio_os.service;

import com.yusolbin.bio_os.dto.GrowthSimulationRequest;
import com.yusolbin.bio_os.dto.GrowthSimulationResponse;
import com.yusolbin.bio_os.dto.GrowthTimelineResponse;
import com.yusolbin.bio_os.model.GrowthSimulation;
import com.yusolbin.bio_os.model.GrowthTimeline;
import com.yusolbin.bio_os.model.PlantType;
import com.yusolbin.bio_os.repository.GrowthSimulationRepository;
import com.yusolbin.bio_os.repository.GrowthTimelineRepository;
import com.yusolbin.bio_os.repository.PlantTypeRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

@Service
public class GrowthSimulationService {

    private final PlantTypeRepository plantTypeRepository;
    private final GrowthSimulationRepository growthSimulationRepository;
    private final GrowthTimelineRepository growthTimelineRepository;

    public GrowthSimulationService(
            PlantTypeRepository plantTypeRepository,
            GrowthSimulationRepository growthSimulationRepository,
            GrowthTimelineRepository growthTimelineRepository
    ) {
        this.plantTypeRepository = plantTypeRepository;
        this.growthSimulationRepository = growthSimulationRepository;
        this.growthTimelineRepository = growthTimelineRepository;
    }

    @Transactional
    public GrowthSimulationResponse simulateGrowth(GrowthSimulationRequest request) {
        PlantType plantType = plantTypeRepository.findById(request.getPlantTypeId())
                .orElseThrow(() -> new IllegalArgumentException("PlantType not found: " + request.getPlantTypeId()));

        int days = normalizeDays(request.getDays());

        double water = request.getWater();
        double light = request.getLight();
        double temperature = request.getTemperature();
        double humidity = request.getHumidity();

        double growthScore = 50.0;

        List<GrowthTimelineResponse> timelineResponses = new ArrayList<>();

        double finalGrowthScore = growthScore;
        double finalTotalEnergy = 100.0;
        String finalRiskLevel = "LOW";
        String finalVisualState = "stable";

        for (int day = 1; day <= days; day++) {
            List<String> activeStates = evaluateActiveStates(
                    plantType,
                    water,
                    light,
                    temperature,
                    humidity
            );

            List<String> matchedRules = buildMatchedRules(
                    plantType,
                    water,
                    light,
                    temperature,
                    humidity,
                    activeStates
            );

            double dailyGrowthDelta = calculateDailyGrowthDelta(plantType, activeStates);
            growthScore = clamp(growthScore + dailyGrowthDelta, 0, 100);

            double totalEnergy = calculateTotalEnergy(
                    plantType,
                    water,
                    light,
                    temperature,
                    humidity,
                    activeStates
            );

            String riskLevel = decideRiskLevel(growthScore, totalEnergy, activeStates);
            String visualState = decideVisualState(growthScore, totalEnergy, activeStates);

            timelineResponses.add(new GrowthTimelineResponse(
                    day,
                    water,
                    light,
                    temperature,
                    humidity,
                    roundOne(growthScore),
                    roundOne(totalEnergy),
                    visualState,
                    riskLevel,
                    activeStates,
                    matchedRules
            ));

            finalGrowthScore = growthScore;
            finalTotalEnergy = totalEnergy;
            finalRiskLevel = riskLevel;
            finalVisualState = visualState;
        }

        String summary = makeSummary(
                plantType.getName(),
                finalGrowthScore,
                finalTotalEnergy,
                finalRiskLevel
        );

        GrowthSimulation simulation = new GrowthSimulation(
                plantType,
                days,
                water,
                light,
                temperature,
                humidity,
                roundOne(finalGrowthScore),
                finalRiskLevel,
                finalVisualState,
                summary
        );

        GrowthSimulation savedSimulation = growthSimulationRepository.save(simulation);

        List<GrowthTimeline> timelineEntities = timelineResponses.stream()
                .map(response -> new GrowthTimeline(
                        savedSimulation,
                        response.getDay(),
                        response.getWater(),
                        response.getLight(),
                        response.getTemperature(),
                        response.getHumidity(),
                        response.getGrowthScore(),
                        response.getTotalEnergy(),
                        response.getVisualState(),
                        response.getRiskLevel(),
                        String.join(",", response.getActiveStates()),
                        String.join("|", response.getMatchedRules())
                ))
                .toList();

        growthTimelineRepository.saveAll(timelineEntities);

        return new GrowthSimulationResponse(savedSimulation, timelineResponses);
    }

    private int normalizeDays(int days) {
        if (days <= 0) {
            return 30;
        }

        return Math.min(days, 365);
    }

    private List<String> evaluateActiveStates(
            PlantType plantType,
            double water,
            double light,
            double temperature,
            double humidity
    ) {
        List<String> activeStates = new ArrayList<>();

        if (water < plantType.getOptimalWaterMin()) {
            activeStates.add("DroughtMode");
        } else if (water > plantType.getOptimalWaterMax()) {
            activeStates.add("OverWatered");
        } else {
            activeStates.add("OptimalWater");
        }

        if (light < plantType.getOptimalLightMin()) {
            activeStates.add("LowLightStress");
        } else if (light > plantType.getOptimalLightMax()) {
            activeStates.add("LightStress");
        } else {
            activeStates.add("PhotosynthesisBoost");
        }

        if (temperature < plantType.getOptimalTemperatureMin()) {
            activeStates.add("ColdStress");
        } else if (temperature > plantType.getOptimalTemperatureMax()) {
            activeStates.add("HeatStress");
        } else {
            activeStates.add("OptimalTemperature");
        }

        if (humidity < plantType.getOptimalHumidityMin()) {
            activeStates.add("LowHumidityStress");
        } else if (humidity > plantType.getOptimalHumidityMax()) {
            activeStates.add("HighHumidityStress");
        } else {
            activeStates.add("OptimalHumidity");
        }

        return activeStates;
    }

    private List<String> buildMatchedRules(
            PlantType plantType,
            double water,
            double light,
            double temperature,
            double humidity,
            List<String> activeStates
    ) {
        List<String> matchedRules = new ArrayList<>();

        matchedRules.add(
                "Water " + formatOne(water)
                        + " compared with optimal range "
                        + formatOne(plantType.getOptimalWaterMin())
                        + " ~ "
                        + formatOne(plantType.getOptimalWaterMax())
                        + " => "
                        + findWaterState(activeStates)
        );

        matchedRules.add(
                "Light " + formatOne(light)
                        + " compared with optimal range "
                        + formatOne(plantType.getOptimalLightMin())
                        + " ~ "
                        + formatOne(plantType.getOptimalLightMax())
                        + " => "
                        + findLightState(activeStates)
        );

        matchedRules.add(
                "Temperature " + formatOne(temperature)
                        + " compared with optimal range "
                        + formatOne(plantType.getOptimalTemperatureMin())
                        + " ~ "
                        + formatOne(plantType.getOptimalTemperatureMax())
                        + " => "
                        + findTemperatureState(activeStates)
        );

        matchedRules.add(
                "Humidity " + formatOne(humidity)
                        + " compared with optimal range "
                        + formatOne(plantType.getOptimalHumidityMin())
                        + " ~ "
                        + formatOne(plantType.getOptimalHumidityMax())
                        + " => "
                        + findHumidityState(activeStates)
        );

        return matchedRules;
    }

    private String findWaterState(List<String> activeStates) {
        if (activeStates.contains("DroughtMode")) return "DroughtMode";
        if (activeStates.contains("OverWatered")) return "OverWatered";
        return "OptimalWater";
    }

    private String findLightState(List<String> activeStates) {
        if (activeStates.contains("LowLightStress")) return "LowLightStress";
        if (activeStates.contains("LightStress")) return "LightStress";
        return "PhotosynthesisBoost";
    }

    private String findTemperatureState(List<String> activeStates) {
        if (activeStates.contains("ColdStress")) return "ColdStress";
        if (activeStates.contains("HeatStress")) return "HeatStress";
        return "OptimalTemperature";
    }

    private String findHumidityState(List<String> activeStates) {
        if (activeStates.contains("LowHumidityStress")) return "LowHumidityStress";
        if (activeStates.contains("HighHumidityStress")) return "HighHumidityStress";
        return "OptimalHumidity";
    }

    private double calculateDailyGrowthDelta(PlantType plantType, List<String> activeStates) {
        double delta = plantType.getBaseGrowthRate();

        if (activeStates.contains("OptimalWater")) delta += 0.8;
        if (activeStates.contains("PhotosynthesisBoost")) delta += 1.2;
        if (activeStates.contains("OptimalTemperature")) delta += 0.8;
        if (activeStates.contains("OptimalHumidity")) delta += 0.6;

        if (activeStates.contains("DroughtMode")) delta -= 3.0;
        if (activeStates.contains("OverWatered")) delta -= 1.8;
        if (activeStates.contains("LowLightStress")) delta -= 1.5;
        if (activeStates.contains("LightStress")) delta -= 1.0;
        if (activeStates.contains("ColdStress")) delta -= 2.0;
        if (activeStates.contains("HeatStress")) delta -= 2.5;
        if (activeStates.contains("LowHumidityStress")) delta -= 1.0;
        if (activeStates.contains("HighHumidityStress")) delta -= 0.8;

        return delta;
    }

    private double calculateTotalEnergy(
            PlantType plantType,
            double water,
            double light,
            double temperature,
            double humidity,
            List<String> activeStates
    ) {
        double energy = 100.0;

        energy += calculateRangeScore(water, plantType.getOptimalWaterMin(), plantType.getOptimalWaterMax(), 10, 0.7);
        energy += calculateRangeScore(light, plantType.getOptimalLightMin(), plantType.getOptimalLightMax(), 15, 0.5);
        energy += calculateRangeScore(temperature, plantType.getOptimalTemperatureMin(), plantType.getOptimalTemperatureMax(), 10, 1.5);
        energy += calculateRangeScore(humidity, plantType.getOptimalHumidityMin(), plantType.getOptimalHumidityMax(), 8, 0.4);

        if (activeStates.contains("PhotosynthesisBoost")) energy += 10;
        if (activeStates.contains("DroughtMode")) energy -= 25;
        if (activeStates.contains("HeatStress")) energy -= 20;
        if (activeStates.contains("ColdStress")) energy -= 15;
        if (activeStates.contains("LowLightStress")) energy -= 12;
        if (activeStates.contains("OverWatered")) energy -= 10;
        if (activeStates.contains("LowHumidityStress")) energy -= 8;
        if (activeStates.contains("HighHumidityStress")) energy -= 6;

        return clamp(energy, 0, 160);
    }

    private double calculateRangeScore(
            double value,
            double min,
            double max,
            double bonus,
            double penaltyRate
    ) {
        if (value >= min && value <= max) {
            return bonus;
        }

        if (value < min) {
            return -((min - value) * penaltyRate);
        }

        return -((value - max) * penaltyRate);
    }

    private String decideRiskLevel(
            double growthScore,
            double totalEnergy,
            List<String> activeStates
    ) {
        if (growthScore <= 10 || totalEnergy <= 10) {
            return "CRITICAL";
        }

        if (growthScore < 30 || totalEnergy < 40) {
            return "HIGH";
        }

        if (activeStates.contains("DroughtMode")
                || activeStates.contains("HeatStress")
                || activeStates.contains("ColdStress")
                || activeStates.contains("LowLightStress")
                || activeStates.contains("OverWatered")) {
            return "MEDIUM";
        }

        return "LOW";
    }

    private String decideVisualState(
            double growthScore,
            double totalEnergy,
            List<String> activeStates
    ) {
        if (growthScore <= 10 || totalEnergy <= 10) {
            return "dead_critical";
        }

        if (growthScore < 30 || totalEnergy < 40) {
            return "low_energy";
        }

        if (activeStates.contains("DroughtMode")) {
            return "drought_mode";
        }

        if (activeStates.contains("HeatStress")) {
            return "heat_stress";
        }

        if (activeStates.contains("ColdStress")) {
            return "cold_stress";
        }

        if (activeStates.contains("PhotosynthesisBoost")) {
            return "photosynthesis_boost";
        }

        return "stable";
    }

    private String makeSummary(
            String plantName,
            double finalGrowthScore,
            double finalTotalEnergy,
            String finalRiskLevel
    ) {
        if ("CRITICAL".equals(finalRiskLevel)) {
            return plantName + " is expected to face critical survival risk under the current environment.";
        }

        if ("HIGH".equals(finalRiskLevel)) {
            return plantName + " is expected to experience severe growth stress. Adjust the environment immediately.";
        }

        if ("MEDIUM".equals(finalRiskLevel)) {
            return plantName + " can survive, but growth may be unstable due to environmental stress.";
        }

        return plantName + " is expected to grow steadily under the current environment.";
    }

    private double clamp(double value, double min, double max) {
        if (value < min) return min;
        if (value > max) return max;
        return value;
    }

    private double roundOne(double value) {
        return Math.round(value * 10.0) / 10.0;
    }

    private String formatOne(double value) {
        return String.format(Locale.US, "%.1f", value);
    }
}