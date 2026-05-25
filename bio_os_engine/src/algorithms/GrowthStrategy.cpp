#include "algorithms/GrowthStrategy.hpp"

#include <iostream>

int GrowthStrategy::growRootTip(PlantTree& plant) {
    PlantNode* root = plant.getRoot();

    if (root == nullptr) {
        std::cout << "Growth failed: root node not found." << std::endl;
        return -1;
    }

    int newRootTipId = plant.createNode(NodeType::RootTip, root->getId());

    PlantNode* rootTip = plant.getNode(newRootTipId);

    if (rootTip != nullptr) {
        rootTip->setMaintenanceCost(2.0);
    }

    std::cout << "GrowthStrategy: New RootTip#"
        << newRootTipId
        << " created."
        << std::endl;

    return newRootTipId;
}

int GrowthStrategy::growLeafOnFirstStem(PlantTree& plant) {
    int stemId = findFirstNodeByType(plant, NodeType::Stem);

    if (stemId == -1) {
        std::cout << "Growth failed: stem node not found." << std::endl;
        return -1;
    }

    int newLeafId = plant.createNode(NodeType::Leaf, stemId);

    PlantNode* leaf = plant.getNode(newLeafId);

    if (leaf != nullptr) {
        leaf->setMaintenanceCost(0.8);
        leaf->setPhotosynthesisRate(30.0);
        leaf->addWater(50.0);
    }

    std::cout << "GrowthStrategy: New Leaf#"
        << newLeafId
        << " created on Stem#"
        << stemId
        << "."
        << std::endl;

    return newLeafId;
}

void GrowthStrategy::boostLeafPhotosynthesis(PlantTree& plant, double amount) {
    for (int nodeId : plant.getAllNodeIds()) {
        PlantNode* node = plant.getNode(nodeId);

        if (node == nullptr) {
            continue;
        }

        if (node->getType() == NodeType::Leaf &&
            node->getStatus() == NodeStatus::Alive) {
            double boostedRate = node->getPhotosynthesisRate() + amount;
            node->setPhotosynthesisRate(boostedRate);

            std::cout << "GrowthStrategy: Leaf#"
                << node->getId()
                << " photosynthesis rate increased to "
                << boostedRate
                << std::endl;
        }
    }
}

int GrowthStrategy::findFirstNodeByType(const PlantTree& plant, NodeType type) {
    for (int nodeId : plant.getAllNodeIds()) {
        const PlantNode* node = plant.getNode(nodeId);

        if (node == nullptr) {
            continue;
        }

        if (node->getType() == type &&
            node->getStatus() == NodeStatus::Alive) {
            return nodeId;
        }
    }

    return -1;
}