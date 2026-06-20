let currentGrowthTimeline = [];
let currentGrowthDayIndex = 0;
let timelineTimer = null;

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
const humidityInput = document.getElementById("humidityInput");

const runButton = document.getElementById("runButton");
const randomButton = document.getElementById("randomButton");
const loadLogsButton = document.getElementById("loadLogsButton");
const resetButton = document.getElementById("resetButton");

const plantTypeSelect = document.getElementById("plantTypeSelect");
const growthDaysInput = document.getElementById("growthDaysInput");
const runGrowthButton = document.getElementById("runGrowthButton");

const addRuleButton = document.getElementById("addRuleButton");
const ruleList = document.getElementById("ruleList");

const plantImage = document.getElementById("plantImage");
const visualState = document.getElementById("visualState");

const tickValue = document.getElementById("tickValue");
const waterValue = document.getElementById("waterValue");
const lightValue = document.getElementById("lightValue");
const temperatureValue = document.getElementById("temperatureValue");
const humidityValue = document.getElementById("humidityValue");

const lastActionValue = document.getElementById("lastActionValue");
const energyValue = document.getElementById("energyValue");
const energyDeltaValue = document.getElementById("energyDeltaValue");
const riskLevelValue = document.getElementById("riskLevelValue");
const visualStateValue = document.getElementById("visualStateValue");

const activeStatesBox = document.getElementById("activeStatesBox");
const matchedRulesBox = document.getElementById("matchedRulesBox");
const recommendationBox = document.getElementById("recommendationBox");
const historyTable = document.getElementById("historyTable");

const growthPlantTypeValue = document.getElementById("growthPlantTypeValue");
const growthDaysValue = document.getElementById("growthDaysValue");
const growthScoreValue = document.getElementById("growthScoreValue");
const growthRiskValue = document.getElementById("growthRiskValue");
const growthVisualValue = document.getElementById("growthVisualValue");
const growthCurrentDayValue = document.getElementById("growthCurrentDayValue");
const growthSummaryBox = document.getElementById("growthSummaryBox");
const growthTimelineTable = document.getElementById("growthTimelineTable");

const playTimelineButton = document.getElementById("playTimelineButton");
const pauseTimelineButton = document.getElementById("pauseTimelineButton");
const resetTimelineButton = document.getElementById("resetTimelineButton");

runButton.addEventListener("click", () => {
    runSimulationFromInput();
});

randomButton.addEventListener("click", () => {
    waterInput.value = randomRange(0, 160).toFixed(1);
    lightInput.value = randomRange(0, 100).toFixed(1);
    temperatureInput.value = randomRange(5, 45).toFixed(1);
    humidityInput.value = randomRange(20, 90).toFixed(1);

    runSimulationFromInput();
});

resetButton.addEventListener("click", () => {
    clearSimulationLogs();
});

loadLogsButton.addEventListener("click", () => {
    loadSimulationLogs();
});

addRuleButton.addEventListener("click", () => {
    createGeneRule();
});

runGrowthButton.addEventListener("click", () => {
    runGrowthSimulation();
});

playTimelineButton.addEventListener("click", () => {
    playGrowthTimeline();
});

pauseTimelineButton.addEventListener("click", () => {
    pauseGrowthTimeline();
});

resetTimelineButton.addEventListener("click", () => {
    resetGrowthTimeline();
});

async function clearSimulationLogs() {
    const confirmed = confirm("저장된 Simulation Log를 모두 삭제하시겠습니까?");

    if (!confirmed) {
        return;
    }

    try {
        const response = await fetch("http://localhost:8080/api/simulations/logs", {
            method: "DELETE",
        });

        if (!response.ok) {
            throw new Error("Failed to clear simulation logs: " + response.status);
        }

        historyTable.innerHTML = "";

        renderResult({
            tick: 0,
            water: 0,
            light: 0,
            temperature: 0,
            humidity: 60,
            totalEnergy: 100,
            lastAction: "None",
            activeStates: [],
            visualState: "stable",
            energyDelta: 0,
            matchedRules: [],
            riskLevel: "LOW",
            recommendation: "Press Run Simulation to start BIO-OS analysis.",
        });

        alert("Simulation Log가 삭제되었습니다.");

    } catch (error) {
        console.error(error);
        alert("Simulation Log 삭제에 실패했습니다. Spring Boot 서버가 실행 중인지 확인해 주세요.");
    }
}

