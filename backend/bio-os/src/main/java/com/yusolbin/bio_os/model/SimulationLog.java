package com.yusolbin.bio_os.model;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
public class SimulationLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private int tick;

    private double water;
    private double light;
    private double temperature;
    private double humidity;

    private double totalEnergy;

    private String lastAction;
    private String visualState;

    private String activeStates;

    private LocalDateTime createdAt;

    private double energyDelta;

    @jakarta.persistence.Column(columnDefinition = "TEXT")
    private String matchedRules;

    @jakarta.persistence.Column(columnDefinition = "TEXT")
    private String recommendation;

    private String riskLevel;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_account_id")
    private UserAccount userAccount;
    
    public SimulationLog() {
    }

    public SimulationLog(
        int tick,
        double water,
        double light,
        double temperature,
        double humidity,
        double totalEnergy,
        String lastAction,
        String visualState,
        String activeStates,
        double energyDelta,
        String matchedRules,
        String riskLevel,
        String recommendation
    ) {
        this.tick = tick;
        this.water = water;
        this.light = light;
        this.temperature = temperature;
        this.humidity = humidity;
        this.totalEnergy = totalEnergy;
        this.lastAction = lastAction;
        this.visualState = visualState;
        this.activeStates = activeStates;
        this.energyDelta = energyDelta;
        this.matchedRules = matchedRules;
        this.riskLevel = riskLevel;
        this.recommendation = recommendation;
        this.createdAt = LocalDateTime.now();
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

    public double getHumidity() {
        return humidity;
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

    public String getActiveStates() {
        return activeStates;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public double getEnergyDelta() {
    return energyDelta;
    }

    public String getMatchedRules() {
        return matchedRules;
    }

    public String getRiskLevel() {
        return riskLevel;
    }

    public String getRecommendation() {
        return recommendation;
    }

    public UserAccount getUserAccount() {
        return userAccount;
    }

    public void setUserAccount(UserAccount userAccount){
        this.userAccount = userAccount;
    }
}