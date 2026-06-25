package com.yusolbin.bio_os.service;

import com.yusolbin.bio_os.dto.AiPredictionRequest;
import com.yusolbin.bio_os.dto.AiPredictionResponse;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class AiPredictionService {

    public AiPredictionResponse predict(AiPredictionRequest request) {
        List<String> riskFactors = new ArrayList<>();

        double riskScore = calculateRiskScore(request, riskFactors);
        double survivalProbability = clamp(100.0 - riskScore, 0, 100);
        double growthPotential = calculateGrowthPotential(request, riskScore);
        double confidenceScore = calculateConfidenceScore(request);

        String predictionLabel = decidePredictionLabel(riskScore, survivalProbability, growthPotential);
        String recommendedAction = makeRecommendedAction(riskFactors, request);
        String reason = makeReason(predictionLabel, riskFactors, request);

        return new AiPredictionResponse(
                predictionLabel,
                roundOne(survivalProbability),
                roundOne(growthPotential),
                roundOne(riskScore),
                roundOne(confidenceScore),
                recommendedAction,
                reason,
                riskFactors,
                "JAVA_AI_PREDICTION_ENGINE"
        );
    }

    private double calculateRiskScore(AiPredictionRequest request, List<String> riskFactors) {
        double score = 0.0;

        double water = request.getWater();
        double light = request.getLight();
        double temperature = request.getTemperature();
        double humidity = request.getHumidity();
        double totalEnergy = request.getTotalEnergy();
        double growthScore = request.getGrowthScore();

        if (water < 30) {
            score += 26;
            riskFactors.add("LOW_WATER");
        } else if (water > 120) {
            score += 14;
            riskFactors.add("OVER_WATER");
        }

        if (light < 30) {
            score += 18;
            riskFactors.add("LOW_LIGHT");
        } else if (light > 95) {
            score += 10;
            riskFactors.add("EXCESS_LIGHT");
        }

        if (temperature < 10) {
            score += 22;
            riskFactors.add("COLD_STRESS");
        } else if (temperature > 35) {
            score += 28;
            riskFactors.add("HEAT_STRESS");
        }

        if (humidity < 30) {
            score += 12;
            riskFactors.add("LOW_HUMIDITY");
        } else if (humidity > 85) {
            score += 10;
            riskFactors.add("HIGH_HUMIDITY");
        }

        if (totalEnergy > 0 && totalEnergy < 40) {
            score += 22;
            riskFactors.add("LOW_TOTAL_ENERGY");
        }

        if (growthScore > 0 && growthScore < 30) {
            score += 20;
            riskFactors.add("LOW_GROWTH_SCORE");
        }

        String riskLevel = request.getRiskLevel();

        if ("CRITICAL".equals(riskLevel)) {
            score += 28;
            riskFactors.add("CRITICAL_RISK_LEVEL");
        } else if ("HIGH".equals(riskLevel)) {
            score += 20;
            riskFactors.add("HIGH_RISK_LEVEL");
        } else if ("MEDIUM".equals(riskLevel)) {
            score += 10;
            riskFactors.add("MEDIUM_RISK_LEVEL");
        }

        List<String> activeStates = request.getActiveStates();

        if (activeStates != null) {
            if (activeStates.contains("DroughtMode")) {
                score += 18;
                riskFactors.add("DROUGHT_MODE");
            }

            if (activeStates.contains("HeatStress")) {
                score += 18;
                riskFactors.add("HEAT_STRESS_STATE");
            }

            if (activeStates.contains("ColdStress")) {
                score += 14;
                riskFactors.add("COLD_STRESS_STATE");
            }

            if (activeStates.contains("LowLightStress")) {
                score += 12;
                riskFactors.add("LOW_LIGHT_STATE");
            }

            if (activeStates.contains("OverWatered")) {
                score += 10;
                riskFactors.add("OVER_WATERED_STATE");
            }
        }

        return clamp(score, 0, 100);
    }

    private double calculateGrowthPotential(AiPredictionRequest request, double riskScore) {
        double potential = 100.0 - riskScore;

        if (request.getWater() >= 50 && request.getWater() <= 90) {
            potential += 8;
        }

        if (request.getLight() >= 60 && request.getLight() <= 90) {
            potential += 8;
        }

        if (request.getTemperature() >= 20 && request.getTemperature() <= 28) {
            potential += 8;
        }

        if (request.getHumidity() >= 40 && request.getHumidity() <= 70) {
            potential += 6;
        }

        if (request.getTotalEnergy() >= 100) {
            potential += 6;
        }

        return clamp(potential, 0, 100);
    }

    private double calculateConfidenceScore(AiPredictionRequest request) {
        double confidence = 70.0;

        if (request.getTotalEnergy() > 0) {
            confidence += 10;
        }

        if (request.getGrowthScore() > 0) {
            confidence += 10;
        }

        if (request.getActiveStates() != null && !request.getActiveStates().isEmpty()) {
            confidence += 10;
        }

        return clamp(confidence, 0, 100);
    }

    private String decidePredictionLabel(
            double riskScore,
            double survivalProbability,
            double growthPotential
    ) {
        if (riskScore >= 80 || survivalProbability <= 20) {
            return "CRITICAL_SURVIVAL_RISK";
        }

        if (riskScore >= 60) {
            return "HIGH_RISK_GROWTH";
        }

        if (riskScore >= 35 || growthPotential < 50) {
            return "UNSTABLE_GROWTH";
        }

        return "STABLE_GROWTH";
    }

    private String makeRecommendedAction(
            List<String> riskFactors,
            AiPredictionRequest request
    ) {
        if (riskFactors.contains("LOW_WATER") || riskFactors.contains("DROUGHT_MODE")) {
            return "Increase water input gradually and monitor total energy recovery.";
        }

        if (riskFactors.contains("HEAT_STRESS") || riskFactors.contains("HEAT_STRESS_STATE")) {
            return "Lower temperature and avoid high-light stress until the plant stabilizes.";
        }

        if (riskFactors.contains("LOW_LIGHT") || riskFactors.contains("LOW_LIGHT_STATE")) {
            return "Increase light exposure to support photosynthesis.";
        }

        if (riskFactors.contains("OVER_WATER") || riskFactors.contains("OVER_WATERED_STATE")) {
            return "Reduce water input and allow the system to stabilize.";
        }

        if (riskFactors.contains("LOW_TOTAL_ENERGY")) {
            return "Stabilize water, light, and temperature to recover total energy.";
        }

        return "Maintain the current environment and continue monitoring the growth timeline.";
    }

    private String makeReason(
            String predictionLabel,
            List<String> riskFactors,
            AiPredictionRequest request
    ) {
        if (riskFactors.isEmpty()) {
            return "Current environment is close to a stable growth condition.";
        }

        return predictionLabel
                + " was predicted because the system detected: "
                + String.join(", ", riskFactors)
                + ".";
    }

    private double clamp(double value, double min, double max) {
        if (value < min) {
            return min;
        }

        if (value > max) {
            return max;
        }

        return value;
    }

    private double roundOne(double value) {
        return Math.round(value * 10.0) / 10.0;
    }
}