#pragma once

#include "core/PlantTree.hpp"
#include "rules/LogicInferenceEngine.hpp"
#include "logs/SimulationLogger.hpp"
#include "core/SimulationState.hpp"
#include "core/SimulationHistory.hpp"

#include <string>

class EngineSnapshot {
public:
    static std::string plantTreeToJson(const PlantTree& plant);
    static std::string statesToJson(const LogicInferenceEngine& inferenceEngine);
    static std::string logsToJson(const SimulationLogger& logger);
    static std::string fullSnapshotToJson(
        const PlantTree& plant,
        const LogicInferenceEngine& inferenceEngine,
        const SimulationLogger& logger,
        const SimulationState& simulationState,
        const SimulationHistory& simulationHistory
    );
    static std::string simulationStateToJson(const SimulationState& state);
    static std::string simulationHistoryToJson(const SimulationHistory& history);

private:
    static std::string escapeJson(const std::string& text);
};