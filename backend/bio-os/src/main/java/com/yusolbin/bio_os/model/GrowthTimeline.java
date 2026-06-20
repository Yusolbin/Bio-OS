package com.yusolbin.bio_os.model;

import jakarta.persistence.*;

@Entity
public class GrowthTimeline {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "growth_simulation_id")
    private GrowthSimulation growthSimulation;

    private int day;

    private double water;
    private double light;
    private double temperature;
    private double humidity;

    private double growthScore;
    private double totalEnergy;

    private String visualState;
    private String riskLevel;

    @Column(length = 1000)
    private String activeStates;

    @Column(length = 2000)
    private String matchedRules;

    public GrowthTimeline() {
    }

    public GrowthTimeline(
            GrowthSimulation growthSimulation,
            int day,
            double water,
            double light,
            double temperature,
            double humidity,
            double growthScore,
            double totalEnergy,
            String visualState,
            String riskLevel,
            String activeStates,
            String matchedRules
    ) {
        this.growthSimulation = growthSimulation;
        this.day = day;
        this.water = water;
        this.light = light;
        this.temperature = temperature;
        this.humidity = humidity;
        this.growthScore = growthScore;
        this.totalEnergy = totalEnergy;
        this.visualState = visualState;
        this.riskLevel = riskLevel;
        this.activeStates = activeStates;
        this.matchedRules = matchedRules;
    }

    public Long getId() {
        return id;
    }

    public GrowthSimulation getGrowthSimulation() {
        return growthSimulation;
    }

    public int getDay() {
        return day;
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

    public double getHumidity() {
        return humidity;
    }

    public double getGrowthScore() {
        return growthScore;
    }

    public double getTotalEnergy() {
        return totalEnergy;
    }

    public String getVisualState() {
        return visualState;
    }

    public String getRiskLevel() {
        return riskLevel;
    }

    public String getActiveStates() {
        return activeStates;
    }

    public String getMatchedRules() {
        return matchedRules;
    }
}