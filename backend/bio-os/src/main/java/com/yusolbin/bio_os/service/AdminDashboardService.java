package com.yusolbin.bio_os.service;

import com.yusolbin.bio_os.dto.AdminDashboardResponse;
import com.yusolbin.bio_os.dto.AdminInsightResponse;
import com.yusolbin.bio_os.model.GrowthSimulation;
import com.yusolbin.bio_os.model.SimulationLog;
import com.yusolbin.bio_os.repository.GeneRuleRepository;
import com.yusolbin.bio_os.repository.GrowthSimulationRepository;
import com.yusolbin.bio_os.repository.PlantTypeRepository;
import com.yusolbin.bio_os.repository.SimulationLogRepository;
import com.yusolbin.bio_os.repository.UserAccountRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
public class AdminDashboardService {

    private final PlantTypeRepository plantTypeRepository;
    private final GeneRuleRepository geneRuleRepository;
    private final SimulationLogRepository simulationLogRepository;
    private final GrowthSimulationRepository growthSimulationRepository;
    private final UserAccountRepository userAccountRepository;

    public AdminDashboardService(
            PlantTypeRepository plantTypeRepository,
            GeneRuleRepository geneRuleRepository,
            SimulationLogRepository simulationLogRepository,
            GrowthSimulationRepository growthSimulationRepository,
            UserAccountRepository userAccountRepository
    ) {
        this.plantTypeRepository = plantTypeRepository;
        this.geneRuleRepository = geneRuleRepository;
        this.simulationLogRepository = simulationLogRepository;
        this.growthSimulationRepository = growthSimulationRepository;
        this.userAccountRepository = userAccountRepository;
    }

    @Transactional(readOnly = true)
    public AdminDashboardResponse getDashboardSummary() {
        long plantTypeCount = plantTypeRepository.count();
        long geneRuleCount = geneRuleRepository.count();
        long simulationLogCount = simulationLogRepository.count();
        long growthSimulationCount = growthSimulationRepository.count();
        long userCount = userAccountRepository.count();

        List<GrowthSimulation> growthSimulations =
                growthSimulationRepository.findAllByOrderByIdDesc();

        List<SimulationLog> simulationLogs = simulationLogRepository.findAll();

        double averageGrowthScore = calculateAverageGrowthScore(growthSimulations);

        long criticalGrowthCount = countByRiskLevel(growthSimulations, "CRITICAL");
        long highGrowthCount = countByRiskLevel(growthSimulations, "HIGH");
        long mediumGrowthCount = countByRiskLevel(growthSimulations, "MEDIUM");
        long lowGrowthCount = countByRiskLevel(growthSimulations, "LOW");

        double averageWater = calculateAverageWater(simulationLogs);
        double averageLight = calculateAverageLight(simulationLogs);
        double averageTemperature = calculateAverageTemperature(simulationLogs);
        double averageHumidity = calculateAverageHumidity(simulationLogs);

        String latestGrowthPlantType = "-";
        String latestGrowthRiskLevel = "-";
        String latestGrowthVisualState = "-";

        if (!growthSimulations.isEmpty()) {
            GrowthSimulation latest = growthSimulations.get(0);

            latestGrowthPlantType = latest.getPlantType().getName();
            latestGrowthRiskLevel = latest.getFinalRiskLevel();
            latestGrowthVisualState = latest.getFinalVisualState();
        }

        List<AdminInsightResponse> insights = generateInsights(
                averageGrowthScore,
                averageWater,
                averageLight,
                averageTemperature,
                averageHumidity,
                criticalGrowthCount,
                highGrowthCount,
                growthSimulationCount
        );

        String overallStatus = determineOverallStatus(insights);

        return new AdminDashboardResponse(
                plantTypeCount,
                geneRuleCount,
                simulationLogCount,
                growthSimulationCount,
                userCount,
                averageGrowthScore,
                averageWater,
                averageLight,
                averageTemperature,
                averageHumidity,
                criticalGrowthCount,
                highGrowthCount,
                mediumGrowthCount,
                lowGrowthCount,
                latestGrowthPlantType,
                latestGrowthRiskLevel,
                latestGrowthVisualState,
                overallStatus,
                insights
        );
    }

    private double calculateAverageGrowthScore(List<GrowthSimulation> growthSimulations) {
        if (growthSimulations.isEmpty()) {
            return 0.0;
        }

        double average = growthSimulations.stream()
                .mapToDouble(GrowthSimulation::getFinalGrowthScore)
                .average()
                .orElse(0.0);

        return roundOne(average);
    }

    private long countByRiskLevel(
            List<GrowthSimulation> growthSimulations,
            String riskLevel
    ) {
        return growthSimulations.stream()
                .filter(simulation -> riskLevel.equals(simulation.getFinalRiskLevel()))
                .count();
    }

