let tick = 0;

const rules = [
    { field: "Water", operator: "<", threshold: 30, state: "DroughtMode" },
    { field: "Light", operator: ">", threshold: 70, state: "PhotosynthesisBoost" },
    { field: "Temperature", operator: ">", threshold: 35, state: "HeatStress" },
    { field: "Water", operator: "<", threshold: 10, state: "PruningMode" },
    { field: "Water", operator: ">", threshold: 100, state: "RecoveryMode" },
];

const plantImages = {
    stable: "assets/stable.png",
    drought_mode: "assets/drought_mode.png",
    heat_stress: "assets/heat_stress.png",
    pruned: "assets/pruned.png",
    pruning_already_executed: "assets/pruning_already_executed.png",
    recovery_mode: "assets/recovery_mode.png",
    low_energy: "assets/low_energy.png",
    dead_critical: "assets/dead_critical.png",
    photosynthesis_boost: "assets/photosynthesis_boost.png",
    cold_stress: "assets/cold_stress.png",
};

const waterInput = document.getElementById("waterInput");
const lightInput = document.getElementById("lightInput");
const temperatureInput = document.getElementById("temperatureInput");

const runButton = document.getElementById("runButton");
const randomButton = document.getElementById("randomButton");
const loadLogsButton = document.getElementById("loadLogsButton");
const resetButton = document.getElementById("resetButton");

const addRuleButton = document.getElementById("addRuleButton");
const ruleList = document.getElementById("ruleList");

const plantImage = document.getElementById("plantImage");
const visualState = document.getElementById("visualState");

const tickValue = document.getElementById("tickValue");
const lastActionValue = document.getElementById("lastActionValue");
const energyValue = document.getElementById("energyValue");
const visualStateValue = document.getElementById("visualStateValue");

const activeStatesBox = document.getElementById("activeStatesBox");
const recommendationBox = document.getElementById("recommendationBox");
const historyTable = document.getElementById("historyTable");

runButton.addEventListener("click", () => {
    runSimulationFromInput();
});

randomButton.addEventListener("click", () => {
    waterInput.value = randomRange(0, 160).toFixed(1);
    lightInput.value = randomRange(0, 100).toFixed(1);
    temperatureInput.value = randomRange(5, 45).toFixed(1);

    runSimulationFromInput();
});

resetButton.addEventListener("click", () => {
    tick = 0;
    historyTable.innerHTML = "";
    renderResult({
        tick: 0,
        water: 0,
        light: 0,
        temperature: 0,
        totalEnergy: 100,
        lastAction: "None",
        activeStates: [],
        visual: "stable",
    });
});

loadLogsButton.addEventListener("click", () => {
    loadSimulationLogs();
});

addRuleButton.addEventListener("click", () => {
    const field = document.getElementById("ruleField").value;
    const operator = document.getElementById("ruleOperator").value;
    const threshold = Number(document.getElementById("ruleThreshold").value);
    const state = document.getElementById("ruleState").value.trim();

    if (!state) {
        alert("State name을 입력해줘.");
        return;
    }

    rules.push({ field, operator, threshold, state });
    renderRules();
});

async function runSimulationFromInput() {
    const water = Number(waterInput.value);
    const light = Number(lightInput.value);
    const temperature = Number(temperatureInput.value);

    try {
        const response = await fetch("http://localhost:8080/api/simulations/run", {
            method: "POST",
            headers: {
                "Content-Type": "application/json",
            },
            body: JSON.stringify({
                water: water,
                light: light,
                temperature: temperature,
            }),
        });

        if (!response.ok) {
            throw new Error("Spring API request failed: " + response.status);
        }

        const result = await response.json();

        renderResult(result);
        appendHistory(result);

    } catch (error) {
        console.error(error);
        alert("Spring Boot API 연결 실패. 서버가 켜져 있는지 확인해줘.");
    }
}

function simulateTick(water, light, temperature) {
    tick += 1;

    const env = {
        Water: water,
        Light: light,
        Temperature: temperature,
    };

    const activeStates = evaluateRules(env);

    let totalEnergy = 100;

    totalEnergy += light * 0.35;
    totalEnergy -= Math.max(0, 30 - water) * 1.4;
    totalEnergy -= Math.max(0, temperature - 32) * 2.2;

    if (activeStates.includes("RecoveryMode")) {
        totalEnergy += 25;
    }

    if (activeStates.includes("HeatStress")) {
        totalEnergy -= 20;
    }

    if (activeStates.includes("DroughtMode")) {
        totalEnergy -= 30;
    }

    totalEnergy = Math.max(0, Math.min(160, totalEnergy));

    let lastAction = "Stable";

    if (activeStates.includes("PruningMode")) {
        lastAction = tick % 2 === 0 ? "PruningAlreadyExecuted" : "Pruning";
    } else if (totalEnergy < 25) {
        lastAction = "Pruning";
    } else {
        lastAction = "Stable";
    }

    const visual = chooseVisualState(activeStates, lastAction, totalEnergy);

    return {
        tick,
        water,
        light,
        temperature,
        totalEnergy,
        lastAction,
        activeStates,
        visual,
    };
}

function evaluateRules(env) {
    const activeStates = [];

    for (const rule of rules) {
        const value = env[rule.field];

        if (evaluateCondition(value, rule.operator, rule.threshold)) {
            activeStates.push(rule.state);
        }
    }

    return [...new Set(activeStates)];
}

