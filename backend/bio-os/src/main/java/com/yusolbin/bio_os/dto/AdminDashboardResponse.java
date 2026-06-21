package com.yusolbin.bio_os.dto;

public class AdminDashboardResponse {

    private long plantTypeCount;
    private long geneRuleCount;
    private long simulationLogCount;
    private long growthSimulationCount;

    private double averageGrowthScore;

    private long criticalGrowthCount;
    private long highGrowthCount;
    private long mediumGrowthCount;
    private long lowGrowthCount;

    private String latestGrowthPlantType;
    private String latestGrowthRiskLevel;
    private String latestGrowthVisualState;

    public AdminDashboardResponse(
            long plantTypeCount,
            long geneRuleCount,
            long simulationLogCount,
            long growthSimulationCount,
            double averageGrowthScore,
            long criticalGrowthCount,
            long highGrowthCount,
            long mediumGrowthCount,
            long lowGrowthCount,
            String latestGrowthPlantType,
            String latestGrowthRiskLevel,
            String latestGrowthVisualState
    ) {
        this.plantTypeCount = plantTypeCount;
        this.geneRuleCount = geneRuleCount;
        this.simulationLogCount = simulationLogCount;
        this.growthSimulationCount = growthSimulationCount;
        this.averageGrowthScore = averageGrowthScore;
        this.criticalGrowthCount = criticalGrowthCount;
        this.highGrowthCount = highGrowthCount;
        this.mediumGrowthCount = mediumGrowthCount;
        this.lowGrowthCount = lowGrowthCount;
        this.latestGrowthPlantType = latestGrowthPlantType;
        this.latestGrowthRiskLevel = latestGrowthRiskLevel;
        this.latestGrowthVisualState = latestGrowthVisualState;
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
}
