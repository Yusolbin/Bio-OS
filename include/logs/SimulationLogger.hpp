#pragma once

#include "AlgorithmLog.hpp"
#include <vector>
#include <string>

class SimulationLogger {
private:
    std::vector<AlgorithmLog> logs;

public:
    void addLog(int tick, LogType type, const std::string& message);

    const std::vector<AlgorithmLog>& getLogs() const;

    void printAll() const;
    void clear();

    std::string toText() const;
};