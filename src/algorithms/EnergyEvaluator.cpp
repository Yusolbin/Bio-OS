#include "algorithms\EnergyEvaluator.hpp"
#include <iostream>

double EnergyEvaluator::evaluate(PlantTree& plant) {
    PlantNode* root = plant.getRoot();

    if (root == nullptr) {
        std::cout << "[Energy Evaluation] Plant tree is empty." << std::endl;
        return 0.0;
    }

    std::cout << "[Energy Evaluation - DFS Start]" << std::endl;

    double totalEnergy = evaluateSubtree(plant, root->getId());

    std::cout << "[Energy Evaluation - DFS Completed]" << std::endl;
    std::cout << "Total Energy Balance: " << totalEnergy << std::endl;

    return totalEnergy;
}

double EnergyEvaluator::evaluateSubtree(PlantTree& plant, int nodeId) {
    PlantNode* node = plant.getNode(nodeId);

    if (node == nullptr) {
        return 0.0;
    }

    if (node->getStatus() == NodeStatus::Pruned) {
        node->setEnergy(0.0);

        std::cout << node->getTypeName() << "#" << node->getId()
            << " is pruned. Energy ignored."
            << std::endl;

        return 0.0;
    }

    double producedEnergy = 0.0;
    double consumedEnergy = node->getMaintenanceCost();

    if (node->getType() == NodeType::Leaf) {
        producedEnergy = node->getPhotosynthesisRate();

        // 물이 부족하면 광합성 효율 감소
        double waterRatio = node->getWater() / node->getMaxWater();
        producedEnergy *= waterRatio;
    }

    double subtreeEnergy = producedEnergy - consumedEnergy;

    for (int childId : node->getChildrenIds()) {
        subtreeEnergy += evaluateSubtree(plant, childId);
    }

    node->setEnergy(subtreeEnergy);

    std::cout << node->getTypeName() << "#" << node->getId()
        << " produced " << producedEnergy
        << " / consumed " << consumedEnergy
        << " / subtree energy = " << subtreeEnergy
        << std::endl;

    return subtreeEnergy;
}