#include "core\PlantNode.hpp"
#include <algorithm>

PlantNode::PlantNode(int id, NodeType type, int parentId)
    : id(id),
    type(type),
    parentId(parentId),
    water(0.0),
    maxWater(100.0),
    energy(0.0),
    maintenanceCost(1.0),
    photosynthesisRate(0.0),
    survivalScore(0.0),
    status(NodeStatus::Alive) {
}

int PlantNode::getId() const {
    return id;
}

NodeType PlantNode::getType() const {
    return type;
}

int PlantNode::getParentId() const {
    return parentId;
}

void PlantNode::addChild(int childId) {
    childrenIds.push_back(childId);
}

const std::vector<int>& PlantNode::getChildrenIds() const {
    return childrenIds;
}

void PlantNode::addWater(double amount) {
    water += amount;

    if (water > maxWater) {
        water = maxWater;
    }

    if (water < 0) {
        water = 0;
    }
}

double PlantNode::getWater() const {
    return water;
}

double PlantNode::getMaxWater() const {
    return maxWater;
}

void PlantNode::setEnergy(double value) {
    energy = value;
}

double PlantNode::getEnergy() const {
    return energy;
}

void PlantNode::setMaintenanceCost(double value) {
    maintenanceCost = value;
}

double PlantNode::getMaintenanceCost() const {
    return maintenanceCost;
}

void PlantNode::setPhotosynthesisRate(double value) {
    photosynthesisRate = value;
}

double PlantNode::getPhotosynthesisRate() const {
    return photosynthesisRate;
}

void PlantNode::setSurvivalScore(double value) {
    survivalScore = value;
}

double PlantNode::getSurvivalScore() const {
    return survivalScore;
}

void PlantNode::setStatus(NodeStatus newStatus) {
    status = newStatus;
}

NodeStatus PlantNode::getStatus() const {
    return status;
}

void PlantNode::removeChild(int childId) {
    childrenIds.erase(
        std::remove(childrenIds.begin(), childrenIds.end(), childId),
        childrenIds.end()
    );
}

std::string PlantNode::getTypeName() const {
    switch (type) {
    case NodeType::Root:
        return "Root";
    case NodeType::Stem:
        return "Stem";
    case NodeType::Branch:
        return "Branch";
    case NodeType::Leaf:
        return "Leaf";
    case NodeType::Flower:
        return "Flower";
    case NodeType::RootTip:
        return "RootTip";
    default:
        return "Unknown";
    }
}

std::string PlantNode::getStatusName() const {
    switch (status) {
    case NodeStatus::Alive:
        return "Alive";
    case NodeStatus::Wilted:
        return "Wilted";
    case NodeStatus::Pruned:
        return "Pruned";
    default:
        return "Unknown";
    }
}