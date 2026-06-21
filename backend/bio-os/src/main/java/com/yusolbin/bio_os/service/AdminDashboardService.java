package com.yusolbin.bio_os.service;

import com.yusolbin.bio_os.dto.AdminDashboardResponse;
import com.yusolbin.bio_os.model.GrowthSimulation;
import com.yusolbin.bio_os.model.SimulationLog;
import com.yusolbin.bio_os.repository.GeneRuleRepository;
import com.yusolbin.bio_os.repository.GrowthSimulationRepository;
import com.yusolbin.bio_os.repository.PlantTypeRepository;
import com.yusolbin.bio_os.repository.SimulationLogRepository;
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

    public AdminDashboardService(
            PlantTypeRepository plantTypeRepository,
            GeneRuleRepository geneRuleRepository,
            SimulationLogRepository simulationLogRepository,
            GrowthSimulationRepository growthSimulationRepository
    ) {
        this.plantTypeRepository = plantTypeRepository;
        this.geneRuleRepository = geneRuleRepository;
        this.simulationLogRepository = simulationLogRepository;
        this.growthSimulationRepository = growthSimulationRepository;
    }

    @Transactional(readOnly = true)
    public AdminDashboardResponse getDashboardSummary() {
        long plantTypeCount = plantTypeRepository.count();
        long geneRuleCount = geneRuleRepository.count();
        long simulationLogCount = simulationLogRepository.count();
        long growthSimulationCount = growthSimulationRepository.count();

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

        List<String> insights = generateInsights(
                averageGrowthScore,
                averageWater,
                averageLight,
                averageTemperature,
                averageHumidity,
                criticalGrowthCount,
                highGrowthCount,
                growthSimulationCount
        );

        return new AdminDashboardResponse(
                plantTypeCount,
                geneRuleCount,
                simulationLogCount,
                growthSimulationCount,
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

    private List<String> generateInsights(
            double averageGrowthScore,
            double averageWater,
            double averageLight,
            double averageTemperature,
            double averageHumidity,
            long criticalGrowthCount,
            long highGrowthCount,
            long growthSimulationCount
    ) {
        List<String> insights = new ArrayList<>();

        if (growthSimulationCount == 0) {
            insights.add("No growth simulation data yet. Run Growth Simulation to generate admin insights.");
            return insights;
        }

        if (criticalGrowthCount > 0) {
            insights.add("Critical growth simulations detected. Immediate review is recommended.");
        }

        if (highGrowthCount > 0) {
            insights.add("High-risk growth simulations exist. Check unstable environmental patterns.");
        }

        if (averageGrowthScore < 40) {
            insights.add("Average growth score is low. Current simulation conditions may be harmful.");
        } else if (averageGrowthScore >= 75) {
            insights.add("Average growth score is strong. Most growth simulations are performing well.");
        }

        if (averageWater < 30) {
            insights.add("Average water level is low. Drought-related risks may increase.");
        } else if (averageWater > 120) {
            insights.add("Average water level is very high. Over-watering patterns should be reviewed.");
        }

        if (averageLight < 30) {
            insights.add("Average light level is low. Photosynthesis performance may be limited.");
        }

        if (averageTemperature > 35) {
            insights.add("Average temperature is high. Heat stress may become a repeated risk factor.");
        } else if (averageTemperature < 10) {
            insights.add("Average temperature is low. Cold stress may become a repeated risk factor.");
        }

        if (averageHumidity < 35) {
            insights.add("Average humidity is low. Dry environment patterns may affect long-term growth.");
        } else if (averageHumidity > 85) {
            insights.add("Average humidity is very high. Excess moisture conditions should be monitored.");
        }

        if (insights.isEmpty()) {
            insights.add("Growth system looks stable. No major admin warning detected.");
        }

        return insights;
    }

    private double roundOne(double value) {
        return Math.round(value * 10.0) / 10.0;
    }
}