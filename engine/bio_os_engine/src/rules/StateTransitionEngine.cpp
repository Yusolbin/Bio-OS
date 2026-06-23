#include "rules/StateTransitionEngine.hpp"

#include "algorithms/GrowthStrategy.hpp"
#include "algorithms/PruningStrategy.hpp"

#include <iostream>

bool StateTransitionEngine::applyTransitions(
    PlantTree& plant,
    const LogicInferenceEngine& inferenceEngine
) {
    bool pruningExecuted = false;

    std::cout << "[State Transition Start]" << std::endl;

    if (inferenceEngine.getState("DroughtMode") == "ON") {
        applyDroughtMode(plant);
    }

    if (inferenceEngine.getState("PhotosynthesisBoost") == "ON") {
        applyPhotosynthesisBoost(plant);
    }

    if (inferenceEngine.getState("HeatStress") == "ON") {
        applyHeatStress(plant);
    }

    if (inferenceEngine.getState("RecoveryMode") == "ON") {
        applyRecoveryMode(plant);
    }

    if (inferenceEngine.getState("PruningMode") == "ON") {
        applyPruningMode(plant, pruningExecuted);
    }

    std::cout << "[State Transition Completed]" << std::endl;

    return pruningExecuted;
}

void StateTransitionEngine::applyDroughtMode(PlantTree& plant) {
    int newRootTipId = GrowthStrategy::growRootTip(plant);

    if (newRootTipId != -1) {
        std::cout << "State Applied: DroughtMode -> RootTip#"
            << newRootTipId
            << " growth requested."
            << std::endl;
    }
}

void StateTransitionEngine::applyPhotosynthesisBoost(PlantTree& plant) {
    GrowthStrategy::boostLeafPhotosynthesis(plant, 5.0);

    std::cout << "State Applied: PhotosynthesisBoost -> Leaf photosynthesis boosted."
        << std::endl;
}

void StateTransitionEngine::applyHeatStress(PlantTree& plant) {
    for (int nodeId : plant.getAllNodeIds()) {
        PlantNode* node = plant.getNode(nodeId);

        if (node == nullptr) {
            continue;
        }

        if (node->getStatus() != NodeStatus::Alive) {
            continue;
        }

        double newCost = node->getMaintenanceCost() + 1.0;
        node->setMaintenanceCost(newCost);

        std::cout << "State Applied: HeatStress -> "
            << node->getTypeName() << "#" << node->getId()
            << " maintenance cost increased to "
            << newCost
            << std::endl;
    }
}

void StateTransitionEngine::applyRecoveryMode(PlantTree& plant) {
    int newLeafId = GrowthStrategy::growLeafOnFirstStem(plant);

    if (newLeafId != -1) {
        std::cout << "State Applied: RecoveryMode -> Leaf#"
            << newLeafId
            << " growth requested."
            << std::endl;
    }
}

void StateTransitionEngine::applyPruningMode(
    PlantTree& plant,
    bool& pruningExecuted
) {
    std::cout << "State Applied: PruningMode -> Forced pruning requested."
        << std::endl;

    pruningExecuted = PruningStrategy::pruneLowestValueLeaf(plant);
}