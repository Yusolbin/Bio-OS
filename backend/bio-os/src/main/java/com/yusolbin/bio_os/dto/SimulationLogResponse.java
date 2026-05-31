package com.yusolbin.bio_os.dto;

import com.yusolbin.bio_os.model.SimulationLog;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

public class SimulationLogResponse {

    private Long id;
    private int tick;

    private double water;
    private double light;
    private double temperature;

    private double totalEnergy;
    private double energyDelta;

    private String lastAction;
    private String visualState;
    private String riskLevel;
    private String recommendation;

    private List<String> activeStates;
    private List<String> matchedRules;

    private LocalDateTime createdAt;

    public SimulationLogResponse(SimulationLog log) {
        this.id = log.getId();
        this.tick = log.getTick();
        this.water = log.getWater();
        this.light = log.getLight();
        this.temperature = log.getTemperature();
        this.totalEnergy = log.getTotalEnergy();
        this.lastAction = log.getLastAction();
        this.visualState = log.getVisualState();
        this.activeStates = parseActiveStates(log.getActiveStates());
        this.createdAt = log.getCreatedAt();
        this.energyDelta = log.getEnergyDelta();
        this.matchedRules = parseTextList(log.getMatchedRules());
        this.riskLevel = log.getRiskLevel();
        this.recommendation = log.getRecommendation();
    }

    private List<String> parseActiveStates(String activeStatesText) {
        if (activeStatesText == null || activeStatesText.isBlank()) {
            return List.of();
        }

        return Arrays.stream(activeStatesText.split(","))
                .map(String::trim)
                .filter(state -> !state.isBlank())
                .toList();
    }

    private List<String> parseTextList(String text){
        if (text == null || text.isBlank()) {
            return List.of();
        }

        return Arrays.stream(text.split("\\|"))
                .map(String::trim)
                .filter(value->!value.isBlank())
                .toList();
    }

    public Long getId() {
        return id;
    }

    public int getTick() {
        return tick;
    }

    public double getWater() {
        return water;
    }

    public double getLight() {
        return light;
    }

    public double getTemperature() {
        return temperature;
    }

    public double getTotalEnergy() {
        return totalEnergy;
    }

    public String getLastAction() {
        return lastAction;
    }

    public String getVisualState() {
        return visualState;
    }

    public List<String> getActiveStates() {
        return activeStates;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public double getEnergyDelta(){
        return energyDelta;
    }

    public List<String> getMatchedRules(){
        return matchedRules;
    }

    public String getRiskLeve(){
        return riskLevel;
    }

    public String getRecommenation() {
        return recommendation;
    }
}