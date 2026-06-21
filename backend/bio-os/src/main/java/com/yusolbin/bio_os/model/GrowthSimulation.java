package com.yusolbin.bio_os.model;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
public class GrowthSimulation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "plant_type_id")
    private PlantType plantType;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_account_id")
    private UserAccount userAccount;

    private int days;

    private double initialWater;
    private double initialLight;
    private double initialTemperature;
    private double initialHumidity;

    private double finalGrowthScore;
    private String finalRiskLevel;
    private String finalVisualState;

    @Column(length = 1000)
    private String summary;

    private LocalDateTime createdAt;

    public GrowthSimulation() {
    }

    public GrowthSimulation(
            PlantType plantType,
            int days,
            double initialWater,
            double initialLight,
            double initialTemperature,
            double initialHumidity,
            double finalGrowthScore,
            String finalRiskLevel,
            String finalVisualState,
            String summary
    ) {
        this.plantType = plantType;
        this.days = days;
        this.initialWater = initialWater;
        this.initialLight = initialLight;
        this.initialTemperature = initialTemperature;
        this.initialHumidity = initialHumidity;
        this.finalGrowthScore = finalGrowthScore;
        this.finalRiskLevel = finalRiskLevel;
        this.finalVisualState = finalVisualState;
        this.summary = summary;
    }

    @PrePersist
    public void prePersist() {
        this.createdAt = LocalDateTime.now();
    }

    public Long getId() {
        return id;
    }

    public PlantType getPlantType() {
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

    public UserAccount getUserAccount() {
        return userAccount;
    }

    public void setUserAccount(UserAccount userAccount){
        this.userAccount = userAccount;
    }
}