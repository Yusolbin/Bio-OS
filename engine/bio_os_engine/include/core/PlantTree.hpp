#pragma once

#include "PlantNode.hpp"
#include <unordered_map>
#include <vector>
#include <memory>
#include <string>

class PlantTree {
private:
    int nextNodeId;
    int rootId;

    std::unordered_map<int, std::unique_ptr<PlantNode>> nodes;

public:
    PlantTree();

    int createNode(NodeType type, int parentId = -1);

    PlantNode* getNode(int nodeId);
    const PlantNode* getNode(int nodeId) const;

    PlantNode* getRoot();
    const PlantNode* getRoot() const;

    bool removeNode(int nodeId);

    std::vector<int> getAllNodeIds() const;
    int getTotalNodeCount() const;

    void printTree() const;

private:
    void printSubtree(int nodeId, int depth) const;
    void removeSubtree(int nodeId);
};