#include "api/EngineFacade.hpp"
#include "api/EngineSnapshot.hpp"

#include <iostream>
#include <stdexcept>

EngineFacade::EngineFacade() {}

void EngineFacade::initializeDefaultPlant() {
    plant = PlantTree();

    int root = plant.createNode(NodeType::Root);
    int stem = plant.createNode(NodeType::Stem, root);

    int leaf1 = plant.createNode(NodeType::Leaf, stem);
    int leaf2 = plant.createNode(NodeType::Leaf, stem);

    int rootTip = plant.createNode(NodeType::RootTip, root);

    plant.getNode(root)->setMaintenanceCost(3.0);
    plant.getNode(stem)->setMaintenanceCost(5.0);
    plant.getNode(rootTip)->setMaintenanceCost(2.0);

    plant.getNode(leaf1)->setMaintenanceCost(1.0);
    plant.getNode(leaf1)->setPhotosynthesisRate(20.0);

    plant.getNode(leaf2)->setMaintenanceCost(1.0);
    plant.getNode(leaf2)->setPhotosynthesisRate(20.0);

    std::cout << "[EngineFacade] Default plant initialized." << std::endl;
}

void EngineFacade::addRule(const std::string& ruleText) {
    GeneRule rule = RuleParser::parse(ruleText);
    rules.push_back(rule);

    std::cout << "[EngineFacade] Rule added: "
        << rule.toString()
        << std::endl;
}

void EngineFacade::runTick(double waterInput, double light, double temperature) {
    Environment environment(waterInput, light, temperature);

    inferenceEngine.clearStates();
    inferenceEngine.evaluateAndApply(rules, environment);

    bool pruningExecutedByStateTransition =
        StateTransitionEngine::applyTransitions(plant, inferenceEngine);

    tickSystem.runTick(
        plant,
        environment,
        pruningExecutedByStateTransition
    );
}

void EngineFacade::printPlantTree() const {
    plant.printTree();
}

void EngineFacade::printRules() const {
    std::cout << "[Registered Rules]" << std::endl;

    if (rules.empty()) {
        std::cout << "(empty)" << std::endl;
        return;
    }

    for (const GeneRule& rule : rules) {
        std::cout << rule.toString() << std::endl;
    }
}

void EngineFacade::printInferredStates() const {
    inferenceEngine.printStates();
}

void EngineFacade::printLogs() const {
    tickSystem.getLogger().printAll();
}

PlantTree& EngineFacade::getPlant() {
    return plant;
}

const PlantTree& EngineFacade::getPlant() const {
    return plant;
}

TickSystem& EngineFacade::getTickSystem() {
    return tickSystem;
}

const TickSystem& EngineFacade::getTickSystem() const {
    return tickSystem;
}

const std::vector<GeneRule>& EngineFacade::getRules() const {
    return rules;
}

std::string EngineFacade::getSnapshotJson() const {
    return EngineSnapshot::fullSnapshotToJson(
        plant,
        inferenceEngine,
        tickSystem.getLogger(),
        tickSystem.getSimulationState(),
        tickSystem.getSimulationHistory()
    );
}

void EngineFacade::printSimulationSummary() const {
    const SimulationState& state = tickSystem.getSimulationState();

    std::cout << "[Simulation Summary]" << std::endl;
    std::cout << "Tick: " << state.getCurrentTick() << std::endl;
    std::cout << "Total Energy: " << state.getTotalEnergy() << std::endl;
    std::cout << "Total Nodes: " << state.getTotalNodes() << std::endl;
    std::cout << "Alive Nodes: " << state.getAliveNodes() << std::endl;
    std::cout << "Wilted Nodes: " << state.getWiltedNodes() << std::endl;
    std::cout << "Pruned Nodes: " << state.getPrunedNodes() << std::endl;
    std::cout << "Survival Score: " << state.getSurvivalScore() << std::endl;
    std::cout << "Last Action: " << state.getLastAction() << std::endl;
}

void EngineFacade::printSimulationHistory() const {
    tickSystem.getSimulationHistory().printHistory();
}