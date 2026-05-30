package com.yusolbin.bio_os.dto;

public class SimulationRequest {

    private double water;
    private double light;
    private double temperature;

    public SimulationRequest() {
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
}
