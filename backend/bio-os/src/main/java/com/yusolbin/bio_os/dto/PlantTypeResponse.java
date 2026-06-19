package com.yusolbin.bio_os.dto;

import com.yusolbin.bio_os.model.PlantType;

import java.time.LocalDateTime;

public class PlantTypeResponse {

    private Long id;
    private String name;

    private double optimalWaterMin;
    private double optimalWaterMax;

    private double optimalLightMin;
    private double optimalLightMax;

    private double optimalTemperatureMin;
    private double optimalTemperatureMax;

    private double optimalHumidityMin;
    private double optimalHumidityMax;

    private double baseGrowthRate;

    private LocalDateTime createdAt;

    public PlantTypeResponse(PlantType plantType) {
        this.id = plantType.getId();
        this.name = plantType.getName();
        this.optimalWaterMin = plantType.getOptimalWaterMin();
        this.optimalWaterMax = plantType.getOptimalWaterMax();
        this.optimalLightMin = plantType.getOptimalLightMin();
        this.optimalLightMax = plantType.getOptimalLightMax();
        this.optimalTemperatureMin = plantType.getOptimalTemperatureMin();
        this.optimalTemperatureMax = plantType.getOptimalTemperatureMax();
        this.optimalHumidityMin = plantType.getOptimalHumidityMin();
        this.optimalHumidityMax = plantType.getOptimalHumidityMax();
        this.baseGrowthRate = plantType.getBaseGrowthRate();
        this.createdAt = plantType.getCreatedAt();
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public double getOptimalWaterMin() {
        return optimalWaterMin;
    }

    public double getOptimalWaterMax() {
        return optimalWaterMax;
    }

    public double getOptimalLightMin() {
        return optimalLightMin;
    }

    public double getOptimalLightMax() {
        return optimalLightMax;
    }

    public double getOptimalTemperatureMin() {
        return optimalTemperatureMin;
    }

    public double getOptimalTemperatureMax() {
        return optimalTemperatureMax;
    }

    public double getOptimalHumidityMin() {
        return optimalHumidityMin;
    }

    public double getOptimalHumidityMax() {
        return optimalHumidityMax;
    }

    public double getBaseGrowthRate() {
        return baseGrowthRate;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
}