async function runSimulationFromInput() {
    const water = Number(waterInput.value);
    const light = Number(lightInput.value);
    const temperature = Number(temperatureInput.value);
    const humidity = Number(humidityInput.value);

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
                humidity: humidity,
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
        alert("Spring Boot API 연결에 실패했습니다. 서버가 켜져 있는지 확인해 주세요.");
    }
}

function renderResult(result) {
    const visualKey = result.visualState || result.visual || "stable";
    const imagePath = plantImages[visualKey] || plantImages.stable;

    const activeStates = result.activeStates || [];
    const matchedRules = result.matchedRules || [];

    plantImage.src = imagePath;
    visualState.textContent = `Current Visual State: ${visualKey}`;

    tickValue.textContent = result.tick ?? 0;

    waterValue.textContent = Number(result.water || 0).toFixed(1);
    lightValue.textContent = Number(result.light || 0).toFixed(1);
    temperatureValue.textContent = Number(result.temperature || 0).toFixed(1);
    humidityValue.textContent = Number(result.humidity || 0).toFixed(1);

    lastActionValue.textContent = result.lastAction || "None";
    energyValue.textContent = Number(result.totalEnergy || 0).toFixed(1);
    energyDeltaValue.textContent = formatSignedNumber(result.energyDelta || 0);
    visualStateValue.textContent = visualKey;

    const riskLevel = result.riskLevel || "LOW";
    riskLevelValue.textContent = riskLevel;
    riskLevelValue.className = `risk-badge ${getRiskClass(riskLevel)}`;

    if (activeStates.length === 0) {
        activeStatesBox.textContent = "No active states.";
    } else {
        activeStatesBox.textContent = activeStates
            .map((state) => `- ${state}: ON`)
            .join("\n");
    }

    if (matchedRules.length === 0) {
        matchedRulesBox.textContent = "No matched rules.";
    } else {
        matchedRulesBox.textContent = matchedRules
            .map((rule) => `• ${rule}`)
            .join("\n");
    }

    recommendationBox.textContent =
        result.recommendation || makeRecommendation(result);
}

