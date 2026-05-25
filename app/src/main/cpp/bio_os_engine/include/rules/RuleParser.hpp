#pragma once

#include "GeneRule.hpp"
#include <string>

class RuleParser {
public:
	static GeneRule parse(const std::string& ruleText);
	
private:
	static std::string trim(const std::string& text);
};