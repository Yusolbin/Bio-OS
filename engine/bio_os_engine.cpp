#include <iostream>
#include <string>
#include <vector>
#include <sstream>
#include <iomanip>
#include <cstdlib>

using namespace std;

struct SimulationResult {
    int tick;
    double water;
    double light;
    double temperature;
    double humidity;
    double totalEnergy;
    double energyDelta;
    string riskLevel;
    string lastAction;
    string visualState;
    vector<string> activeStates;
    vector<string> matchedRules;
    string recommendation;
    string engineSource;
};

string escapeJson(const string& value) {
    string result;

    for (char c : value) {
        switch (c) {
            case '"':
                result += "\\\"";
                break;
            case '\\':
                result += "\\\\";
                break;
            case '\n':
                result += "\\n";
                break;
            case '\r':
                result += "\\r";
                break;
            case '\t':
                result += "\\t";
                break;
            default:
                result += c;
                break;
        }
    }

    return result;
}

string vectorToJsonArray(const vector<string>& items) {
    stringstream ss;

    ss << "[";

    for (size_t i = 0; i < items.size(); i++) {
        ss << "\"" << escapeJson(items[i]) << "\"";

        if (i + 1 < items.size()) {
            ss << ",";
        }
    }

    ss << "]";

    return ss.str();
}

string resultToJson(const SimulationResult& result) {
    stringstream ss;

    ss << fixed << setprecision(1);

    ss << "{";
    ss << "\"tick\":" << result.tick << ",";
    ss << "\"water\":" << result.water << ",";
    ss << "\"light\":" << result.light << ",";
    ss << "\"temperature\":" << result.temperature << ",";
    ss << "\"humidity\":" << result.humidity << ",";
    ss << "\"totalEnergy\":" << result.totalEnergy << ",";
    ss << "\"energyDelta\":" << result.energyDelta << ",";
    ss << "\"riskLevel\":\"" << escapeJson(result.riskLevel) << "\",";
    ss << "\"lastAction\":\"" << escapeJson(result.lastAction) << "\",";
    ss << "\"visualState\":\"" << escapeJson(result.visualState) << "\",";
    ss << "\"activeStates\":" << vectorToJsonArray(result.activeStates) << ",";
    ss << "\"matchedRules\":" << vectorToJsonArray(result.matchedRules) << ",";
    ss << "\"recommendation\":\"" << escapeJson(result.recommendation) << "\",";
    ss << "\"engineSource\":\"" << escapeJson(result.engineSource) << "\"";
    ss << "}";

    return ss.str();
}

double calculateBaseEnergy(double water, double light, double temperature, double humidity) {
    double energy = 100.0;

    if (water < 30.0) {
        energy -= 30.0;
    }

    if (water > 120.0) {
        energy -= 12.0;
    }

    if (light < 25.0) {
        energy -= 18.0;
    }

    if (light > 70.0 && light <= 100.0) {
        energy += 10.0;
    }

    if (temperature > 35.0) {
        energy -= 24.0;
    }

    if (temperature < 10.0) {
        energy -= 22.0;
    }

    if (humidity < 30.0) {
        energy -= 12.0;
    }

    if (humidity > 85.0) {
        energy -= 8.0;
    }

    if (water >= 45.0 && water <= 90.0 &&
        light >= 45.0 && light <= 90.0 &&
        temperature >= 18.0 && temperature <= 30.0 &&
        humidity >= 40.0 && humidity <= 75.0) {
        energy += 18.0;
    }

    if (energy < 0.0) {
        energy = 0.0;
    }

    if (energy > 150.0) {
        energy = 150.0;
    }

    return energy;
}

string determineRiskLevel(double totalEnergy) {
    if (totalEnergy <= 25.0) {
        return "CRITICAL";
    }

    if (totalEnergy <= 55.0) {
        return "HIGH";
    }

    if (totalEnergy <= 85.0) {
        return "MEDIUM";
    }

    return "LOW";
}

string determineVisualState(
    double water,
    double light,
    double temperature,
    double humidity,
    double totalEnergy
) {
    if (totalEnergy <= 25.0) {
        return "dead_critical";
    }

    if (totalEnergy <= 45.0) {
        return "low_energy";
    }

    if (water < 30.0) {
        return "drought_mode";
    }

    if (temperature > 35.0) {
        return "heat_stress";
    }

    if (temperature < 10.0) {
        return "cold_stress";
    }

    if (water >= 90.0 && totalEnergy < 80.0) {
        return "recovery_mode";
    }

    if (light >= 75.0 && totalEnergy >= 90.0) {
        return "photosynthesis_boost";
    }

    return "stable";
}

string determineLastAction(
    double water,
    double light,
    double temperature,
    double humidity,
    double totalEnergy
) {
    if (totalEnergy <= 25.0) {
        return "EmergencyRecovery";
    }

    if (water < 30.0) {
        return "IncreaseWater";
    }

    if (temperature > 35.0) {
        return "ReduceTemperature";
    }

    if (temperature < 10.0) {
        return "IncreaseTemperature";
    }

    if (light < 25.0) {
        return "IncreaseLight";
    }

    if (humidity < 30.0) {
        return "IncreaseHumidity";
    }

    if (totalEnergy >= 110.0) {
        return "MaintainGrowth";
    }

    return "None";
}