    private double calculateAverageWater(List<SimulationLog> simulationLogs) {
        if (simulationLogs.isEmpty()) {
            return 0.0;
        }

        double average = simulationLogs.stream()
                .mapToDouble(SimulationLog::getWater)
                .average()
                .orElse(0.0);

        return roundOne(average);
    }

    private double calculateAverageLight(List<SimulationLog> simulationLogs) {
        if (simulationLogs.isEmpty()) {
            return 0.0;
        }

        double average = simulationLogs.stream()
                .mapToDouble(SimulationLog::getLight)
                .average()
                .orElse(0.0);

        return roundOne(average);
    }

    private double calculateAverageTemperature(List<SimulationLog> simulationLogs) {
        if (simulationLogs.isEmpty()) {
            return 0.0;
        }

        double average = simulationLogs.stream()
                .mapToDouble(SimulationLog::getTemperature)
                .average()
                .orElse(0.0);

        return roundOne(average);
    }

    private double calculateAverageHumidity(List<SimulationLog> simulationLogs) {
        if (simulationLogs.isEmpty()) {
            return 0.0;
        }

        double average = simulationLogs.stream()
                .mapToDouble(SimulationLog::getHumidity)
                .average()
                .orElse(0.0);

        return roundOne(average);
    }

    private List<AdminInsightResponse> generateInsights(
            double averageGrowthScore,
            double averageWater,
            double averageLight,
            double averageTemperature,
            double averageHumidity,
            long criticalGrowthCount,
            long highGrowthCount,
            long growthSimulationCount
    ) {
        List<AdminInsightResponse> insights = new ArrayList<>();

        if (growthSimulationCount == 0) {
            insights.add(new AdminInsightResponse(
                    "INFO",
                    "No growth simulation data yet. Run Growth Simulation to generate admin insights."
            ));
            return insights;
        }

        if (criticalGrowthCount > 0) {
            insights.add(new AdminInsightResponse(
                    "CRITICAL",
                    "Critical growth simulations detected. Immediate review is recommended."
            ));
        }

        if (highGrowthCount > 0) {
            insights.add(new AdminInsightResponse(
                    "WARNING",
                    "High-risk growth simulations exist. Check unstable environmental patterns."
            ));
        }

        if (averageGrowthScore < 40) {
            insights.add(new AdminInsightResponse(
                    "CRITICAL",
                    "Average growth score is low. Current simulation conditions may be harmful."
            ));
        } else if (averageGrowthScore >= 75) {
            insights.add(new AdminInsightResponse(
                    "STABLE",
                    "Average growth score is strong. Most growth simulations are performing well."
            ));
        }

        if (averageWater < 30) {
            insights.add(new AdminInsightResponse(
                    "WARNING",
                    "Average water level is low. Drought-related risks may increase."
            ));
        } else if (averageWater > 120) {
            insights.add(new AdminInsightResponse(
                    "WARNING",
                    "Average water level is very high. Over-watering patterns should be reviewed."
            ));
        }

        if (averageLight < 30) {
            insights.add(new AdminInsightResponse(
                    "WARNING",
                    "Average light level is low. Photosynthesis performance may be limited."
            ));
        }

        if (averageTemperature > 35) {
            insights.add(new AdminInsightResponse(
                    "WARNING",
                    "Average temperature is high. Heat stress may become a repeated risk factor."
            ));
        } else if (averageTemperature < 10) {
            insights.add(new AdminInsightResponse(
                    "WARNING",
                    "Average temperature is low. Cold stress may become a repeated risk factor."
            ));
        }

        if (averageHumidity < 35) {
            insights.add(new AdminInsightResponse(
                    "WARNING",
                    "Average humidity is low. Dry environment patterns may affect long-term growth."
            ));
        } else if (averageHumidity > 85) {
            insights.add(new AdminInsightResponse(
                    "WARNING",
                    "Average humidity is very high. Excess moisture conditions should be monitored."
            ));
        }

        if (insights.isEmpty()) {
            insights.add(new AdminInsightResponse(
                    "STABLE",
                    "Growth system looks stable. No major admin warning detected."
            ));
        }

        return insights;
    }

    private String determineOverallStatus(List<AdminInsightResponse> insights) {
    boolean hasCritical = insights.stream()
            .anyMatch(insight -> "CRITICAL".equals(insight.getSeverity()));

    if (hasCritical) {
        return "CRITICAL";
    }

    boolean hasWarning = insights.stream()
            .anyMatch(insight -> "WARNING".equals(insight.getSeverity()));

    if (hasWarning) {
        return "WARNING";
    }

    boolean hasStable = insights.stream()
            .anyMatch(insight -> "STABLE".equals(insight.getSeverity()));

    if (hasStable) {
        return "STABLE";
    }

    return "INFO";
}

    private double roundOne(double value) {
        return Math.round(value * 10.0) / 10.0;
    }
}