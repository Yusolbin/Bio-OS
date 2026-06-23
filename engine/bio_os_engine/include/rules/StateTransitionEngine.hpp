#pragma once

#include "core/PlantTree.hpp"
#include "rules/LogicInferenceEngine.hpp"

class StateTransitionEngine {
public:
    static bool applyTransitions(
        PlantTree& plant,
        const LogicInferenceEngine& inferenceEngine
    );

private:
    static void applyDroughtMode(PlantTree& plant);
    static void applyPhotosynthesisBoost(PlantTree& plant);
    static void applyHeatStress(PlantTree& plant);
    static void applyRecoveryMode(PlantTree& plant);

    static void applyPruningMode(
        PlantTree& plant,
        bool& pruningExecuted
    );
};