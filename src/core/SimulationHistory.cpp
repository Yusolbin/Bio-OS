#include "core/SimulationHistory.hpp"

#include <iostream>
#include <sstream>

void SimulationHistory::addState(const SimulationState& state) {
    states.push_back(state);
}

const std::vector<SimulationState>& SimulationHistory::getStates() const {
    return states;
}

void SimulationHistory::clear() {
    states.clear();
}

void SimulationHistory::printHistory() const {
    std::cout << "[Simulation History]" << std::endl;

    if (states.empty()) {
        std::cout << "(empty)" << std::endl;
        return;
    }

    for (const SimulationState& state : states) {
        std::cout << "Tick " << state.getCurrentTick()
            << " | Energy=" << state.getTotalEnergy()
            << " | Total=" << state.getTotalNodes()
            << " | Alive=" << state.getAliveNodes()
            << " | Pruned=" << state.getPrunedNodes()
            << " | Score=" << state.getSurvivalScore()
            << " | Action=" << state.getLastAction()
            << std::endl;
    }
}

std::string SimulationHistory::toText() const {
    std::ostringstream oss;

    for (const SimulationState& state : states) {
        oss << "Tick " << state.getCurrentTick()
            << " | Energy=" << state.getTotalEnergy()
            << " | Total=" << state.getTotalNodes()
            << " | Alive=" << state.getAliveNodes()
            << " | Pruned=" << state.getPrunedNodes()
            << " | Score=" << state.getSurvivalScore()
            << " | Action=" << state.getLastAction()
            << "\n";
    }

    return oss.str();
}