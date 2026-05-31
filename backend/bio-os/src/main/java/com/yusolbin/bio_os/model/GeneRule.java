package com.yusolbin.bio_os.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

import java.time.LocalDateTime;

@Entity
public class GeneRule {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String fieldName;
    private String operator;
    private double threshold;
    private String targetState;
    private double energyEffect;

    private boolean active;

    private LocalDateTime createdAt;

    public GeneRule(){

    }

    public GeneRule(String fieldName, String operator, double threshold, String targetState, double energyEffect) {
        this.fieldName = fieldName;
        this.operator = operator;
        this.threshold = threshold;
        this.targetState = targetState;
        this.energyEffect = energyEffect;
        this.active = true;
        this.createdAt = LocalDateTime.now();
    }

    public Long getId() {
        return id;
    }

    public String getFieldName() {
        return fieldName;
    }

    public String getOperator() {
        return operator;
    }

    public double getThreshold() {
        return threshold;
    }

    public String getTargetState() {
        return targetState;
    }

    public boolean isActive() {
        return active;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public double getEnergyEffect() {
        return energyEffect;
    }

    public void toggleActive() {
        this.active = !this.active;
    }

    public void deactivate() {
        this.active = false;
    }
}
