#pragma once

#include <string>
#include <vector>

enum class NodeType {
    Root,
    Stem,
    Branch,
    Leaf,
    Flower,
    RootTip
};

enum class NodeStatus {
    Alive,
    Wilted,
    Pruned
};

class PlantNode {
private:
    int id;
    NodeType type;
    int parentId;
    std::vector<int> childrenIds;

    double water;
    double maxWater;
    double energy;
    double maintenanceCost;
    double photosynthesisRate;
    double survivalScore;

    NodeStatus status;

public:
    PlantNode(int id, NodeType type, int parentId = -1);

    int getId() const;
    NodeType getType() const;
    int getParentId() const;

    void addChild(int childId);
    const std::vector<int>& getChildrenIds() const;

    void addWater(double amount);
    double getWater() const;
    double getMaxWater() const;

    void setEnergy(double value);
    double getEnergy() const;

    void setMaintenanceCost(double value);
    double getMaintenanceCost() const;

    void setPhotosynthesisRate(double value);
    double getPhotosynthesisRate() const;

    void setSurvivalScore(double value);
    double getSurvivalScore() const;

    void setStatus(NodeStatus newStatus);
    NodeStatus getStatus() const;

    void removeChild(int childId);

    std::string getTypeName() const;
    std::string getStatusName() const;
};