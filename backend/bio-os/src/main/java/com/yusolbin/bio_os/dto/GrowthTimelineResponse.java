package com.yusolbin.bio_os.dto;

import com.yusolbin.bio_os.model.GrowthTimeline;

import java.util.Arrays;
import java.util.List;

public class GrowthTimelineResponse {

    private int day;

    private double water;
    private double light;
    private double temperature;
    private double humidity;

    private double growthScore;
    private double totalEnergy;

    private String visualState;
    private String riskLevel;

    private List<String> activeStates;
    private List<String> matchedRules;

    public GrowthTimelineResponse(
            int day,
            double water,
            double light,
            double temperature,
            double humidity,
            double growthScore,
            double totalEnergy,
            String visualState,
            String riskLevel,
            List<String> activeStates,
            List<String> matchedRules
    ) {
        this.day = day;
        this.water = water;
        this.light = light;
        this.temperature = temperature;
        this.humidity = humidity;
        this.growthScore = growthScore;
        this.totalEnergy = totalEnergy;
        this.visualState = visualState;
        this.riskLevel = riskLevel;
        this.activeStates = activeStates;
        this.matchedRules = matchedRules;
    }

    public GrowthTimelineResponse(GrowthTimeline timeline) {
        this.day = timeline.getDay();
        this.water = timeline.getWater();
        this.light = timeline.getLight();
        this.temperature = timeline.getTemperature();
        this.humidity = timeline.getHumidity();
        this.growthScore = timeline.getGrowthScore();
        this.totalEnergy = timeline.getTotalEnergy();
        this.visualState = timeline.getVisualState();
        this.riskLevel = timeline.getRiskLevel();
        this.activeStates = parseCommaList(timeline.getActiveStates());
        this.matchedRules = parsePipeList(timeline.getMatchedRules());
    }

    private List<String> parseCommaList(String text) {
        if (text == null || text.isBlank()) {
            return List.of();
        }

        return Arrays.stream(text.split(","))
                .map(String::trim)
                .filter(value -> !value.isBlank())
                .toList();
    }

    private List<String> parsePipeList(String text) {
        if (text == null || text.isBlank()) {
            return List.of();
        }

        return Arrays.stream(text.split("\\|"))
                .map(String::trim)
                .filter(value -> !value.isBlank())
                .toList();
    }

    public int getDay() {
        return day;
    }

    public double getWater() {
        return water;
    }

    public double getLight() {
        return light;
    }

    public double getTemperature() {
        return temperature;
    }

    public double getHumidity() {
        return humidity;
    }

    public double getGrowthScore() {
        return growthScore;
    }

    public double getTotalEnergy() {
        return totalEnergy;
    }

    public String getVisualState() {
        return visualState;
    }

    public String getRiskLevel() {
        return riskLevel;
    }

    public List<String> getActiveStates() {
        return activeStates;
    }

    public List<String> getMatchedRules() {
        return matchedRules;
    }
}