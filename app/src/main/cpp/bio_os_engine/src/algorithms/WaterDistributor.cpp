#include "algorithms\WaterDistributor.hpp"

#include <iostream>
#include <queue>

void WaterDistributor::distribute(PlantTree& plant, double inputWater) {
    PlantNode* root = plant.getRoot();

    if (root == nullptr) {
        std::cout << "[Water Distribution] Plant tree is empty." << std::endl;
        return;
    }

    std::queue<std::pair<int, double>> bfsQueue;
    bfsQueue.push({ root->getId(), inputWater });

    std::cout << "[Water Distribution - BFS Start]" << std::endl;

    while (!bfsQueue.empty()) {
        int currentNodeId = bfsQueue.front().first;
        double availableWater = bfsQueue.front().second;
        bfsQueue.pop();

        PlantNode* currentNode = plant.getNode(currentNodeId);

        if (currentNode == nullptr) {
            continue;
        }

        if (currentNode->getStatus() == NodeStatus::Pruned) {
            std::cout << currentNode->getTypeName() << "#" << currentNode->getId()
                << " is pruned. Water ignored."
                << std::endl;
            continue;
        }

        double beforeWater = currentNode->getWater();
        currentNode->addWater(availableWater);
        double afterWater = currentNode->getWater();

        double absorbedWater = afterWater - beforeWater;
        double overflowWater = availableWater - absorbedWater;

        std::cout << currentNode->getTypeName() << "#" << currentNode->getId()
            << " absorbed " << absorbedWater
            << " water. Current water: "
            << currentNode->getWater() << "/"
            << currentNode->getMaxWater()
            << std::endl;

        const std::vector<int>& children = currentNode->getChildrenIds();

        if (!children.empty()) {
            double waterForEachChild = overflowWater / children.size();

            for (int childId : children) {
                bfsQueue.push({ childId, waterForEachChild });
            }
        }
    }

    std::cout << "[Water Distribution - BFS Completed]" << std::endl;
}