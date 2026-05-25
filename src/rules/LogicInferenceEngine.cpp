#include "rules\LogicInferenceEngine.hpp"

#include <iostream>
#include <stdexcept>

LogicInferenceEngine::LogicInferenceEngine() {}

bool LogicInferenceEngine::evaluateRule(const GeneRule& rule, const Environment& environment) {
    double leftValue = getEnvironmentValue(rule.getConditionLeft(), environment);
    double rightValue = std::stod(rule.getConditionRight());

    return compare(leftValue, rule.getConditionOperator(), rightValue);
}

void LogicInferenceEngine::applyRule(const GeneRule& rule) {
    states[rule.getActionTarget()] = rule.getActionValue();

    std::cout << "Rule Fired: "
        << rule.getActionTarget()
        << " = "
        << rule.getActionValue()
        << std::endl;
}

void LogicInferenceEngine::evaluateAndApply(
    const std::vector<GeneRule>& rules,
    const Environment& environment
) {
    std::cout << "[Logic Inference Start]" << std::endl;

    for (const GeneRule& rule : rules) {
        std::cout << "Checking rule: " << rule.toString() << std::endl;

        if (evaluateRule(rule, environment)) {
            applyRule(rule);
        }
        else {
            std::cout << "Rule not fired." << std::endl;
        }
    }

    std::cout << "[Logic Inference Completed]" << std::endl;
}

std::string LogicInferenceEngine::getState(const std::string& key) const {
    auto it = states.find(key);

    if (it == states.end()) {
        return "";
    }

    return it->second;
}

const std::unordered_map<std::string, std::string>& LogicInferenceEngine::getStates() const {
    return states;
}

void LogicInferenceEngine::printStates() const {
    std::cout << "[Current Inferred States]" << std::endl;

    if (states.empty()) {
        std::cout << "(empty)" << std::endl;
        return;
    }

    for (const auto& pair : states) {
        std::cout << pair.first << " = " << pair.second << std::endl;
    }
}

double LogicInferenceEngine::getEnvironmentValue(
    const std::string& key,
    const Environment& environment
) const {
    if (key == "Water") {
        return environment.getWaterInput();
    }

    if (key == "Light") {
        return environment.getLight();
    }

    if (key == "Temperature") {
        return environment.getTemperature();
    }

    throw std::invalid_argument("Unknown environment key: " + key);
}

bool LogicInferenceEngine::compare(
    double left,
    ComparisonOperator op,
    double right
) const {
    switch (op) {
    case ComparisonOperator::LessThan:
        return left < right;
    case ComparisonOperator::GreaterThan:
        return left > right;
    case ComparisonOperator::LessEqual:
        return left <= right;
    case ComparisonOperator::GreaterEqual:
        return left >= right;
    case ComparisonOperator::Equal:
        return left == right;
    case ComparisonOperator::NotEqual:
        return left != right;
    default:
        return false;
    }
}

void LogicInferenceEngine::clearStates() {
    states.clear();
}