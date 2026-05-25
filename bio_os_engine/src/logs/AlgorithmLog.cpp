#include "logs\AlgorithmLog.hpp"

AlgorithmLog::AlgorithmLog(int tick, LogType type, const std::string& message)
    : tick(tick), type(type), message(message) {
}

int AlgorithmLog::getTick() const {
    return tick;
}

LogType AlgorithmLog::getType() const {
    return type;
}

std::string AlgorithmLog::getMessage() const {
    return message;
}

std::string AlgorithmLog::getTypeName() const {
    switch (type) {
    case LogType::Tick:
        return "TICK";
    case LogType::WaterDistribution:
        return "WATER";
    case LogType::EnergyEvaluation:
        return "ENERGY";
    case LogType::Pruning:
        return "PRUNING";
    case LogType::Growth:
        return "GROWTH";
    case LogType::Rule:
        return "RULE";
    case LogType::System:
        return "SYSTEM";
    default:
        return "UNKNOWN";
    }
}

std::string AlgorithmLog::toString() const {
    return "[Tick " + std::to_string(tick) + "] "
        + "[" + getTypeName() + "] "
        + message;
}