package com.yusolbin.bio_os.model;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
public class PlantType {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
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

    public PlantType() {
    }

    public PlantType(
            String name,
            double optimalWaterMin,
            double optimalWaterMax,
            double optimalLightMin,
            double optimalLightMax,
            double optimalTemperatureMin,
            double optimalTemperatureMax,
            double optimalHumidityMin,
            double optimalHumidityMax,
            double baseGrowthRate
    ) {
        this.name = name;
        this.optimalWaterMin = optimalWaterMin;
        this.optimalWaterMax = optimalWaterMax;
        this.optimalLightMin = optimalLightMin;
        this.optimalLightMax = optimalLightMax;
        this.optimalTemperatureMin = optimalTemperatureMin;
        this.optimalTemperatureMax = optimalTemperatureMax;
        this.optimalHumidityMin = optimalHumidityMin;
        this.optimalHumidityMax = optimalHumidityMax;
        this.baseGrowthRate = baseGrowthRate;
    }

    @PrePersist
    public void prePersist() {
        this.createdAt = LocalDateTime.now();
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
