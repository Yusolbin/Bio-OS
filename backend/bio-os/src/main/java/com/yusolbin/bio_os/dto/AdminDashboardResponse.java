package com.yusolbin.bio_os.dto;

import com.yusolbin.bio_os.dto.AdminInsightResponse;

import java.util.List;

public class AdminDashboardResponse {

    private long plantTypeCount;
    private long geneRuleCount;
    private long simulationLogCount;
    private long growthSimulationCount;

    private double averageGrowthScore;

    private double averageWater;
    private double averageLight;
    private double averageTemperature;
    private double averageHumidity;

    private long criticalGrowthCount;
    private long highGrowthCount;
    private long mediumGrowthCount;
    private long lowGrowthCount;

    private String latestGrowthPlantType;
    private String latestGrowthRiskLevel;
    private String latestGrowthVisualState;
    private String overallStatus;

    private List<AdminInsightResponse> insights;

    public AdminDashboardResponse(
            long plantTypeCount,
            long geneRuleCount,
            long simulationLogCount,
            long growthSimulationCount,
            double averageGrowthScore,
            double averageWater,
            double averageLight,
            double averageTemperature,
            double averageHumidity,
            long criticalGrowthCount,
            long highGrowthCount,
            long mediumGrowthCount,
            long lowGrowthCount,
            String latestGrowthPlantType,
            String latestGrowthRiskLevel,
            String latestGrowthVisualState,
            String overallStatus,
            List<AdminInsightResponse> insights
    ) {
        this.plantTypeCount = plantTypeCount;
        this.geneRuleCount = geneRuleCount;
        this.simulationLogCount = simulationLogCount;
        this.growthSimulationCount = growthSimulationCount;
        this.averageGrowthScore = averageGrowthScore;
        this.averageWater = averageWater;
        this.averageLight = averageLight;
        this.averageTemperature = averageTemperature;
        this.averageHumidity = averageHumidity;
        this.criticalGrowthCount = criticalGrowthCount;
        this.highGrowthCount = highGrowthCount;
        this.mediumGrowthCount = mediumGrowthCount;
        this.lowGrowthCount = lowGrowthCount;
        this.latestGrowthPlantType = latestGrowthPlantType;
        this.latestGrowthRiskLevel = latestGrowthRiskLevel;
        this.latestGrowthVisualState = latestGrowthVisualState;
        this.overallStatus = overallStatus;
        this.insights = insights;
    }

    public long getPlantTypeCount() {
        return plantTypeCount;
    }

    public long getGeneRuleCount() {
        return geneRuleCount;
    }

    public long getSimulationLogCount() {
        return simulationLogCount;
    }

    public long getGrowthSimulationCount() {
        return growthSimulationCount;
    }

    public double getAverageGrowthScore() {
        return averageGrowthScore;
    }

    public double getAverageWater() {
        return averageWater;
    }

    public double getAverageLight() {
        return averageLight;
    }

    public double getAverageTemperature() {
        return averageTemperature;
    }

    public double getAverageHumidity() {
        return averageHumidity;
    }

    public long getCriticalGrowthCount() {
        return criticalGrowthCount;
    }

    public long getHighGrowthCount() {
        return highGrowthCount;
    }

    public long getMediumGrowthCount() {
        return mediumGrowthCount;
    }

    public long getLowGrowthCount() {
        return lowGrowthCount;
    }

    public String getLatestGrowthPlantType() {
        return latestGrowthPlantType;
    }

    public String getLatestGrowthRiskLevel() {
        return latestGrowthRiskLevel;
    }

    public String getLatestGrowthVisualState() {
        return latestGrowthVisualState;
    }

    public String getOverallStatus() {
        return overallStatus;
    }

    public List<AdminInsightResponse> getInsights() {
        return insights;
    }
}