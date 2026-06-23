#include "core/TickSystem.hpp"

#include "algorithms/WaterDistributor.hpp"
#include "algorithms/EnergyEvaluator.hpp"
#include "algorithms/PruningStrategy.hpp"

#include <iostream>

TickSystem::TickSystem()
    : currentTick(0) {
}

void TickSystem::runTick(
    PlantTree& plant,
    const Environment& environment,
    bool pruningAlreadyExecuted
) {
    currentTick++;

    std::string lastAction = "Stable";

    std::cout << std::endl;
    std::cout << "[TICK " << currentTick << " START]" << std::endl;
    std::cout << "Environment: "
        << "Water=" << environment.getWaterInput()
        << ", Light=" << environment.getLight()
        << ", Temperature=" << environment.getTemperature()
        << std::endl;

    logger.addLog(currentTick, LogType::Tick, "Tick started.");
    logger.addLog(
        currentTick,
        LogType::System,
        "Environment: Water=" + std::to_string(environment.getWaterInput())
        + ", Light=" + std::to_string(environment.getLight())
        + ", Temperature=" + std::to_string(environment.getTemperature())
    );

    WaterDistributor::distribute(plant, environment.getWaterInput());
    logger.addLog(currentTick, LogType::WaterDistribution, "BFS water distribution completed.");

    std::cout << std::endl;

    double totalEnergy = EnergyEvaluator::evaluate(plant);
    logger.addLog(
        currentTick,
        LogType::EnergyEvaluation,
        "DFS energy evaluation completed. Total energy balance = " + std::to_string(totalEnergy)
    );

    std::cout << std::endl;

    if (totalEnergy < 0) {
        std::cout << "Resource deficit detected." << std::endl;
        logger.addLog(currentTick, LogType::Pruning, "Resource deficit detected.");

        if (pruningAlreadyExecuted) {
            std::cout << "Pruning already executed during state transition. Skipping additional pruning."
                << std::endl;

            lastAction = "PruningAlreadyExecuted";
            logger.addLog(
                currentTick,
                LogType::Pruning,
                "Pruning already executed during state transition. Additional pruning skipped."
            );
        }
        else {
            std::cout << "Pruning required." << std::endl;
            logger.addLog(currentTick, LogType::Pruning, "Pruning required.");

            bool pruned = PruningStrategy::pruneLowestValueLeaf(plant);

            if (pruned) {
                lastAction = "Pruning";
                logger.addLog(currentTick, LogType::Pruning, "Lowest survival score leaf pruned.");
            }
            else {
                lastAction = "PruningFailed";
                logger.addLog(currentTick, LogType::Pruning, "No pruning candidate found.");
            }
        }
    }
    else {
        std::cout << "Resource state is stable. No pruning required." << std::endl;
        lastAction = "Stable";
        logger.addLog(currentTick, LogType::Pruning, "Resource state is stable. No pruning required.");
    }
  

    updateSimulationState(plant, totalEnergy, lastAction);
    simulationHistory.addState(simulationState);

    logger.addLog(currentTick, LogType::Tick, "Tick ended.");

    std::cout << "[TICK " << currentTick << " END]" << std::endl;
}

int TickSystem::getCurrentTick() const {
    return currentTick;
}

const SimulationLogger& TickSystem::getLogger() const {
    return logger;
}

SimulationLogger& TickSystem::getLogger() {
    return logger;
}

const SimulationState& TickSystem::getSimulationState() const {
    return simulationState;
}

SimulationState& TickSystem::getSimulationState() {
    return simulationState;
}

void TickSystem::updateSimulationState(
    const PlantTree& plant,
    double totalEnergy,
    const std::string& lastAction
) {
    int totalNodes = plant.getTotalNodeCount();
    int aliveNodes = 0;
    int wiltedNodes = 0;
    int prunedNodes = 0;

    for (int nodeId : plant.getAllNodeIds()) {
        const PlantNode* node = plant.getNode(nodeId);

        if (node == nullptr) {
            continue;
        }

        if (node->getStatus() == NodeStatus::Alive) {
            aliveNodes++;
        }
        else if (node->getStatus() == NodeStatus::Wilted) {
            wiltedNodes++;
        }
        else if (node->getStatus() == NodeStatus::Pruned) {
            prunedNodes++;
        }
    }

    simulationState.setCurrentTick(currentTick);
    simulationState.setTotalEnergy(totalEnergy);
    simulationState.setTotalNodes(totalNodes);
    simulationState.setAliveNodes(aliveNodes);
    simulationState.setWiltedNodes(wiltedNodes);
    simulationState.setPrunedNodes(prunedNodes);
    simulationState.setSurvivalScore(
        calculateSurvivalScore(totalNodes, aliveNodes, totalEnergy)
    );
    simulationState.setLastAction(lastAction);
}

double TickSystem::calculateSurvivalScore(
    int totalNodes,
    int aliveNodes,
    double totalEnergy
) const {
    if (totalNodes <= 0) {
        return 0.0;
    }

    double aliveRatio = static_cast<double>(aliveNodes) / totalNodes;
    double energyScore = totalEnergy + 50.0;

    if (energyScore < 0.0) {
        energyScore = 0.0;
    }

    if (energyScore > 100.0) {
        energyScore = 100.0;
    }

    double score = aliveRatio * 70.0 + energyScore * 0.3;

    if (score < 0.0) {
        score = 0.0;
    }

    if (score > 100.0) {
        score = 100.0;
    }

    return score;


}

const SimulationHistory& TickSystem::getSimulationHistory() const {
    return simulationHistory;
}

SimulationHistory& TickSystem::getSimulationHistory() {
    return simulationHistory;
}