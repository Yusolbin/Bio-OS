package com.yusolbin.bio_os.dto;

import java.util.List;

public class AiPredictionRequest {

    private double water;
    private double light;
    private double temperature;
    private double humidity;

    private double totalEnergy;
    private double growthScore;

    private String riskLevel;
    private String visualState;

    private List<String> activeStates;

    public AiPredictionRequest() {
    }

    public double getWater() {
        return water;
    }

    public void setWater(double water) {
        this.water = water;
    }

    public double getLight() {
        return light;
    }

    public void setLight(double light) {
        this.light = light;
    }

    public double getTemperature() {
        return temperature;
    }

    public void setTemperature(double temperature) {
        this.temperature = temperature;
    }

    public double getHumidity() {
        return humidity;
    }

    public void setHumidity(double humidity) {
        this.humidity = humidity;
    }

    public double getTotalEnergy() {
        return totalEnergy;
    }

    public void setTotalEnergy(double totalEnergy) {
        this.totalEnergy = totalEnergy;
    }

    public double getGrowthScore() {
        return growthScore;
    }

    public void setGrowthScore(double growthScore) {
        this.growthScore = growthScore;
    }

    public String getRiskLevel() {
        return riskLevel;
    }

    public void setRiskLevel(String riskLevel) {
        this.riskLevel = riskLevel;
    }

    public String getVisualState() {
        return visualState;
    }

    public void setVisualState(String visualState) {
        this.visualState = visualState;
    }

    public List<String> getActiveStates() {
        return activeStates;
    }

    public void setActiveStates(List<String> activeStates) {
        this.activeStates = activeStates;
    }
}