vector<string> determineActiveStates(
    double water,
    double light,
    double temperature,
    double humidity,
    double totalEnergy
) {
    vector<string> states;

    if (water < 30.0) {
        states.push_back("DroughtMode");
    }

    if (temperature > 35.0) {
        states.push_back("HeatStress");
    }

    if (temperature < 10.0) {
        states.push_back("ColdStress");
    }

    if (humidity < 30.0) {
        states.push_back("LowHumidity");
    }

    if (light >= 75.0 && totalEnergy >= 90.0) {
        states.push_back("PhotosynthesisBoost");
    }

    if (water >= 90.0 && totalEnergy < 80.0) {
        states.push_back("RecoveryMode");
    }

    if (totalEnergy <= 45.0) {
        states.push_back("LowEnergy");
    }

    if (totalEnergy <= 25.0) {
        states.push_back("DeadCritical");
    }

    return states;
}

vector<string> determineMatchedRules(
    double water,
    double light,
    double temperature,
    double humidity,
    double totalEnergy
) {
    vector<string> rules;

    if (water < 30.0) {
        rules.push_back("IF Water < 30 THEN DroughtMode");
    }

    if (temperature > 35.0) {
        rules.push_back("IF Temperature > 35 THEN HeatStress");
    }

    if (temperature < 10.0) {
        rules.push_back("IF Temperature < 10 THEN ColdStress");
    }

    if (humidity < 30.0) {
        rules.push_back("IF Humidity < 30 THEN LowHumidity");
    }

    if (light >= 75.0 && totalEnergy >= 90.0) {
        rules.push_back("IF Light >= 75 AND Energy >= 90 THEN PhotosynthesisBoost");
    }

    if (totalEnergy <= 45.0) {
        rules.push_back("IF Energy <= 45 THEN LowEnergy");
    }

    if (totalEnergy <= 25.0) {
        rules.push_back("IF Energy <= 25 THEN DeadCritical");
    }

    return rules;
}

string makeRecommendation(
    double water,
    double light,
    double temperature,
    double humidity,
    double totalEnergy,
    const string& visualState
) {
    if (visualState == "dead_critical") {
        return "Critical state detected. Increase water and stabilize temperature immediately.";
    }

    if (visualState == "low_energy") {
        return "Energy is low. Adjust water, light, and temperature to recover stability.";
    }

    if (visualState == "drought_mode") {
        return "Water input is too low. Increase water gradually.";
    }

    if (visualState == "heat_stress") {
        return "Temperature is too high. Reduce temperature below 35.";
    }

    if (visualState == "cold_stress") {
        return "Temperature is too low. Increase temperature above 10.";
    }

    if (visualState == "recovery_mode") {
        return "Recovery mode is active. Maintain stable water and temperature.";
    }

    if (visualState == "photosynthesis_boost") {
        return "Photosynthesis boost is active. Current light condition is favorable.";
    }

    return "Current environment is stable. Maintain current conditions.";
}

SimulationResult runSimulation(double water, double light, double temperature, double humidity) {
    double totalEnergy = calculateBaseEnergy(water, light, temperature, humidity);
    double energyDelta = totalEnergy - 100.0;

    string riskLevel = determineRiskLevel(totalEnergy);
    string visualState = determineVisualState(water, light, temperature, humidity, totalEnergy);
    string lastAction = determineLastAction(water, light, temperature, humidity, totalEnergy);

    vector<string> activeStates = determineActiveStates(
        water,
        light,
        temperature,
        humidity,
        totalEnergy
    );

    vector<string> matchedRules = determineMatchedRules(
        water,
        light,
        temperature,
        humidity,
        totalEnergy
    );

    string recommendation = makeRecommendation(
        water,
        light,
        temperature,
        humidity,
        totalEnergy,
        visualState
    );

    SimulationResult result;

    result.tick = 1;
    result.water = water;
    result.light = light;
    result.temperature = temperature;
    result.humidity = humidity;
    result.totalEnergy = totalEnergy;
    result.energyDelta = energyDelta;
    result.riskLevel = riskLevel;
    result.lastAction = lastAction;
    result.visualState = visualState;
    result.activeStates = activeStates;
    result.matchedRules = matchedRules;
    result.recommendation = recommendation;
    result.engineSource = "CPP_CLI_ENGINE";

    return result;
}

int main(int argc, char* argv[]) {
    if (argc != 5) {
        cerr << "Usage: bio_os_engine.exe <water> <light> <temperature> <humidity>" << endl;
        return 1;
    }

    double water = atof(argv[1]);
    double light = atof(argv[2]);
    double temperature = atof(argv[3]);
    double humidity = atof(argv[4]);

    SimulationResult result = runSimulation(water, light, temperature, humidity);

    cout << resultToJson(result) << endl;

    return 0;
}
