#pragma once

class Environment {
private:
	double waterInput;
	double light;
	double temperature;

public:
	Environment();
	Environment(double waterInput, double light, double temperature);

	double getWaterInput() const;
	double getLight() const;
	double getTemperature() const;

	void setWaterInput(double value);
	void setLight(double value);
	void setTemperature(double value);
};