function evaluateCondition(value, operator, threshold) {
    switch (operator) {
        case "<":
            return value < threshold;
        case ">":
            return value > threshold;
        case "<=":
            return value <= threshold;
        case ">=":
            return value >= threshold;
        default:
            return false;
    }
}

function chooseVisualState(activeStates, lastAction, totalEnergy) {
    if (totalEnergy <= 5) {
        return "dead_critical";
    }

    if (activeStates.includes("RecoveryMode")) {
        return "recovery_mode";
    }

    if (activeStates.includes("HeatStress")) {
        return "heat_stress";
    }

    if (activeStates.includes("DroughtMode")) {
        return "drought_mode";
    }

    if (lastAction === "PruningAlreadyExecuted") {
        return "pruning_already_executed";
    }

    if (lastAction === "Pruning" || lastAction === "PruningFailed") {
        return "pruned";
    }

    if (activeStates.includes("PhotosynthesisBoost")) {
        return "photosynthesis_boost";
    }

    if (activeStates.includes("ColdStress")) {
        return "cold_stress";
    }

    if (totalEnergy < 40) {
        return "low_energy";
    }

    return "stable";
}

function renderResult(result) {
    const visualKey = result.visualState || result.visual || "stable";
    const imagePath = plantImages[visualKey] || plantImages.stable;

    plantImage.src = imagePath;
    visualState.textContent = `visual: ${visualKey}`;

    tickValue.textContent = result.tick;
    lastActionValue.textContent = result.lastAction;
    energyValue.textContent = result.totalEnergy.toFixed(1);
    visualStateValue.textContent = visualKey;

    if (result.activeStates.length === 0) {
        activeStatesBox.textContent = "No active states.";
    } else {
        activeStatesBox.textContent = result.activeStates
            .map((state) => `- ${state}: ON`)
            .join("\n");
    }

    recommendationBox.textContent = makeRecommendation(result);
}

function appendHistory(result) {
    const row = document.createElement("tr");

    row.innerHTML = `
        <td>${result.tick}</td>
        <td>${result.water.toFixed(1)}</td>
        <td>${result.light.toFixed(1)}</td>
        <td>${result.temperature.toFixed(1)}</td>
        <td>${result.lastAction}</td>
        <td>${result.visualState || result.visual || "stable"}</td>
    `;

    historyTable.prepend(row);
}

async function loadSimulationLogs() {
    try {
        const response = await fetch("http://localhost:8080/api/simulations/logs");

        if (!response.ok) {
            throw new Error("Failed to load simulation logs: " + response.status);
        }

        const logs = await response.json();

        historyTable.innerHTML = "";

        logs.forEach((log) => {
            appendHistoryFromLog(log);
        });

        if (logs.length > 0) {
            renderResult(logs[0]);
        }

    } catch (error) {
        console.error(error);
        alert("DB 로그 조회 실패. Spring Boot 서버가 켜져 있는지 확인해주세요.");
    }
}

function appendHistoryFromLog(log) {
    const row = document.createElement("tr");

    const visualKey = log.visualState || log.visual || "stable";

    row.innerHTML = `
        <td>${log.tick}</td>
        <td>${Number(log.water).toFixed(1)}</td>
        <td>${Number(log.light).toFixed(1)}</td>
        <td>${Number(log.temperature).toFixed(1)}</td>
        <td>${log.lastAction}</td>
        <td>${visualKey}</td>
    `;

    historyTable.appendChild(row);
}

function makeRecommendation(result) {
    if (result.tick === 0) {
        return "Press Run Simulation to start BIO-OS analysis.";
    }

    if (result.visual === "dead_critical") {
        return [
            "Predicted Risk: Critical Survival Failure",
            "Reason: Energy level is extremely low.",
            "Suggestion: Increase water input and reduce temperature immediately.",
        ].join("\n");
    }

    if (result.activeStates.includes("RecoveryMode")) {
        return [
            "Predicted Risk: Recovery Mode",
            "Reason: Water input is high enough to trigger recovery.",
            "Suggestion: Maintain moderate temperature and stable light exposure.",
        ].join("\n");
    }

    if (result.activeStates.includes("HeatStress")) {
        return [
            "Predicted Risk: Heat Stress",
            "Reason: Temperature is above the configured threshold.",
            "Suggestion: Lower temperature below 35.",
        ].join("\n");
    }

    if (result.activeStates.includes("DroughtMode")) {
        return [
            "Predicted Risk: Drought Stress",
            "Reason: Water input is below the configured threshold.",
            "Suggestion: Increase water input gradually.",
        ].join("\n");
    }

    if (result.lastAction.includes("Pruning")) {
        return [
            "Predicted Risk: Pruning Risk",
            "Reason: The plant system selected pruning as a survival action.",
            "Suggestion: Stabilize water and light conditions.",
        ].join("\n");
    }

    return [
        "Predicted Risk: Stable / Growth Friendly",
        "Reason: Current environment is within a stable range.",
        "Suggestion: Maintain current environment.",
    ].join("\n");
}

function renderRules() {
    ruleList.innerHTML = "";

    for (const rule of rules) {
        const item = document.createElement("li");
        item.textContent = `IF ${rule.field} ${rule.operator} ${rule.threshold} THEN ${rule.state} = ON`;
        ruleList.appendChild(item);
    }
}

function randomRange(min, max) {
    return min + (max - min) * Math.random();
}

renderRules();

renderResult({
    tick: 0,
    water: 0,
    light: 0,
    temperature: 0,
    totalEnergy: 100,
    lastAction: "None",
    activeStates: [],
    visual: "stable",
});