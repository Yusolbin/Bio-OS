#pragma once
#include "GeneRule.hpp"
#include "core\Environment.hpp"

#include <string>
#include <unordered_map>
#include <vector>

class LogicInferenceEngine {
private:
    std::unordered_map<std::string, std::string> states;

public:
    LogicInferenceEngine();

    bool evaluateRule(const GeneRule& rule, const Environment& environment);
    void applyRule(const GeneRule& rule);

    void evaluateAndApply(
        const std::vector<GeneRule>& rules,
        const Environment& environment
    );

    std::string getState(const std::string& key) const;
    const std::unordered_map<std::string, std::string>& getStates() const;

    void clearStates();
    void printStates() const;

private:
    double getEnvironmentValue(const std::string& key, const Environment& environment) const;
    bool compare(double left, ComparisonOperator op, double right) const;
};