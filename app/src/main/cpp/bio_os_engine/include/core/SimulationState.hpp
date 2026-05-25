#pragma once

#include <string>

class SimulationState {
private:
    int currentTick;
    double totalEnergy;

    int totalNodes;
    int aliveNodes;
    int wiltedNodes;
    int prunedNodes;

    double survivalScore;
    std::string lastAction;

public:
    SimulationState();

    void reset();

    void setCurrentTick(int value);
    int getCurrentTick() const;

    void setTotalEnergy(double value);
    double getTotalEnergy() const;

    void setTotalNodes(int value);
    int getTotalNodes() const;

    void setAliveNodes(int value);
    int getAliveNodes() const;

    void setWiltedNodes(int value);
    int getWiltedNodes() const;

    void setPrunedNodes(int value);
    int getPrunedNodes() const;

    void setSurvivalScore(double value);
    double getSurvivalScore() const;

    void setLastAction(const std::string& value);
    const std::string& getLastAction() const;
};
