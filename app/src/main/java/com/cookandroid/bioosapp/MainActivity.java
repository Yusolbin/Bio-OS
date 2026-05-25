package com.cookandroid.bioosapp;

import android.app.Activity;
import android.os.Bundle;
import android.graphics.Typeface;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Random;

import java.io.FileOutputStream;


import org.json.JSONArray;
import org.json.JSONObject;

public class MainActivity extends Activity {
    private BioOSEngine engine;

    private TextView titleText;
    private TextView summaryText;
    private TextView statesText;
    private TextView nodesText;
    private TextView logsText;
    private TextView rawJsonText;
    private TextView historyText;
    private TextView aiRecommendationText;

    private PlantTreeView plantTreeView;

    private final ArrayList<String> tickHistory = new ArrayList<>();
    private final ArrayList<String> csvHistory = new ArrayList<>();
    private final Random random = new Random();
    private int scenarioIndex = 0;

    private Button rawJsonToggleButton;
    private Button saveCsvButton;
    private Button runRandomButton;
    private Button runBalancedButton;

    private boolean isRawJsonVisible = false;

    private double lastWaterInput = 0.0;
    private double lastLightInput = 0.0;
    private double lastTemperatureInput = 0.0;

    private int datasetSampleIndex = 0;

    private final double[][] scenarios = {
            {15.0, 80.0, 32.0},
            {5.0, 85.0, 34.0},
            {120.0, 75.0, 26.0},
            {150.0, 80.0, 28.0}
    };

    private final String[] scenarioNames = {
            "Dry Tick",
            "Severe Drought Tick",
            "Recovery Tick",
            "Stable Tick"
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        engine = new BioOSEngine();

        addDefaultRules();
        setContentView(createDashboardLayout());

        updateDashboard();
    }

    private View createDashboardLayout() {
        ScrollView scrollView = new ScrollView(this);

        LinearLayout root = new LinearLayout(this);
        root.setOrientation(LinearLayout.VERTICAL);
        root.setPadding(28, 28, 28, 28);

        titleText = new TextView(this);
        titleText.setText("Bio-OS Dashboard");
        titleText.setTextSize(24);
        titleText.setTypeface(null, Typeface.BOLD);
        root.addView(titleText);

        TextView subtitle = new TextView(this);
        subtitle.setText("C++ Bio-System Engine running through JNI");
        subtitle.setTextSize(13);
        subtitle.setPadding(0, 8, 0, 24);
        root.addView(subtitle);

        Button runNextButton = new Button(this);
        runNextButton.setText("Run Next Tick");
        runNextButton.setOnClickListener(v -> runNextScenario());
        root.addView(runNextButton);

        runRandomButton = new Button(this);
        runRandomButton.setText("Run Random 100 Ticks");
        runRandomButton.setOnClickListener(v -> runRandomTicks(100));
        root.addView(runRandomButton);

        runBalancedButton = new Button(this);
        runBalancedButton.setText("Run Balanced Dataset");
        runBalancedButton.setOnClickListener(v -> runBalancedDataset());
        root.addView(runBalancedButton);

        saveCsvButton = new Button(this);
        saveCsvButton.setText("Save Tick History CSV");
        saveCsvButton.setOnClickListener(v -> saveHistoryCsv());
        root.addView(saveCsvButton);

        Button resetButton = new Button(this);
        resetButton.setText("Reset Simulation");
        resetButton.setOnClickListener(v -> resetSimulation());
        root.addView(resetButton);

        summaryText = createSection(root, "Summary");
        statesText = createSection(root, "Active States");
        aiRecommendationText = createSection(root, "AI Recommendation");

        TextView visualTitle = new TextView(this);
        visualTitle.setText("\nPlant Structure View");
        visualTitle.setTextSize(18);
        visualTitle.setTypeface(null, Typeface.BOLD);
        visualTitle.setPadding(0, 18, 0, 8);
        root.addView(visualTitle);

        plantTreeView = new PlantTreeView(this);
        plantTreeView.setPadding(0, 12, 0, 12);
        root.addView(
                plantTreeView,
                new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        620
                )
        );

        nodesText = createSection(root, "Nodes");
        historyText = createSection(root, "Tick History");
        logsText = createSection(root, "Algorithm Logs");

