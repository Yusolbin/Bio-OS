#include "api/EngineSnapshot.hpp"

#include <sstream>
#include <vector>

std::string EngineSnapshot::plantTreeToJson(const PlantTree& plant) {
    std::ostringstream json;

    json << "{";
    json << "\"totalNodes\":" << plant.getTotalNodeCount() << ",";
    json << "\"nodes\":[";

    std::vector<int> nodeIds = plant.getAllNodeIds();

    bool firstNode = true;

    for (int nodeId : nodeIds) {
        const PlantNode* node = plant.getNode(nodeId);

        if (node == nullptr) {
            continue;
        }

        if (!firstNode) {
            json << ",";
        }

        json << "{";
        json << "\"id\":" << node->getId() << ",";
        json << "\"type\":\"" << escapeJson(node->getTypeName()) << "\",";
        json << "\"status\":\"" << escapeJson(node->getStatusName()) << "\",";
        json << "\"parentId\":" << node->getParentId() << ",";
        json << "\"water\":" << node->getWater() << ",";
        json << "\"maxWater\":" << node->getMaxWater() << ",";
        json << "\"energy\":" << node->getEnergy() << ",";
        json << "\"maintenanceCost\":" << node->getMaintenanceCost() << ",";
        json << "\"photosynthesisRate\":" << node->getPhotosynthesisRate() << ",";
        json << "\"survivalScore\":" << node->getSurvivalScore();
        json << "}";

        firstNode = false;
    }

    json << "]";
    json << "}";

    return json.str();
}

std::string EngineSnapshot::statesToJson(const LogicInferenceEngine& inferenceEngine) {
    std::ostringstream json;

    json << "{";

    const auto& states = inferenceEngine.getStates();

    size_t index = 0;

    for (const auto& pair : states) {
        json << "\"" << escapeJson(pair.first) << "\":";
        json << "\"" << escapeJson(pair.second) << "\"";

        if (index + 1 < states.size()) {
            json << ",";
        }

        index++;
    }

    json << "}";

    return json.str();
}

std::string EngineSnapshot::logsToJson(const SimulationLogger& logger) {
    std::ostringstream json;

    json << "[";

    const auto& logs = logger.getLogs();

    for (size_t i = 0; i < logs.size(); ++i) {
        json << "{";
        json << "\"tick\":" << logs[i].getTick() << ",";
        json << "\"type\":\"" << escapeJson(logs[i].getTypeName()) << "\",";
        json << "\"message\":\"" << escapeJson(logs[i].getMessage()) << "\"";
        json << "}";

        if (i + 1 < logs.size()) {
            json << ",";
        }
    }

    json << "]";

    return json.str();
}

std::string EngineSnapshot::simulationStateToJson(const SimulationState& state) {
    std::ostringstream json;

    json << "{";
    json << "\"currentTick\":" << state.getCurrentTick() << ",";
    json << "\"totalEnergy\":" << state.getTotalEnergy() << ",";
    json << "\"totalNodes\":" << state.getTotalNodes() << ",";
    json << "\"aliveNodes\":" << state.getAliveNodes() << ",";
    json << "\"wiltedNodes\":" << state.getWiltedNodes() << ",";
    json << "\"prunedNodes\":" << state.getPrunedNodes() << ",";
    json << "\"survivalScore\":" << state.getSurvivalScore() << ",";
    json << "\"lastAction\":\"" << escapeJson(state.getLastAction()) << "\"";
    json << "}";

    return json.str();
}

std::string EngineSnapshot::simulationHistoryToJson(const SimulationHistory& history) {
    std::ostringstream json;

    json << "[";

    const auto& states = history.getStates();

    for (size_t i = 0; i < states.size(); ++i) {
        json << simulationStateToJson(states[i]);

        if (i + 1 < states.size()) {
            json << ",";
        }
    }

    json << "]";

    return json.str();
}

std::string EngineSnapshot::fullSnapshotToJson(
    const PlantTree& plant,
    const LogicInferenceEngine& inferenceEngine,
    const SimulationLogger& logger,
    const SimulationState& simulationState,
    const SimulationHistory& simulationHistory
) {
    std::ostringstream json;

    json << "{";
    json << "\"tick\":" << simulationState.getCurrentTick() << ",";
    json << "\"summary\":" << simulationStateToJson(simulationState) << ",";
    json << "\"history\":" << simulationHistoryToJson(simulationHistory) << ",";
    json << "\"plant\":" << plantTreeToJson(plant) << ",";
    json << "\"states\":" << statesToJson(inferenceEngine) << ",";
    json << "\"logs\":" << logsToJson(logger);
    json << "}";

    return json.str();
}

std::string EngineSnapshot::escapeJson(const std::string& text) {
    std::string escaped;

    for (char ch : text) {
        switch (ch) {
        case '"':
            escaped += "\\\"";
            break;
        case '\\':
            escaped += "\\\\";
            break;
        case '\n':
            escaped += "\\n";
            break;
        case '\r':
            escaped += "\\r";
            break;
        case '\t':
            escaped += "\\t";
            break;
        default:
            escaped += ch;
            break;
        }
    }

    return escaped;
}