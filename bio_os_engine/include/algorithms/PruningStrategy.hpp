#pragma once

#include "core\PlantTree.hpp"

class PruningStrategy {
public:
	static bool pruneLowestValueLeaf(PlantTree& plant);
};