        rawJsonToggleButton = new Button(this);
        rawJsonToggleButton.setText("Show Raw JSON");
        rawJsonToggleButton.setOnClickListener(v -> toggleRawJson());
        root.addView(rawJsonToggleButton);

        rawJsonText = createSection(root, "Raw JSON");
        rawJsonText.setVisibility(View.GONE);

        scrollView.addView(root);
        return scrollView;
    }

    private TextView createSection(LinearLayout root, String title) {
        TextView sectionTitle = new TextView(this);
        sectionTitle.setText("\n" + title);
        sectionTitle.setTextSize(18);
        sectionTitle.setTypeface(null, Typeface.BOLD);
        sectionTitle.setPadding(0, 18, 0, 8);
        root.addView(sectionTitle);

        TextView content = new TextView(this);
        content.setTextSize(14);
        content.setPadding(18, 18, 18, 18);
        root.addView(content);

        return content;
    }

    private void runNextScenario() {
        double water = scenarios[scenarioIndex][0];
        double light = scenarios[scenarioIndex][1];
        double temperature = scenarios[scenarioIndex][2];

        lastWaterInput = water;
        lastLightInput = light;
        lastTemperatureInput = temperature;

        engine.runTick(water, light, temperature);

        scenarioIndex++;
        if (scenarioIndex >= scenarios.length) {
            scenarioIndex = 0;
        }

        updateDashboard();
    }

    private void runRandomTicks(int count) {
        for (int i = 0; i < count; i++) {
            double water = randomRange(0.0, 160.0);
            double light = randomRange(0.0, 100.0);
            double temperature = randomRange(5.0, 45.0);

            lastWaterInput = water;
            lastLightInput = light;
            lastTemperatureInput = temperature;

            engine.runTick(water, light, temperature);

            updateDashboard();
        }

        Toast.makeText(
                this,
                count + " random ticks generated.",
                Toast.LENGTH_LONG
        ).show();
    }

    private void runBalancedDataset() {
        int totalCount = 180;

        tickHistory.clear();
        csvHistory.clear();
        datasetSampleIndex = 0;

        for (int i = 0; i < totalCount; i++) {
            double water;
            double light;
            double temperature;

            int scenarioType = i % 6;

            if (scenarioType == 0) {
                // Severe drought: Pruning 유도
                water = randomRange(0.0, 8.0);
                light = randomRange(70.0, 100.0);
                temperature = randomRange(30.0, 42.0);
            } else if (scenarioType == 1) {
                // Dry stress
                water = randomRange(8.0, 25.0);
                light = randomRange(50.0, 100.0);
                temperature = randomRange(25.0, 40.0);
            } else if (scenarioType == 2) {
                // Recovery / high water
                water = randomRange(110.0, 160.0);
                light = randomRange(60.0, 100.0);
                temperature = randomRange(18.0, 30.0);
            } else if (scenarioType == 3) {
                // Heat stress
                water = randomRange(30.0, 90.0);
                light = randomRange(40.0, 90.0);
                temperature = randomRange(36.0, 45.0);
            } else if (scenarioType == 4) {
                // Low light
                water = randomRange(40.0, 100.0);
                light = randomRange(0.0, 30.0);
                temperature = randomRange(15.0, 28.0);
            } else {
                // Stable condition
                water = randomRange(60.0, 100.0);
                light = randomRange(60.0, 85.0);
                temperature = randomRange(20.0, 30.0);
            }

            runIndependentSample(water, light, temperature);
        }

        updateDashboard();

        Toast.makeText(
                this,
                totalCount + " independent balanced samples generated.",
                Toast.LENGTH_LONG
        ).show();
    }

    private void runIndependentSample(double water, double light, double temperature) {
        if (engine != null) {
            engine.destroy();
        }

        engine = new BioOSEngine();
        addDefaultRules();

        lastWaterInput = water;
        lastLightInput = light;
        lastTemperatureInput = temperature;

        engine.runTick(water, light, temperature);

        datasetSampleIndex++;

        try {
            String json = engine.getSnapshotJson();
            JSONObject root = new JSONObject(json);
            JSONObject summary = root.getJSONObject("summary");

            String lastAction = summary.optString("lastAction", "Unknown");
            double totalEnergy = summary.optDouble("totalEnergy", 0.0);
            int aliveNodes = summary.optInt("aliveNodes", 0);
            int prunedNodes = summary.optInt("prunedNodes", 0);

            String historyLine =
                    "Sample " + datasetSampleIndex +
                            " | " + lastAction +
                            " | Energy " + String.format("%.1f", totalEnergy) +
                            " | Alive " + aliveNodes +
                            " | Pruned " + prunedNodes;

            tickHistory.add(historyLine);

            String csvLine =
                    datasetSampleIndex + "," +
                            lastAction + "," +
                            String.format("%.1f", totalEnergy) + "," +
                            aliveNodes + "," +
                            prunedNodes + "," +
                            String.format("%.1f", water) + "," +
                            String.format("%.1f", light) + "," +
                            String.format("%.1f", temperature);

            csvHistory.add(csvLine);

        } catch (Exception e) {
            Toast.makeText(
                    this,
                    "Sample parse failed: " + e.getMessage(),
                    Toast.LENGTH_SHORT
            ).show();
        }
    }

    private double randomRange(double min, double max) {
        return min + (max - min) * random.nextDouble();
    }

    private void resetSimulation() {
        if (engine != null) {
            engine.destroy();
        }

        engine = new BioOSEngine();

        addDefaultRules();

        scenarioIndex = 0;
        tickHistory.clear();
        csvHistory.clear();

        updateDashboard();
    }

    private void toggleRawJson() {
        isRawJsonVisible = !isRawJsonVisible;

        if (isRawJsonVisible) {
            rawJsonText.setVisibility(View.VISIBLE);
            rawJsonToggleButton.setText("Hide Raw JSON");
        } else {
            rawJsonText.setVisibility(View.GONE);
            rawJsonToggleButton.setText("Show Raw JSON");
        }
    }

    private void updateDashboard() {
        try {
            String json = engine.getSnapshotJson();
            JSONObject root = new JSONObject(json);

            JSONObject summary = root.getJSONObject("summary");

            addHistoryFromSummary(summary);

            String nextScenarioName = scenarioNames[scenarioIndex];

            String summaryResult =
                    "Current Tick: " + summary.optInt("currentTick", summary.optInt("tick", 0)) + "\n" +
                            "Total Energy: " + summary.optDouble("totalEnergy") + "\n" +
                            "Total Nodes: " + summary.optInt("totalNodes") + "\n" +
                            "Alive Nodes: " + summary.optInt("aliveNodes") + "\n" +
                            "Wilted Nodes: " + summary.optInt("wiltedNodes") + "\n" +
                            "Pruned Nodes: " + summary.optInt("prunedNodes") + "\n" +
                            "Survival Score: " + summary.optDouble("survivalScore") + "\n" +
                            "Last Action: " + summary.optString("lastAction") + "\n\n" +
                            "Next Scenario: " + nextScenarioName;

            summaryText.setText(summaryResult);

            statesText.setText(parseStates(root));
            summaryText.setText(summaryResult);

            statesText.setText(parseStates(root));
            aiRecommendationText.setText(makeAiRecommendation(summary));

            if (plantTreeView != null) {
                plantTreeView.updateFromJson(root);
            }

            if (plantTreeView != null) {
                plantTreeView.updateFromJson(root);
            }

            nodesText.setText(parseNodes(root));
            historyText.setText(getHistoryDisplayText());
            logsText.setText(parseLogs(root));
            rawJsonText.setText(json);

        } catch (Exception e) {
            summaryText.setText("Dashboard parse error:\n" + e.getMessage());
        }
    }

    private void addHistoryFromSummary(JSONObject summary) {
        int tick = summary.optInt("currentTick", summary.optInt("tick", 0));

        if (tick == 0) {
            return;
        }

        String prefix = "Tick " + tick + " |";

        for (String oldLine : tickHistory) {
            if (oldLine.startsWith(prefix)) {
                return;
            }
        }

        String lastAction = summary.optString("lastAction", "Unknown");
        double totalEnergy = summary.optDouble("totalEnergy", 0.0);
        int aliveNodes = summary.optInt("aliveNodes", 0);
        int prunedNodes = summary.optInt("prunedNodes", 0);

        String line =
                "Tick " + tick +
                        " | " + lastAction +
                        " | Energy " + String.format("%.1f", totalEnergy) +
                        " | Alive " + aliveNodes +
                        " | Pruned " + prunedNodes;

        tickHistory.add(line);

        String csvLine =
                tick + "," +
                        lastAction + "," +
                        String.format("%.1f", totalEnergy) + "," +
                        aliveNodes + "," +
                        prunedNodes + "," +
                        String.format("%.1f", lastWaterInput) + "," +
                        String.format("%.1f", lastLightInput) + "," +
                        String.format("%.1f", lastTemperatureInput);

        csvHistory.add(csvLine);
    }

    private String getHistoryDisplayText() {
        if (tickHistory.size() == 0) {
            return "No tick history yet.";
        }

        StringBuilder builder = new StringBuilder();

        int start = Math.max(0, tickHistory.size() - 8);

        for (int i = start; i < tickHistory.size(); i++) {
            builder.append(tickHistory.get(i)).append("\n");
        }

        return builder.toString();
    }

    private void saveHistoryCsv() {
        if (csvHistory.size() == 0) {
            Toast.makeText(this, "No tick history to save.", Toast.LENGTH_SHORT).show();
            return;
        }

        StringBuilder csvBuilder = new StringBuilder();

        csvBuilder.append("tick,lastAction,totalEnergy,aliveNodes,prunedNodes,waterInput,light,temperature\n");

        for (String line : csvHistory) {
            csvBuilder.append(line).append("\n");
        }

        try {
            FileOutputStream outputStream = openFileOutput(
                    "bio_os_tick_history.csv",
                    MODE_PRIVATE
            );

            outputStream.write(
                    csvBuilder.toString().getBytes("UTF-8")
            );

            outputStream.close();

            Toast.makeText(
                    this,
                    "CSV saved: bio_os_tick_history.csv",
                    Toast.LENGTH_LONG
            ).show();

        } catch (Exception e) {
            Toast.makeText(
                    this,
                    "CSV save failed: " + e.getMessage(),
                    Toast.LENGTH_LONG
            ).show();
        }
    }

    private String makeAiRecommendation(JSONObject summary) {
        String lastAction = summary.optString("lastAction", "None");

        double totalEnergy = summary.optDouble("totalEnergy", 0.0);
        int aliveNodes = summary.optInt("aliveNodes", 0);
        int prunedNodes = summary.optInt("prunedNodes", 0);
        int currentTick = summary.optInt("currentTick", summary.optInt("tick", 0));

        if (currentTick == 0 || lastAction.equalsIgnoreCase("None")) {
            return
                    "Predicted Risk: Waiting for simulation\n" +
                            "Reason: No tick has been executed yet.\n" +
                            "Suggestion: Press RUN NEXT TICK or RUN BALANCED DATASET to start Bio-OS analysis.\n\n" +
                            "Current Indicators\n" +
                            "- Last Action: " + lastAction + "\n" +
                            "- Total Energy: " + String.format("%.1f", totalEnergy) + "\n" +
                            "- Alive Nodes: " + aliveNodes + "\n" +
                            "- Pruned Nodes: " + prunedNodes;
        }

        String predictedRisk;
        String reason;
        String suggestion;

        if (lastAction.equalsIgnoreCase("PruningAlreadyExecuted")) {
            predictedRisk = "Critical Pruning Risk";
            reason = "Severe stress was detected and pruning was already triggered during state transition.";
            suggestion = "Increase water input above 100 and keep temperature below 30 for recovery.";
        } else if (lastAction.equalsIgnoreCase("Pruning")) {
            predictedRisk = "Pruning Risk";
            reason = "The engine selected a low-survival node for pruning.";
            suggestion = "Raise water input and stabilize light exposure.";
        } else if (lastAction.equalsIgnoreCase("PruningFailed")) {
            predictedRisk = "Structural Damage Risk";
            reason = "The system attempted pruning, but no valid pruning target was available.";
            suggestion = "Reset growth condition with recovery-level water and moderate temperature.";
        } else if (lastAction.equalsIgnoreCase("Stable")) {
            if (totalEnergy > 20.0) {
                predictedRisk = "Stable / Growth Friendly";
                reason = "Energy balance is positive and the plant system is stable.";
                suggestion = "Maintain current environment or slightly increase light for growth.";
            } else {
                predictedRisk = "Stable but Weak";
                reason = "The plant is stable, but total energy is still low.";
                suggestion = "Increase light and water gradually to improve energy balance.";
            }
        } else {
            predictedRisk = "Unknown";
            reason = "The AI helper could not classify the current action.";
            suggestion = "Collect more simulation data for better prediction.";
        }

        String environmentHint = makeEnvironmentHint();

        return
                "Predicted Risk: " + predictedRisk + "\n" +
                        "Reason: " + reason + "\n" +
                        "Suggestion: " + suggestion + "\n\n" +
                        "Environment Hint\n" +
                        environmentHint + "\n\n" +
                        "Current Indicators\n" +
                        "- Last Action: " + lastAction + "\n" +
                        "- Total Energy: " + String.format("%.1f", totalEnergy) + "\n" +
                        "- Alive Nodes: " + aliveNodes + "\n" +
                        "- Pruned Nodes: " + prunedNodes;
    }

    private String makeEnvironmentHint() {
        StringBuilder builder = new StringBuilder();

        builder.append("- Water Input: ")
                .append(String.format("%.1f", lastWaterInput));

        if (lastWaterInput < 10.0) {
            builder.append(" | Severe drought detected");
        } else if (lastWaterInput < 30.0) {
            builder.append(" | Drought stress detected");
        } else if (lastWaterInput > 100.0) {
            builder.append(" | Recovery-level water detected");
        } else {
            builder.append(" | Normal water range");
        }

        builder.append("\n");

        builder.append("- Light: ")
                .append(String.format("%.1f", lastLightInput));

        if (lastLightInput > 70.0) {
            builder.append(" | Photosynthesis boost likely");
        } else if (lastLightInput < 30.0) {
            builder.append(" | Low-light stress possible");
        } else {
            builder.append(" | Moderate light");
        }

        builder.append("\n");

        builder.append("- Temperature: ")
                .append(String.format("%.1f", lastTemperatureInput));

        if (lastTemperatureInput > 35.0) {
            builder.append(" | Heat stress detected");
        } else if (lastTemperatureInput < 10.0) {
            builder.append(" | Cold stress possible");
        } else {
            builder.append(" | Stable temperature");
        }

        return builder.toString();
    }

    private String parseStates(JSONObject root) {
        JSONObject states = root.optJSONObject("states");

        if (states == null || states.length() == 0) {
            return "No active states.";
        }

        StringBuilder builder = new StringBuilder();

        JSONArray names = states.names();
        if (names == null) {
            return "No active states.";
        }

        for (int i = 0; i < names.length(); i++) {
            String key = names.optString(i);
            String value = states.optString(key);

            builder.append("- ")
                    .append(key)
                    .append(": ")
                    .append(value)
                    .append("\n");
        }

        return builder.toString();
    }

    private String parseNodes(JSONObject root) {
        StringBuilder builder = new StringBuilder();

        JSONArray nodes = root.optJSONArray("nodes");
        appendNodeArray(builder, nodes);

        JSONArray plantNodes = root.optJSONArray("plantNodes");
        appendNodeArray(builder, plantNodes);

        JSONObject plantObject = root.optJSONObject("plant");
        if (plantObject != null) {
            JSONArray innerNodes = plantObject.optJSONArray("nodes");
            appendNodeArray(builder, innerNodes);

            JSONArray innerPlantNodes = plantObject.optJSONArray("plantNodes");
            appendNodeArray(builder, innerPlantNodes);
        }

        JSONArray plantArray = root.optJSONArray("plant");
        appendNodeArray(builder, plantArray);

        if (builder.length() == 0) {
            return "No plant nodes.";
        }

        return builder.toString();
    }

    private void appendNodeArray(StringBuilder builder, JSONArray nodes) {
        if (nodes == null) {
            return;
        }

        for (int i = 0; i < nodes.length(); i++) {
            JSONObject node = nodes.optJSONObject(i);

            if (node == null) {
                continue;
            }

            appendSingleNode(builder, node);
        }
    }

    private void appendSingleNode(StringBuilder builder, JSONObject node) {
        int id = node.optInt("id", -1);
        String type = node.optString("type", "Unknown");
        String status = node.optString("status", "Unknown");

        int parent = node.optInt(
                "parentId",
                node.optInt("parent", -1)
        );

        double water = node.optDouble("water", 0.0);
        double maxWater = node.optDouble("maxWater", 0.0);
        double energy = node.optDouble("energy", 0.0);
        double survivalScore = node.optDouble("survivalScore", 0.0);

        builder.append("#")
                .append(id)
                .append(" ")
                .append(type)
                .append(" [")
                .append(status)
                .append("]\n");

        builder.append("Parent: ")
                .append(parent)
                .append("   ");

        builder.append("Water: ")
                .append(String.format("%.1f", water))
                .append("/")
                .append(String.format("%.1f", maxWater))
                .append("   ");

        builder.append("Energy: ")
                .append(String.format("%.1f", energy))
                .append("\n");

        builder.append("Survival Score: ")
                .append(String.format("%.2f", survivalScore))
                .append("\n\n");
    }

    private void appendNodeRecursive(StringBuilder builder, JSONObject node, int depth) {
        if (node == null) {
            return;
        }

        appendSingleNode(builder, node, depth);

        JSONArray children = node.optJSONArray("children");
        if (children != null) {
            for (int i = 0; i < children.length(); i++) {
                JSONObject child = children.optJSONObject(i);
                appendNodeRecursive(builder, child, depth + 1);
            }
        }

        JSONArray childNodes = node.optJSONArray("childNodes");
        if (childNodes != null) {
            for (int i = 0; i < childNodes.length(); i++) {
                JSONObject child = childNodes.optJSONObject(i);
                appendNodeRecursive(builder, child, depth + 1);
            }
        }
    }

    private void appendSingleNode(StringBuilder builder, JSONObject node, int depth) {
        String indent = "";
        for (int i = 0; i < depth; i++) {
            indent += "  ";
        }

        int id = node.optInt("id", -1);
        String type = node.optString("type", "Unknown");
        String status = node.optString("status", "Unknown");

        int parent = node.optInt(
                "parentId",
                node.optInt("parent", -1)
        );

        double water = node.optDouble("water", 0.0);
        double maxWater = node.optDouble("maxWater", 0.0);
        double energy = node.optDouble("energy", 0.0);
        double maintenanceCost = node.optDouble("maintenanceCost", 0.0);
        double photosynthesisRate = node.optDouble("photosynthesisRate", 0.0);
        double survivalScore = node.optDouble("survivalScore", 0.0);

        builder.append(indent)
                .append("#")
                .append(id)
                .append(" ")
                .append(type)
                .append(" [")
                .append(status)
                .append("]\n");

        builder.append(indent)
                .append("Parent: ")
                .append(parent)
                .append("\n");

        builder.append(indent)
                .append("Water: ")
                .append(water)
                .append(" / ")
                .append(maxWater)
                .append("\n");

        builder.append(indent)
                .append("Energy: ")
                .append(energy)
                .append("\n");

        builder.append(indent)
                .append("Maintenance Cost: ")
                .append(maintenanceCost)
                .append("\n");

        builder.append(indent)
                .append("Photosynthesis Rate: ")
                .append(photosynthesisRate)
                .append("\n");

        builder.append(indent)
                .append("Survival Score: ")
                .append(survivalScore)
                .append("\n\n");
    }

    private String parseLogs(JSONObject root) {
        JSONArray logs = root.optJSONArray("logs");

        if (logs == null || logs.length() == 0) {
            return "No logs yet.";
        }

        StringBuilder builder = new StringBuilder();

        int start = Math.max(0, logs.length() - 12);

        for (int i = start; i < logs.length(); i++) {
            JSONObject log = logs.optJSONObject(i);

            if (log == null) {
                continue;
            }

            builder.append("[Tick ")
                    .append(log.optInt("tick"))
                    .append("] ")
                    .append(log.optString("type"))
                    .append(" - ")
                    .append(log.optString("message"))
                    .append("\n");
        }

        return builder.toString();
    }

    private void addDefaultRules() {
        engine.addRule("IF Water < 30 THEN DroughtMode = ON");
        engine.addRule("IF Light > 70 THEN PhotosynthesisBoost = ON");
        engine.addRule("IF Temperature > 35 THEN HeatStress = ON");
        engine.addRule("IF Water < 10 THEN PruningMode = ON");
        engine.addRule("IF Water > 100 THEN RecoveryMode = ON");
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (engine != null) {
            engine.destroy();
        }
    }
}