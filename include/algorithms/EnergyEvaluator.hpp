#pragma once

#include "core\PlantTree.hpp"

class EnergyEvaluator {
public:
    static double evaluate(PlantTree& plant);

private:
    static double evaluateSubtree(PlantTree& plant, int nodeId);
};