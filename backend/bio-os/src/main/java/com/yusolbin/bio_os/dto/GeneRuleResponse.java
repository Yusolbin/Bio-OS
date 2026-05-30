package com.yusolbin.bio_os.dto;

import com.yusolbin.bio_os.model.GeneRule;

import java.time.LocalDateTime;

public class GeneRuleResponse {

    private Long id;
    private String fieldName;
    private String operator;
    private double threshold;
    private String targetState;
    private boolean active;
    private LocalDateTime createdAt;

    public GeneRuleResponse(GeneRule geneRule) {
        this.id = geneRule.getId();
        this.fieldName = geneRule.getFieldName();
        this.operator = geneRule.getOperator();
        this.threshold = geneRule.getThreshold();
        this.targetState = geneRule.getTargetState();
        this.active = geneRule.isActive();
        this.createdAt = geneRule.getCreatedAt();
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
}