function appendHistory(result) {
    const row = document.createElement("tr");

    const visualKey = result.visualState || result.visual || "stable";

    row.innerHTML = `
        <td>${result.tick ?? 0}</td>
        <td>${Number(result.water || 0).toFixed(1)}</td>
        <td>${Number(result.light || 0).toFixed(1)}</td>
        <td>${Number(result.temperature || 0).toFixed(1)}</td>
        <td>${Number(result.humidity || 0).toFixed(1)}</td>
        <td>${Number(result.totalEnergy || 0).toFixed(1)}</td>
        <td>${result.lastAction || "None"}</td>
        <td>${visualKey}</td>
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
        alert("DB 로그 조회에 실패했습니다. Spring Boot 서버가 켜져 있는지 확인해 주세요.");
    }
}

function appendHistoryFromLog(log) {
    const row = document.createElement("tr");

    const visualKey = log.visualState || log.visual || "stable";

    row.innerHTML = `
        <td>${log.tick ?? 0}</td>
        <td>${Number(log.water || 0).toFixed(1)}</td>
        <td>${Number(log.light || 0).toFixed(1)}</td>
        <td>${Number(log.temperature || 0).toFixed(1)}</td>
        <td>${Number(log.humidity || 0).toFixed(1)}</td>
        <td>${Number(log.totalEnergy || 0).toFixed(1)}</td>
        <td>${log.lastAction || "None"}</td>
        <td>${visualKey}</td>
    `;

    historyTable.appendChild(row);
}

async function createGeneRule() {
    const fieldName = document.getElementById("ruleField").value;
    const operator = document.getElementById("ruleOperator").value;
    const threshold = Number(document.getElementById("ruleThreshold").value);
    const targetState = document.getElementById("ruleState").value.trim();
    const energyEffect = Number(document.getElementById("ruleEnergyEffect").value);

    if (!targetState) {
        alert("State name을 입력해주세요.");
        return;
    }

    try {
        const response = await fetch("http://localhost:8080/api/rules", {
            method: "POST",
            headers: {
                "Content-Type": "application/json",
            },
            body: JSON.stringify({
                fieldName: fieldName,
                operator: operator,
                threshold: threshold,
                targetState: targetState,
                energyEffect: energyEffect,
            }),
        });

        if (!response.ok) {
            throw new Error("Failed to create gene rule: " + response.status);
        }

        await loadGeneRules();

    } catch (error) {
        console.error(error);
        alert("Gene Rule 저장에 실패했습니다. Spring Boot 서버가 켜져 있는지 확인해 주세요.");
    }
}

async function loadGeneRules() {
    try {
        const response = await fetch("http://localhost:8080/api/rules");

        if (!response.ok) {
            throw new Error("Failed to load gene rules: " + response.status);
        }

        const serverRules = await response.json();

        ruleList.innerHTML = "";

        if (!serverRules || serverRules.length === 0) {
            const emptyItem = document.createElement("li");
            emptyItem.textContent = "No gene rules registered.";
            ruleList.appendChild(emptyItem);
            return;
        }

        serverRules.forEach((rule) => {
            const item = document.createElement("li");
            item.className = "rule-item";

            const ruleText = document.createElement("span");
            ruleText.className = "rule-text";

            const status = rule.active ? "ON" : "OFF";
            const displayOperator = formatOperator(rule.operator);
            const energyEffect = formatSignedNumber(rule.energyEffect || 0);

            ruleText.textContent =
                `IF ${rule.fieldName} ${displayOperator} ${rule.threshold} THEN ${rule.targetState} = ${status} / Effect ${energyEffect}`;

            if (!rule.active) {
                ruleText.classList.add("inactive-rule");
            }

            const actions = document.createElement("div");
            actions.className = "rule-actions";

            const toggleButton = document.createElement("button");
            toggleButton.className = "mini-button toggle-button";
            toggleButton.textContent = rule.active ? "Disable" : "Enable";
            toggleButton.addEventListener("click", () => {
                toggleGeneRule(rule.id);
            });

            const deleteButton = document.createElement("button");
            deleteButton.className = "mini-button delete-button";
            deleteButton.textContent = "Delete";
            deleteButton.addEventListener("click", () => {
                deleteGeneRule(rule.id);
            });

            actions.appendChild(toggleButton);
            actions.appendChild(deleteButton);

            item.appendChild(ruleText);
            item.appendChild(actions);

            ruleList.appendChild(item);
        });

    } catch (error) {
        console.error(error);
        alert("Gene Rule 조회에 실패했습니다. Spring Boot 서버가 실행 중인지 확인해 주세요.");
    }
}

async function toggleGeneRule(ruleId) {
    try {
        const response = await fetch(`http://localhost:8080/api/rules/${ruleId}/toggle`, {
            method: "PATCH",
        });

        if (!response.ok) {
            throw new Error("Failed to toggle gene rule: " + response.status);
        }

        await loadGeneRules();

    } catch (error) {
        console.error(error);
        alert("Gene Rule 활성/비활성 전환에 실패했습니다. Spring Boot 서버가 실행 중인지 확인해 주세요.");
    }
}

async function deleteGeneRule(ruleId) {
    const confirmed = confirm("이 Gene Rule을 삭제하시겠습니까?");

    if (!confirmed) {
        return;
    }

    try {
        const response = await fetch(`http://localhost:8080/api/rules/${ruleId}`, {
            method: "DELETE",
        });

        if (!response.ok) {
            throw new Error("Failed to delete gene rule: " + response.status);
        }

        await loadGeneRules();

    } catch (error) {
        console.error(error);
        alert("Gene Rule 삭제에 실패했습니다.");
    }
}

async function loadPlantTypes() {
    try {
        const response = await fetch("http://localhost:8080/api/plants");

        if (!response.ok) {
            throw new Error("Failed to load plant types: " + response.status);
        }

        const plantTypes = await response.json();

        plantTypeSelect.innerHTML = "";

        if (!plantTypes || plantTypes.length === 0) {
            const option = document.createElement("option");
            option.value = "";
            option.textContent = "No plant types registered";
            plantTypeSelect.appendChild(option);
            return;
        }

        plantTypes.forEach((plantType) => {
            const option = document.createElement("option");
            option.value = plantType.id;
            option.textContent = `${plantType.name} / Water ${plantType.optimalWaterMin}-${plantType.optimalWaterMax}`;
            plantTypeSelect.appendChild(option);
        });

    } catch (error) {
        console.error(error);
        alert("Plant Type 조회에 실패했습니다. Spring Boot 서버가 실행 중인지 확인해 주세요.");
    }
}

