#pragma once

#include "core/PlantTree.hpp"
#include "core/Environment.hpp"
#include "core/TickSystem.hpp"

#include "rules/GeneRule.hpp"
#include "rules/RuleParser.hpp"
#include "rules/LogicInferenceEngine.hpp"
#include "rules/StateTransitionEngine.hpp"

#include <string>
#include <vector>

class EngineFacade {
private:
    PlantTree plant;
    TickSystem tickSystem;
    LogicInferenceEngine inferenceEngine;
    std::vector<GeneRule> rules;

public:
    EngineFacade();

    void initializeDefaultPlant();

    void addRule(const std::string& ruleText);

    void runTick(double waterInput, double light, double temperature);

    void printPlantTree() const;
    void printRules() const;
    void printInferredStates() const;
    void printLogs() const;
    void printSimulationSummary() const;
    void printSimulationHistory() const;

    std::string getSnapshotJson() const;

    PlantTree& getPlant();
    const PlantTree& getPlant() const;

    TickSystem& getTickSystem();
    const TickSystem& getTickSystem() const;

    const std::vector<GeneRule>& getRules() const;
};