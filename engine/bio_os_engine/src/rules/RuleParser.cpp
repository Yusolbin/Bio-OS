#include "..\..\include\rules\RuleParser.hpp"

#include <sstream>
#include <stdexcept>
#include <algorithm>
#include <cctype>

GeneRule RuleParser::parse(const std::string& ruleText) {
    std::string ifKeyword;
    std::string conditionLeft;
    std::string operatorSymbol;
    std::string conditionRight;
    std::string thenKeyword;
    std::string actionTarget;
    std::string equalSymbol;
    std::string actionValue;

    std::stringstream ss(ruleText);

    ss >> ifKeyword
        >> conditionLeft
        >> operatorSymbol
        >> conditionRight
        >> thenKeyword
        >> actionTarget
        >> equalSymbol
        >> actionValue;

    if (ifKeyword != "IF") {
        throw std::invalid_argument("Rule must start with IF.");
    }

    if (thenKeyword != "THEN") {
        throw std::invalid_argument("Rule must contain THEN.");
    }

    if (equalSymbol != "=") {
        throw std::invalid_argument("Action must use = symbol.");
    }

    ComparisonOperator op = GeneRule::parseOperator(operatorSymbol);

    if (op == ComparisonOperator::Unknown) {
        throw std::invalid_argument("Unknown comparison operator: " + operatorSymbol);
    }

    return GeneRule(
        conditionLeft,
        op,
        conditionRight,
        actionTarget,
        actionValue
    );
}

std::string RuleParser::trim(const std::string& text) {
    size_t start = 0;
    while (start < text.size() && std::isspace(static_cast<unsigned char>(text[start]))) {
        start++;
    }

    size_t end = text.size();
    while (end > start && std::isspace(static_cast<unsigned char>(text[end - 1]))) {
        end--;
    }

    return text.substr(start, end - start);
}