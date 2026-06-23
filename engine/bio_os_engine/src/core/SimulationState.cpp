#include "core/SimulationState.hpp"

SimulationState::SimulationState() {
    reset();
}

void SimulationState::reset() {
    currentTick = 0;
    totalEnergy = 0.0;

    totalNodes = 0;
    aliveNodes = 0;
    wiltedNodes = 0;
    prunedNodes = 0;

    survivalScore = 0.0;
    lastAction = "None";
}

void SimulationState::setCurrentTick(int value) {
    currentTick = value;
}

int SimulationState::getCurrentTick() const {
    return currentTick;
}

void SimulationState::setTotalEnergy(double value) {
    totalEnergy = value;
}

double SimulationState::getTotalEnergy() const {
    return totalEnergy;
}

void SimulationState::setTotalNodes(int value) {
    totalNodes = value;
}

int SimulationState::getTotalNodes() const {
    return totalNodes;
}

void SimulationState::setAliveNodes(int value) {
    aliveNodes = value;
}

int SimulationState::getAliveNodes() const {
    return aliveNodes;
}

void SimulationState::setWiltedNodes(int value) {
    wiltedNodes = value;
}

int SimulationState::getWiltedNodes() const {
    return wiltedNodes;
}

void SimulationState::setPrunedNodes(int value) {
    prunedNodes = value;
}

int SimulationState::getPrunedNodes() const {
    return prunedNodes;
}

void SimulationState::setSurvivalScore(double value) {
    survivalScore = value;
}

double SimulationState::getSurvivalScore() const {
    return survivalScore;
}

void SimulationState::setLastAction(const std::string& value) {
    lastAction = value;
}

const std::string& SimulationState::getLastAction() const {
    return lastAction;
}