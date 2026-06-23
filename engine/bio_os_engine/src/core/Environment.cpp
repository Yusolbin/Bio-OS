#include "core\Environment.hpp"

Environment::Environment()
	: waterInput(0.0), light(50.0), temperature(25.0) {}

Environment::Environment(double waterInput, double light, double temperature)
    : waterInput(waterInput), light(light), temperature(temperature) {
}

double Environment::getWaterInput() const {
    return waterInput;
}

double Environment::getLight() const {
    return light;
}

double Environment::getTemperature() const {
    return temperature;
}

void Environment::setWaterInput(double value) {
    waterInput = value;
}

void Environment::setLight(double value) {
    light = value;
}

void Environment::setTemperature(double value) {
    temperature = value;
}