#pragma once

#include "core/PlantTree.hpp"
#include "core/Environment.hpp"
#include "core/SimulationState.hpp"
#include "logs/SimulationLogger.hpp"
#include "core/SimulationHistory.hpp"

class TickSystem {
private:
    int currentTick;
    SimulationLogger logger;
    SimulationState simulationState;
    SimulationHistory simulationHistory;

public:
    TickSystem();

    void runTick(
        PlantTree& plant,
        const Environment& environment,
        bool pruningAlreadyExecuted = false
    );

    int getCurrentTick() const;

    const SimulationLogger& getLogger() const;
    SimulationLogger& getLogger();

    const SimulationState& getSimulationState() const;
    SimulationState& getSimulationState();

    const SimulationHistory& getSimulationHistory() const;
    SimulationHistory& getSimulationHistory();

private:
    void updateSimulationState(
        const PlantTree& plant,
        double totalEnergy,
        const std::string& lastAction
    );

    double calculateSurvivalScore(
        int totalNodes,
        int aliveNodes,
        double totalEnergy
    ) const;
};