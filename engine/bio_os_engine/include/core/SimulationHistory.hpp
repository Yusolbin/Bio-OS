#pragma once

#include "core/SimulationState.hpp"

#include <vector>
#include <string>

class SimulationHistory {
private:
    std::vector<SimulationState> states;

public:
    void addState(const SimulationState& state);

    const std::vector<SimulationState>& getStates() const;

    void clear();

    void printHistory() const;

    std::string toText() const;
};