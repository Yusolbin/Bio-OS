package com.yusolbin.bio_os.service;

import com.yusolbin.bio_os.dto.AdminDashboardResponse;
import com.yusolbin.bio_os.model.GrowthSimulation;
import com.yusolbin.bio_os.repository.GeneRuleRepository;
import com.yusolbin.bio_os.repository.GrowthSimulationRepository;
import com.yusolbin.bio_os.repository.PlantTypeRepository;
import com.yusolbin.bio_os.repository.SimulationLogRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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

        double averageGrowthScore = calculateAverageGrowthScore(growthSimulations);

        long criticalGrowthCount = countByRiskLevel(growthSimulations, "CRITICAL");
        long highGrowthCount = countByRiskLevel(growthSimulations, "HIGH");
        long mediumGrowthCount = countByRiskLevel(growthSimulations, "MEDIUM");
        long lowGrowthCount = countByRiskLevel(growthSimulations, "LOW");

        String latestGrowthPlantType = "-";
        String latestGrowthRiskLevel = "-";
        String latestGrowthVisualState = "-";

        if (!growthSimulations.isEmpty()) {
            GrowthSimulation latest = growthSimulations.get(0);

            latestGrowthPlantType = latest.getPlantType().getName();
            latestGrowthRiskLevel = latest.getFinalRiskLevel();
            latestGrowthVisualState = latest.getFinalVisualState();
        }

        return new AdminDashboardResponse(
                plantTypeCount,
                geneRuleCount,
                simulationLogCount,
                growthSimulationCount,
                averageGrowthScore,
                criticalGrowthCount,
                highGrowthCount,
                mediumGrowthCount,
                lowGrowthCount,
                latestGrowthPlantType,
                latestGrowthRiskLevel,
                latestGrowthVisualState
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

    private double roundOne(double value) {
        return Math.round(value * 10.0) / 10.0;
    }
}