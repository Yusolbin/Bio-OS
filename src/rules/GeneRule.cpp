#include "rules\GeneRule.hpp"


GeneRule::GeneRule()
    : conditionLeft(""),
    conditionOperator(ComparisonOperator::Unknown),
    conditionRight(""),
    actionTarget(""),
    actionValue("") {
}

GeneRule::GeneRule(
    const std::string& conditionLeft,
    ComparisonOperator conditionOperator,
    const std::string& conditionRight,
    const std::string& actionTarget,
    const std::string& actionValue
)
    : conditionLeft(conditionLeft),
    conditionOperator(conditionOperator),
    conditionRight(conditionRight),
    actionTarget(actionTarget),
    actionValue(actionValue) {
}

const std::string& GeneRule::getConditionLeft() const {
    return conditionLeft;
}

ComparisonOperator GeneRule::getConditionOperator() const {
    return conditionOperator;
}

const std::string& GeneRule::getConditionRight() const {
    return conditionRight;
}

const std::string& GeneRule::getActionTarget() const {
    return actionTarget;
}

const std::string& GeneRule::getActionValue() const {
    return actionValue;
}

std::string GeneRule::getOperatorSymbol() const {
    return operatorToString(conditionOperator);
}

std::string GeneRule::toString() const {
    return "IF " + conditionLeft + " "
        + getOperatorSymbol() + " "
        + conditionRight + " THEN "
        + actionTarget + " = "
        + actionValue;
}

ComparisonOperator GeneRule::parseOperator(const std::string& symbol) {
    if (symbol == "<") {
        return ComparisonOperator::LessThan;
    }
    if (symbol == ">") {
        return ComparisonOperator::GreaterThan;
    }
    if (symbol == "<=") {
        return ComparisonOperator::LessEqual;
    }
    if (symbol == ">=") {
        return ComparisonOperator::GreaterEqual;
    }
    if (symbol == "==") {
        return ComparisonOperator::Equal;
    }
    if (symbol == "!=") {
        return ComparisonOperator::NotEqual;
    }

    return ComparisonOperator::Unknown;
}

std::string GeneRule::operatorToString(ComparisonOperator op) {
    switch (op) {
    case ComparisonOperator::LessThan:
        return "<";
    case ComparisonOperator::GreaterThan:
        return ">";
    case ComparisonOperator::LessEqual:
        return "<=";
    case ComparisonOperator::GreaterEqual:
        return ">=";
    case ComparisonOperator::Equal:
        return "==";
    case ComparisonOperator::NotEqual:
        return "!=";
    default:
        return "?";
    }
}