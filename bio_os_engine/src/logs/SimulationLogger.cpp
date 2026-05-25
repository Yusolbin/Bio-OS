#include "D:\CoreSync\Bio_OS\bio_os_engine\include\logs/SimulationLogger.hpp"

#include <iostream>

void SimulationLogger::addLog(int tick, LogType type, const std::string& message) {
    logs.emplace_back(tick, type, message);
}

const std::vector<AlgorithmLog>& SimulationLogger::getLogs() const {
    return logs;
}

void SimulationLogger::printAll() const {
    std::cout << "[Simulation Logs]" << std::endl;

    for (const AlgorithmLog& log : logs) {
        std::cout << log.toString() << std::endl;
    }
}

void SimulationLogger::clear() {
    logs.clear();
}

std::string SimulationLogger::toText() const {
    std::string result;

    for (const AlgorithmLog& log : logs) {
        result += log.toString();
        result += "\n";
    }

    return result;
}