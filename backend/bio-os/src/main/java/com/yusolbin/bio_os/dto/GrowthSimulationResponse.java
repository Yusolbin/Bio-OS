package com.yusolbin.bio_os.dto;

import com.yusolbin.bio_os.model.GrowthSimulation;

import java.time.LocalDateTime;
import java.util.List;

public class GrowthSimulationResponse {

    private Long simulationId;
    private String plantType;
    private int days;

    private double initialWater;
    private double initialLight;
    private double initialTemperature;
    private double initialHumidity;

    private double finalGrowthScore;
    private String finalRiskLevel;
    private String finalVisualState;

    private String summary;
    private LocalDateTime createdAt;

    private List<GrowthTimelineResponse> timeline;

    public GrowthSimulationResponse(
            GrowthSimulation simulation,
            List<GrowthTimelineResponse> timeline
    ) {
        this.simulationId = simulation.getId();
        this.plantType = simulation.getPlantType().getName();
        this.days = simulation.getDays();
        this.initialWater = simulation.getInitialWater();
        this.initialLight = simulation.getInitialLight();
        this.initialTemperature = simulation.getInitialTemperature();
        this.initialHumidity = simulation.getInitialHumidity();
        this.finalGrowthScore = simulation.getFinalGrowthScore();
        this.finalRiskLevel = simulation.getFinalRiskLevel();
        this.finalVisualState = simulation.getFinalVisualState();
        this.summary = simulation.getSummary();
        this.createdAt = simulation.getCreatedAt();
        this.timeline = timeline;
    }

    public Long getSimulationId() {
        return simulationId;
    }

    public String getPlantType() {
        return plantType;
    }

    public int getDays() {
        return days;
    }

    public double getInitialWater() {
        return initialWater;
    }

    public double getInitialLight() {
        return initialLight;
    }

    public double getInitialTemperature() {
        return initialTemperature;
    }

    public double getInitialHumidity() {
        return initialHumidity;
    }

    public double getFinalGrowthScore() {
        return finalGrowthScore;
    }

    public String getFinalRiskLevel() {
        return finalRiskLevel;
    }

    public String getFinalVisualState() {
        return finalVisualState;
    }

    public String getSummary() {
        return summary;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public List<GrowthTimelineResponse> getTimeline() {
        return timeline;
    }
}