async function runGrowthSimulation() {
    const plantTypeId = Number(plantTypeSelect.value);

    if (!plantTypeId) {
        alert("Plant Type을 선택해 주세요.");
        return;
    }

    const days = Number(growthDaysInput.value || 30);

    try {
        const response = await fetch("http://localhost:8080/api/growth/simulate", {
            method: "POST",
            headers: {
                "Content-Type": "application/json",
            },
            body: JSON.stringify({
                plantTypeId: plantTypeId,
                water: Number(waterInput.value),
                light: Number(lightInput.value),
                temperature: Number(temperatureInput.value),
                humidity: Number(humidityInput.value),
                days: days,
            }),
        });

        if (!response.ok) {
            throw new Error("Growth simulation request failed: " + response.status);
        }

        const result = await response.json();

        renderGrowthResult(result);

    } catch (error) {
        console.error(error);
        alert("Growth Simulation 실행에 실패했습니다. Spring Boot 서버가 켜져 있는지 확인해 주세요.");
    }
}

function renderGrowthResult(result) {
    pauseGrowthTimeline();

    currentGrowthTimeline = result.timeline || [];
    currentGrowthDayIndex = 0;

    growthPlantTypeValue.textContent = result.plantType || "-";
    growthDaysValue.textContent = result.days ?? currentGrowthTimeline.length;
    growthScoreValue.textContent = Number(result.finalGrowthScore || 0).toFixed(1);

    const finalRiskLevel = result.finalRiskLevel || "LOW";
    growthRiskValue.textContent = finalRiskLevel;
    growthRiskValue.className = `risk-badge ${getRiskClass(finalRiskLevel)}`;

    growthVisualValue.textContent = result.finalVisualState || "stable";
    growthSummaryBox.textContent = result.summary || "No summary.";

    renderGrowthTimelineTable(currentGrowthTimeline);

    if (currentGrowthTimeline.length > 0) {
        renderGrowthDay(0);
    }
}

function renderGrowthTimelineTable(timeline) {
    growthTimelineTable.innerHTML = "";

    if (!timeline || timeline.length === 0) {
        const row = document.createElement("tr");
        row.innerHTML = `
            <td colspan="5">No growth timeline.</td>
        `;
        growthTimelineTable.appendChild(row);
        return;
    }

    timeline.forEach((dayData, index) => {
        const row = document.createElement("tr");
        row.dataset.index = String(index);

        row.innerHTML = `
            <td>${dayData.day}</td>
            <td>${Number(dayData.growthScore || 0).toFixed(1)}</td>
            <td>${Number(dayData.totalEnergy || 0).toFixed(1)}</td>
            <td>${dayData.riskLevel || "LOW"}</td>
            <td>${dayData.visualState || "stable"}</td>
        `;

        row.addEventListener("click", () => {
            pauseGrowthTimeline();
            renderGrowthDay(index);
        });

        growthTimelineTable.appendChild(row);
    });
}

function renderGrowthDay(index) {
    if (!currentGrowthTimeline || currentGrowthTimeline.length === 0) {
        return;
    }

    if (index < 0) {
        index = 0;
    }

    if (index >= currentGrowthTimeline.length) {
        index = currentGrowthTimeline.length - 1;
    }

    currentGrowthDayIndex = index;

    const dayData = currentGrowthTimeline[index];
    const visualKey = dayData.visualState || "stable";
    const imagePath = plantImages[visualKey] || plantImages.stable;

    plantImage.src = imagePath;
    visualState.textContent = `Growth Timeline Day ${dayData.day}: ${visualKey}`;

    growthCurrentDayValue.textContent = `Day ${dayData.day}`;
    growthScoreValue.textContent = Number(dayData.growthScore || 0).toFixed(1);

    const riskLevel = dayData.riskLevel || "LOW";
    growthRiskValue.textContent = riskLevel;
    growthRiskValue.className = `risk-badge ${getRiskClass(riskLevel)}`;

    growthVisualValue.textContent = visualKey;

    highlightGrowthTimelineRow(index);
}

