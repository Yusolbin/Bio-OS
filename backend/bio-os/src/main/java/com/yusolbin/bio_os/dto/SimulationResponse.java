package com.yusolbin.bio_os.dto;

import java.util.List;

public class SimulationResponse {

    private Long logId;
    private int tick;
    private double water;
    private double light;
    private double temperature;
    private double totalEnergy;
    private String lastAction;
    private String visualState;
    private List<String> activeStates;

    public SimulationResponse() {
    }

    public SimulationResponse(
            Long logId,
            int tick,
            double water,
            double light,
            double temperature,
            double totalEnergy,
            String lastAction,
            String visualState,
            List<String> activeStates
    ) {
        this.logId = logId;
        this.tick = tick;
        this.water = water;
        this.light = light;
        this.temperature = temperature;
        this.totalEnergy = totalEnergy;
        this.lastAction = lastAction;
        this.visualState = visualState;
        this.activeStates = activeStates;
    }

    public Long getLogId() {
        return logId;
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
}
