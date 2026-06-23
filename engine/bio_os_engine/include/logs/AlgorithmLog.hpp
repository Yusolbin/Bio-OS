#pragma once


#include <string>

enum class LogType {
    Tick,
    WaterDistribution,
    EnergyEvaluation,
    Pruning,
    Growth,
    Rule,
    System
};

class AlgorithmLog {
private:
    int tick;
    LogType type;
    std::string message;

public:
    AlgorithmLog(int tick, LogType type, const std::string& message);

    int getTick() const;
    LogType getType() const;
    std::string getMessage() const;

    std::string getTypeName() const;
    std::string toString() const;
};