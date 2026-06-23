#include "algorithms\PruningStrategy.hpp"

#include <iostream>
#include <queue>
#include <vector>


class PruningCandidate {
public:
    int nodeId;
    double survivalScore;

    PruningCandidate(int nodeId, double survivalScore)
        : nodeId(nodeId), survivalScore(survivalScore) {
    }

    bool operator<(const PruningCandidate& other) const {
        // priority_queue는 큰 값이 먼저 나오므로,
        // survivalScore가 낮은 후보가 먼저 나오도록 반대로 비교
        return survivalScore > other.survivalScore;
    }
};

bool PruningStrategy::pruneLowestValueLeaf(PlantTree& plant) {
    std::priority_queue<PruningCandidate> candidates;

    std::cout << "[Pruning Strategy - Priority Queue Start]" << std::endl;

    for (int nodeId : plant.getAllNodeIds()) {
        PlantNode* node = plant.getNode(nodeId);

        if (node == nullptr) {
            continue;
        }

        if (node->getType() == NodeType::Leaf &&
            node->getStatus() == NodeStatus::Alive) {

            double survivalScore =
                node->getPhotosynthesisRate()
                - node->getMaintenanceCost()
                - (node->getMaxWater() - node->getWater()) * 0.05;

            node->setSurvivalScore(survivalScore);

            std::cout << "Candidate "
                << node->getTypeName() << "#" << node->getId()
                << " survival score = " << survivalScore
                << std::endl;

            candidates.push(PruningCandidate(node->getId(), survivalScore));
        }
    }

    if (candidates.empty()) {
        std::cout << "No leaf candidate found for pruning." << std::endl;
        std::cout << "[Pruning Strategy - Completed]" << std::endl;
        return false;
    }

    PruningCandidate selected = candidates.top();

    PlantNode* selectedNode = plant.getNode(selected.nodeId);

    if (selectedNode == nullptr) {
        std::cout << "Selected node not found." << std::endl;
        std::cout << "[Pruning Strategy - Completed]" << std::endl;
        return false;
    }

    selectedNode->setStatus(NodeStatus::Pruned);
    selectedNode->setEnergy(0.0);
    selectedNode->setSurvivalScore(selected.survivalScore);

    std::cout << "Pruned "
        << selectedNode->getTypeName() << "#" << selectedNode->getId()
        << " by status change. Survival score: "
        << selected.survivalScore
        << std::endl;

    std::cout << "[Pruning Strategy - Completed]" << std::endl;

    return true;
}