function highlightGrowthTimelineRow(index) {
    const rows = growthTimelineTable.querySelectorAll("tr");

    rows.forEach((row) => {
        row.classList.remove("active-timeline-row");
    });

    const activeRow = growthTimelineTable.querySelector(`tr[data-index="${index}"]`);

    if (activeRow) {
        activeRow.classList.add("active-timeline-row");
        activeRow.scrollIntoView({
            behavior: "smooth",
            block: "nearest",
        });
    }
}

function playGrowthTimeline() {
    if (!currentGrowthTimeline || currentGrowthTimeline.length === 0) {
        alert("먼저 Growth Simulation을 실행해 주세요.");
        return;
    }

    pauseGrowthTimeline();

    timelineTimer = setInterval(() => {
        renderGrowthDay(currentGrowthDayIndex);

        currentGrowthDayIndex += 1;

        if (currentGrowthDayIndex >= currentGrowthTimeline.length) {
            pauseGrowthTimeline();
        }
    }, 700);
}

function pauseGrowthTimeline() {
    if (timelineTimer) {
        clearInterval(timelineTimer);
        timelineTimer = null;
    }
}

function resetGrowthTimeline() {
    pauseGrowthTimeline();

    if (!currentGrowthTimeline || currentGrowthTimeline.length === 0) {
        return;
    }

    renderGrowthDay(0);
}

function makeRecommendation(result) {
    const activeStates = result.activeStates || [];
    const visualKey = result.visualState || result.visual || "stable";

    if (result.tick === 0) {
        return "Press Run Simulation to start BIO-OS analysis.";
    }

    if (visualKey === "dead_critical") {
        return [
            "Predicted Risk: Critical Survival Failure",
            "Reason: Energy level is extremely low.",
            "Suggestion: Increase water input and reduce temperature immediately.",
        ].join("\n");
    }

    if (activeStates.includes("RecoveryMode")) {
        return [
            "Predicted Risk: Recovery Mode",
            "Reason: Water input is high enough to trigger recovery.",
            "Suggestion: Maintain moderate temperature and stable light exposure.",
        ].join("\n");
    }

    if (activeStates.includes("HeatStress")) {
        return [
            "Predicted Risk: Heat Stress",
            "Reason: Temperature is above the configured threshold.",
            "Suggestion: Lower temperature below 35.",
        ].join("\n");
    }

    if (activeStates.includes("DroughtMode")) {
        return [
            "Predicted Risk: Drought Stress",
            "Reason: Water input is below the configured threshold.",
            "Suggestion: Increase water input gradually.",
        ].join("\n");
    }

    if ((result.lastAction || "").includes("Pruning")) {
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

function randomRange(min, max) {
    return min + (max - min) * Math.random();
}

function formatOperator(operator) {
    switch (operator) {
        case "LT":
            return "<";
        case "GT":
            return ">";
        case "LTE":
            return "<=";
        case "GTE":
            return ">=";
        case "EQ":
            return "=";
        default:
            return operator;
    }
}

function formatSignedNumber(value) {
    const numberValue = Number(value || 0);

    if (numberValue > 0) {
        return `+${numberValue.toFixed(1)}`;
    }

    return numberValue.toFixed(1);
}

function getRiskClass(riskLevel) {
    switch (riskLevel) {
        case "CRITICAL":
            return "risk-critical";
        case "HIGH":
            return "risk-high";
        case "MEDIUM":
            return "risk-medium";
        case "LOW":
        default:
            return "risk-low";
    }
}

loadPlantTypes();
loadGeneRules();

renderResult({
    tick: 0,
    water: 0,
    light: 0,
    temperature: 0,
    humidity: 60,
    totalEnergy: 100,
    lastAction: "None",
    activeStates: [],
    visualState: "stable",
    energyDelta: 0,
    matchedRules: [],
    riskLevel: "LOW",
    recommendation: "Press Run Simulation to start BIO-OS analysis.",
});