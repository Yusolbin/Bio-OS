package com.yusolbin.bio_os.dto;

import java.util.List;

public class AiPredictionResponse {

    private String predictionLabel;
    private double survivalProbability;
    private double growthPotential;
    private double riskScore;
    private double confidenceScore;

    private String recommendedAction;
    private String reason;
    private List<String> riskFactors;
    private String engineSource;

    public AiPredictionResponse(
            String predictionLabel,
            double survivalProbability,
            double growthPotential,
            double riskScore,
            double confidenceScore,
            String recommendedAction,
            String reason,
            List<String> riskFactors,
            String engineSource
    ) {
        this.predictionLabel = predictionLabel;
        this.survivalProbability = survivalProbability;
        this.growthPotential = growthPotential;
        this.riskScore = riskScore;
        this.confidenceScore = confidenceScore;
        this.recommendedAction = recommendedAction;
        this.reason = reason;
        this.riskFactors = riskFactors;
        this.engineSource = engineSource;
    }

    public String getPredictionLabel() {
        return predictionLabel;
    }

    public double getSurvivalProbability() {
        return survivalProbability;
    }

    public double getGrowthPotential() {
        return growthPotential;
    }

    public double getRiskScore() {
        return riskScore;
    }

    public double getConfidenceScore() {
        return confidenceScore;
    }

    public String getRecommendedAction() {
        return recommendedAction;
    }

    public String getReason() {
        return reason;
    }

    public List<String> getRiskFactors() {
        return riskFactors;
    }

    public String getEngineSource() {
        return engineSource;
    }
}