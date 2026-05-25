#pragma once

#include <string>

enum class ComparisonOperator {
    LessThan,
    GreaterThan,
    LessEqual,
    GreaterEqual,
    Equal,
    NotEqual,
    Unknown
};

class GeneRule {
private:
    std::string conditionLeft;
    ComparisonOperator conditionOperator;
    std::string conditionRight;

    std::string actionTarget;
    std::string actionValue;

public:
    GeneRule();
    GeneRule(
        const std::string& conditionLeft,
        ComparisonOperator conditionOperator,
        const std::string& conditionRight,
        const std::string& actionTarget,
        const std::string& actionValue
    );

    const std::string& getConditionLeft() const;
    ComparisonOperator getConditionOperator() const;
    const std::string& getConditionRight() const;

    const std::string& getActionTarget() const;
    const std::string& getActionValue() const;

    std::string getOperatorSymbol() const;
    std::string toString() const;

    static ComparisonOperator parseOperator(const std::string& symbol);
    static std::string operatorToString(ComparisonOperator op);
};