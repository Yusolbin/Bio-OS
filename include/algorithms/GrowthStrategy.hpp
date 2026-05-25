#pragma once

#include "core/PlantTree.hpp"

class GrowthStrategy {
public:
    static int growRootTip(PlantTree& plant);
    static int growLeafOnFirstStem(PlantTree& plant);
    static void boostLeafPhotosynthesis(PlantTree& plant, double amount);

private:
    static int findFirstNodeByType(const PlantTree& plant, NodeType type);
};
