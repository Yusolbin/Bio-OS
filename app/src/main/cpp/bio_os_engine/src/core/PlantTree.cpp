#include "core\PlantTree.hpp"
#include <iostream>

PlantTree::PlantTree()
    : nextNodeId(1), rootId(-1) {
}

int PlantTree::createNode(NodeType type, int parentId) {
    int newId = nextNodeId++;

    nodes[newId] = std::make_unique<PlantNode>(newId, type, parentId);

    if (parentId == -1) {
        rootId = newId;
    }
    else {
        PlantNode* parent = getNode(parentId);

        if (parent != nullptr) {
            parent->addChild(newId);
        }
    }

    return newId;
}

PlantNode* PlantTree::getNode(int nodeId) {
    auto it = nodes.find(nodeId);

    if (it == nodes.end()) {
        return nullptr;
    }

    return it->second.get();
}

const PlantNode* PlantTree::getNode(int nodeId) const {
    auto it = nodes.find(nodeId);

    if (it == nodes.end()) {
        return nullptr;
    }

    return it->second.get();
}

PlantNode* PlantTree::getRoot() {
    return getNode(rootId);
}

const PlantNode* PlantTree::getRoot() const {
    return getNode(rootId);
}

bool PlantTree::removeNode(int nodeId) {
    if (nodes.find(nodeId) == nodes.end()) {
        return false;
    }

    if (nodeId == rootId) {
        nodes.clear();
        rootId = -1;
        return true;
    }

    PlantNode* node = getNode(nodeId);

    if (node == nullptr) {
        return false;
    }

    int parentId = node->getParentId();
    PlantNode* parent = getNode(parentId);

    if (parent != nullptr) {
        parent->removeChild(nodeId);
    }

    removeSubtree(nodeId);

    return true;
}

void PlantTree::removeSubtree(int nodeId) {
    PlantNode* node = getNode(nodeId);

    if (node == nullptr) {
        return;
    }

    std::vector<int> children = node->getChildrenIds();

    for (int childId : children) {
        removeSubtree(childId);
    }

    nodes.erase(nodeId);
}

std::vector<int> PlantTree::getAllNodeIds() const {
    std::vector<int> ids;

    for (const auto& pair : nodes) {
        ids.push_back(pair.first);
    }

    return ids;
}

int PlantTree::getTotalNodeCount() const {
    return static_cast<int>(nodes.size());
}

void PlantTree::printTree() const {
    std::cout << "Plant Tree:" << std::endl;

    if (rootId == -1) {
        std::cout << "(empty)" << std::endl;
        return;
    }

    printSubtree(rootId, 0);
}

void PlantTree::printSubtree(int nodeId, int depth) const {
    const PlantNode* node = getNode(nodeId);

    if (node == nullptr) {
        return;
    }

    for (int i = 0; i < depth; ++i) {
        std::cout << "  ";
    }

    std::cout << node->getTypeName() << "#" << node->getId()
        << " [" << node->getStatusName() << "]" << std::endl;

    for (int childId : node->getChildrenIds()) {
        printSubtree(childId, depth + 1);
    }
}