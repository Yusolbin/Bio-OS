let currentGrowthTimeline = [];
let currentGrowthDayIndex = 0;
let currentGrowthSimulationId = null;
let timelineTimer = null;
let currentUser = loadCurrentUser();

if (!currentUser) {
    window.location.href = "auth.html";
}

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
const loadGrowthSimulationsButton = document.getElementById("loadGrowthSimulationsButton");

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
const growthHistoryTable = document.getElementById("growthHistoryTable");

const growthChartCanvas = document.getElementById("growthChartCanvas");
const growthChartContext = growthChartCanvas ? growthChartCanvas.getContext("2d") : null;

const playTimelineButton = document.getElementById("playTimelineButton");
const pauseTimelineButton = document.getElementById("pauseTimelineButton");
const resetTimelineButton = document.getElementById("resetTimelineButton");
const exportGrowthCsvButton = document.getElementById("exportGrowthCsvButton");

const loadAdminSummaryButton = document.getElementById("loadAdminSummaryButton");

const adminPlantTypeCount = document.getElementById("adminPlantTypeCount");
const adminGeneRuleCount = document.getElementById("adminGeneRuleCount");
const adminSimulationLogCount = document.getElementById("adminSimulationLogCount");
const adminGrowthSimulationCount = document.getElementById("adminGrowthSimulationCount");
const adminUserCount = document.getElementById("adminUserCount");
const adminAverageGrowthScore = document.getElementById("adminAverageGrowthScore");

const adminAverageWater = document.getElementById("adminAverageWater");
const adminAverageLight = document.getElementById("adminAverageLight");
const adminAverageTemperature = document.getElementById("adminAverageTemperature");
const adminAverageHumidity = document.getElementById("adminAverageHumidity");

const adminLatestPlantType = document.getElementById("adminLatestPlantType");
const adminLatestRiskLevel = document.getElementById("adminLatestRiskLevel");
const adminLatestVisualState = document.getElementById("adminLatestVisualState");
const adminRiskDistributionBox = document.getElementById("adminRiskDistributionBox");

const adminRiskChartCanvas = document.getElementById("adminRiskChartCanvas");
const adminRiskChartContext = adminRiskChartCanvas ? adminRiskChartCanvas.getContext("2d") : null;

const adminEnvironmentChartCanvas = document.getElementById("adminEnvironmentChartCanvas");
const adminEnvironmentChartContext = adminEnvironmentChartCanvas
    ? adminEnvironmentChartCanvas.getContext("2d")
    : null;

const adminInsightList = document.getElementById("adminInsightList");
const adminOverallStatus = document.getElementById("adminOverallStatus");

const logoutButton = document.getElementById("logoutButton");
const authGuardMessage = document.getElementById("authGuardMessage");
const adminDashboardCard = document.getElementById("adminDashboardCard");

const engineSourceText = document.getElementById("engineSourceText");

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

loadGrowthSimulationsButton.addEventListener("click", () => {
    loadGrowthSimulations();
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

exportGrowthCsvButton.addEventListener("click", () => {
    exportCurrentGrowthSimulationCsv();
});

loadAdminSummaryButton.addEventListener("click", () => {
    loadAdminSummary();
});

logoutButton.addEventListener("click", () => {
    logoutUser();
});

async function clearSimulationLogs() {
    if (!currentUser) {
        alert("Simulation Log 삭제는 로그인 후 사용할 수 있습니다.");
        showDashboardMessage("Login required to clear simulation logs.");
        return;
    }

    const confirmed = confirm("저장된 Simulation Log를 모두 삭제하시겠습니까?");

    if (!confirmed) {
        return;
    }

    try {
        const response = await fetch(
            "http://localhost:8080/api/simulations/logs",
            {
                method: "DELETE",
                headers: buildAuthHeaders(),
            }
        );

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
    if (!currentUser) {
        alert("Run Simulation은 로그인 후 실행할 수 있습니다.");
        showDashboardMessage("Login required for Run Simulation.");
        return;
    }

    const water = Number(waterInput.value);
    const light = Number(lightInput.value);
    const temperature = Number(temperatureInput.value);
    const humidity = Number(humidityInput.value);

    try {
        const response = await fetch("http://localhost:8080/api/simulations/run", {
            method: "POST",
            headers: buildJsonHeaders(),
            body: JSON.stringify({
                water: water,
                light: light,
                temperature: temperature,
                humidity: humidity,
            }),
        });

        if (handleAuthError(response)) {
            return;
        }

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

    if (engineSourceText) {
        engineSourceText.textContent = result.engineSource || "-";
    }

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
    if (!currentUser) {
        alert("Simulation Logs는 로그인 후 조회할 수 있습니다.");
        showDashboardMessage("Login required to load simulation logs.");
        return;
    }

    try {
        const response = await fetch(
            "http://localhost:8080/api/simulations/logs",
            {
                headers: buildAuthHeaders(),
            }
        );

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
    if (!currentUser || currentUser.role !== "ADMIN") {
        alert("Gene Rule 관리는 ADMIN 계정만 사용할 수 있습니다.");
        showDashboardMessage("ADMIN role required for Gene Rule management.");
        return;
    }

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
            headers: buildJsonHeaders(),
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
    if (!isAdminUser()) {
        if (ruleList) {
            ruleList.innerHTML = "";

            const lockedItem = document.createElement("li");
            lockedItem.textContent = "ADMIN only: Gene Rule list is locked.";
            ruleList.appendChild(lockedItem);
        }

        return;
    }

    try {
        const response = await fetch("http://localhost:8080/api/rules", {
            headers: buildAuthHeaders(),
        });

        if (response.status === 403) {
            if (ruleList) {
                ruleList.innerHTML = "";

                const lockedItem = document.createElement("li");
                lockedItem.textContent = "ADMIN only: Gene Rule list is locked.";
                ruleList.appendChild(lockedItem);
            }

            return;
        }

        if (response.status === 401) {
            alert("로그인 세션이 만료되었습니다. 다시 로그인해 주세요.");
            logoutUser();
            return;
        }

        if (handleForbiddenError(response, "Gene Rule 관리는 ADMIN 계정만 사용할 수 있습니다.")) {
            return;
        }

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
            toggleButton.disabled = !isAdminUser();
            toggleButton.classList.toggle("disabled-button", !isAdminUser());
            toggleButton.addEventListener("click", () => {
                toggleGeneRule(rule.id);
            });

            const deleteButton = document.createElement("button");
            deleteButton.className = "mini-button delete-button";
            deleteButton.textContent = "Delete";
            deleteButton.disabled = !isAdminUser();
            deleteButton.classList.toggle("disabled-button", !isAdminUser());
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
    if (!isAdminUser()) {
        alert("Gene Rule 관리는 ADMIN 계정만 사용할 수 있습니다.");
        showDashboardMessage("ADMIN role required for Gene Rule management.");
        return;
    }

    try {
        const response = await fetch(`http://localhost:8080/api/rules/${ruleId}/toggle`, {
            method: "PATCH",
            headers: buildAuthHeaders(),
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
    if (!isAdminUser()) {
        alert("Gene Rule 관리는 ADMIN 계정만 사용할 수 있습니다.");
        showDashboardMessage("ADMIN role required for Gene Rule management.");
        return;
    }

    const confirmed = confirm("이 Gene Rule을 삭제하시겠습니까?");

    if (!confirmed) {
        return;
    }

    try {
        const response = await fetch(`http://localhost:8080/api/rules/${ruleId}`, {
            method: "DELETE",
            headers: buildAuthHeaders(),
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
        const response = await fetch("http://localhost:8080/api/plants", {
            headers: buildAuthHeaders(),
        });

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
    if (!currentUser) {
        alert("Growth Simulation은 로그인 후 실행할 수 있습니다.");
        showDashboardMessage("Login required for Growth Simulation.");
        return;
    }

    const plantTypeId = Number(plantTypeSelect.value);

    if (!plantTypeId) {
        alert("Plant Type을 선택해 주세요.");
        return;
    }

    const days = Number(growthDaysInput.value || 30);

    try {
        const response = await fetch("http://localhost:8080/api/growth/simulate", {
            method: "POST",
            headers: buildJsonHeaders(),
            body: JSON.stringify({
                plantTypeId: plantTypeId,
                water: Number(waterInput.value),
                light: Number(lightInput.value),
                temperature: Number(temperatureInput.value),
                humidity: Number(humidityInput.value),
                days: days,
            }),
        });

        if (handleAuthError(response)) {
            return;
        }

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

async function loadGrowthSimulations() {
    if (!currentUser) {
        alert("Saved Growth Simulations는 로그인 후 확인할 수 있습니다.");
        showDashboardMessage("Login required to load saved growth simulations.");
        return;
    }

    try {
        const response = await fetch(
            "http://localhost:8080/api/growth/simulations",
            {
                headers: buildAuthHeaders(),
            }
        );

        if (!response.ok) {
            throw new Error("Failed to load growth simulations: " + response.status);
        }

        const simulations = await response.json();

        renderGrowthSimulationHistory(simulations);

    } catch (error) {
        console.error(error);
        alert("Growth Simulation 기록 조회에 실패했습니다. Spring Boot 서버가 켜져 있는지 확인해 주세요.");
    }
}

function renderGrowthSimulationHistory(simulations) {
    growthHistoryTable.innerHTML = "";

    if (!simulations || simulations.length === 0) {
        const row = document.createElement("tr");

        row.innerHTML = `
            <td colspan="6">No saved growth simulations.</td>
        `;

        growthHistoryTable.appendChild(row);
        return;
    }

    simulations.forEach((simulation) => {
        const row = document.createElement("tr");
        row.className = "clickable-row";

        row.innerHTML = `
            <td>${simulation.simulationId}</td>
            <td>${simulation.plantType}</td>
            <td>${simulation.days}</td>
            <td>${Number(simulation.finalGrowthScore || 0).toFixed(1)}</td>
            <td>${simulation.finalRiskLevel || "LOW"}</td>
            <td>${simulation.finalVisualState || "stable"}</td>
        `;

        row.addEventListener("click", () => {
            loadGrowthSimulationDetail(simulation.simulationId);
        });

        growthHistoryTable.appendChild(row);
    });
}

async function loadGrowthSimulationDetail(simulationId) {
    try {
        currentGrowthSimulationId = simulationId;

        const response = await fetch(`http://localhost:8080/api/growth/simulations/${simulationId}`, {
            headers: buildAuthHeaders(),
        });

        if (!response.ok) {
            throw new Error("Failed to load growth simulation detail: " + response.status);
        }

        const detail = await response.json();

        renderGrowthResult(detail);
        drawGrowthChart(detail.timeline || []);

    } catch (error) {
        console.error(error);
        alert("Growth Simulation 상세 조회에 실패했습니다.");
    }
}

async function exportCurrentGrowthSimulationCsv() {
    if (!currentGrowthSimulationId) {
        alert("먼저 Saved Growth Simulation을 선택해 주세요.");
        return;
    }

    try {
        const response = await fetch(
            `http://localhost:8080/api/growth/simulations/${currentGrowthSimulationId}/csv`,
            {
                headers: buildAuthHeaders(),
            }
        );

        if (!response.ok) {
            throw new Error("Failed to export growth simulation CSV: " + response.status);
        }

        const blob = await response.blob();
        const url = window.URL.createObjectURL(blob);

        const link = document.createElement("a");
        link.href = url;
        link.download = `growth_simulation_${currentGrowthSimulationId}.csv`;

        document.body.appendChild(link);
        link.click();
        link.remove();

        window.URL.revokeObjectURL(url);

    } catch (error) {
        console.error(error);
        alert("CSV Export에 실패했습니다.");
    }
}

function renderGrowthResult(result) {
    pauseGrowthTimeline();

    currentGrowthTimeline = result.timeline || [];
    currentGrowthDayIndex = 0;
    currentGrowthSimulationId = result.simulationId || result.id || currentGrowthSimulationId;

    if (exportGrowthCsvButton) {
        exportGrowthCsvButton.disabled = !currentGrowthSimulationId;
    }

    growthPlantTypeValue.textContent = result.plantType || "-";
    growthDaysValue.textContent = result.days ?? currentGrowthTimeline.length;
    growthScoreValue.textContent = Number(result.finalGrowthScore || 0).toFixed(1);

    const finalRiskLevel = result.finalRiskLevel || "LOW";
    growthRiskValue.textContent = finalRiskLevel;
    growthRiskValue.className = `risk-badge ${getRiskClass(finalRiskLevel)}`;

    growthVisualValue.textContent = result.finalVisualState || "stable";
    growthSummaryBox.textContent = result.summary || "No summary.";

    renderGrowthTimelineTable(currentGrowthTimeline);
    drawGrowthChart(currentGrowthTimeline, 0);

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
    drawGrowthChart(currentGrowthTimeline, currentGrowthDayIndex);
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

async function loadAdminSummary() {
    if (!isAdminUser()) {
        alert("Admin Dashboard는 ADMIN 계정만 사용할 수 있습니다.");
        showDashboardMessage("ADMIN role required for Admin Dashboard.");
        return;
    }

    try {
        const response = await fetch("http://localhost:8080/api/admin/summary", {
            headers: buildAuthHeaders(),
        });

        if (response.status === 401) {
            alert("로그인 세션이 만료되었습니다. 다시 로그인해 주세요.");
            logoutUser();
            return;
        }

        if (handleForbiddenError(response, "Admin Dashboard는 ADMIN 계정만 사용할 수 있습니다.")) {
            return;
        }

        if (!response.ok) {
            throw new Error("Failed to load admin summary: " + response.status);
        }

        const summary = await response.json();

        renderAdminSummary(summary);

    } catch (error) {
        console.error(error);
        alert("Admin Summary 조회에 실패했습니다. Spring Boot 서버가 켜져 있는지 확인해 주세요.");
    }
}

function renderAdminSummary(summary) {
    adminPlantTypeCount.textContent = summary.plantTypeCount ?? 0;
    adminGeneRuleCount.textContent = summary.geneRuleCount ?? 0;
    adminSimulationLogCount.textContent = summary.simulationLogCount ?? 0;
    adminGrowthSimulationCount.textContent = summary.growthSimulationCount ?? 0;
    adminUserCount.textContent = summary.userCount ?? 0;

    adminAverageGrowthScore.textContent = Number(summary.averageGrowthScore || 0).toFixed(1);
    adminAverageWater.textContent = Number(summary.averageWater || 0).toFixed(1);
    adminAverageLight.textContent = Number(summary.averageLight || 0).toFixed(1);
    adminAverageTemperature.textContent = Number(summary.averageTemperature || 0).toFixed(1);
    adminAverageHumidity.textContent = Number(summary.averageHumidity || 0).toFixed(1);

    const overallStatus = summary.overallStatus || "INFO";
    adminOverallStatus.textContent = overallStatus;
    adminOverallStatus.className = getAdminStatusClass(overallStatus);

    adminLatestPlantType.textContent = summary.latestGrowthPlantType || "-";

    const latestRisk = summary.latestGrowthRiskLevel || "LOW";
    adminLatestRiskLevel.textContent = latestRisk;
    adminLatestRiskLevel.className = `risk-badge ${getRiskClass(latestRisk)}`;

    adminLatestVisualState.textContent = summary.latestGrowthVisualState || "-";

    adminRiskDistributionBox.textContent = [
        `CRITICAL: ${summary.criticalGrowthCount ?? 0}`,
        `HIGH: ${summary.highGrowthCount ?? 0}`,
        `MEDIUM: ${summary.mediumGrowthCount ?? 0}`,
        `LOW: ${summary.lowGrowthCount ?? 0}`,
    ].join("\n");

    drawAdminRiskChart(summary);
    drawAdminEnvironmentChart(summary);
    renderAdminInsights(summary.insights || []);
}

function renderAdminInsights(insights) {
    if (!adminInsightList) {
        return;
    }

    adminInsightList.innerHTML = "";

    if (!insights || insights.length === 0) {
        const emptyMessage = document.createElement("p");
        emptyMessage.textContent = "No admin insights loaded.";
        adminInsightList.appendChild(emptyMessage);
        return;
    }

    insights.forEach((insight) => {
        const item = document.createElement("div");

        const severity = insight.severity || "INFO";
        const message = insight.message || String(insight);

        item.className = `admin-insight-item ${getAdminInsightClass(severity)}`;

        item.innerHTML = `
            <span class="admin-insight-severity">${severity}</span>
            <span class="admin-insight-message">${message}</span>
        `;

        adminInsightList.appendChild(item);
    });
}

function getAdminInsightClass(severity) {
    switch (severity) {
        case "CRITICAL":
            return "admin-insight-critical";
        case "WARNING":
            return "admin-insight-warning";
        case "STABLE":
            return "admin-insight-stable";
        case "INFO":
        default:
            return "admin-insight-info";
    }
}

function getAdminStatusClass(status) {
    switch (status) {
        case "CRITICAL":
            return "admin-status-critical";
        case "WARNING":
            return "admin-status-warning";
        case "STABLE":
            return "admin-status-stable";
        case "INFO":
        default:
            return "admin-status-info";
    }
}

function drawAdminRiskChart(summary) {
    if (!adminRiskChartCanvas || !adminRiskChartContext) {
        return;
    }

    const ctx = adminRiskChartContext;
    const width = adminRiskChartCanvas.width;
    const height = adminRiskChartCanvas.height;

    ctx.clearRect(0, 0, width, height);

    ctx.fillStyle = "#ffffff";
    ctx.fillRect(0, 0, width, height);

    ctx.strokeStyle = "#e2e8f0";
    ctx.lineWidth = 1;
    ctx.strokeRect(0.5, 0.5, width - 1, height - 1);

    const data = [
        {
            label: "CRITICAL",
            value: Number(summary.criticalGrowthCount || 0),
            color: "#be123c",
        },
        {
            label: "HIGH",
            value: Number(summary.highGrowthCount || 0),
            color: "#c2410c",
        },
        {
            label: "MEDIUM",
            value: Number(summary.mediumGrowthCount || 0),
            color: "#b45309",
        },
        {
            label: "LOW",
            value: Number(summary.lowGrowthCount || 0),
            color: "#047857",
        },
    ];

    const maxValue = Math.max(...data.map((item) => item.value), 1);

    const paddingLeft = 48;
    const paddingRight = 24;
    const paddingTop = 28;
    const paddingBottom = 42;

    const chartWidth = width - paddingLeft - paddingRight;
    const chartHeight = height - paddingTop - paddingBottom;

    drawAdminRiskAxes(ctx, paddingLeft, paddingTop, chartWidth, chartHeight, maxValue);
    drawAdminRiskBars(ctx, data, paddingLeft, paddingTop, chartWidth, chartHeight, maxValue);
}

function drawAdminRiskAxes(ctx, left, top, chartWidth, chartHeight, maxValue) {
    ctx.strokeStyle = "#cbd5e1";
    ctx.lineWidth = 1;

    ctx.beginPath();
    ctx.moveTo(left, top);
    ctx.lineTo(left, top + chartHeight);
    ctx.lineTo(left + chartWidth, top + chartHeight);
    ctx.stroke();

    ctx.font = "12px Arial";
    ctx.fillStyle = "#64748b";

    const ySteps = 4;

    for (let i = 0; i <= ySteps; i++) {
        const value = Math.round((maxValue / ySteps) * i);
        const y = top + chartHeight - (value / maxValue) * chartHeight;

        ctx.strokeStyle = "#f1f5f9";
        ctx.beginPath();
        ctx.moveTo(left, y);
        ctx.lineTo(left + chartWidth, y);
        ctx.stroke();

        ctx.fillStyle = "#64748b";
        ctx.fillText(String(value), 16, y + 4);
    }
}

function drawAdminRiskBars(ctx, data, left, top, chartWidth, chartHeight, maxValue) {
    const gap = 18;
    const barWidth = (chartWidth - gap * (data.length + 1)) / data.length;

    data.forEach((item, index) => {
        const x = left + gap + index * (barWidth + gap);
        const barHeight = (item.value / maxValue) * chartHeight;
        const y = top + chartHeight - barHeight;

        ctx.fillStyle = item.color;
        ctx.fillRect(x, y, barWidth, barHeight);

        ctx.fillStyle = "#0f172a";
        ctx.font = "bold 13px Arial";
        ctx.fillText(String(item.value), x + barWidth / 2 - 4, y - 8);

        ctx.fillStyle = "#64748b";
        ctx.font = "12px Arial";
        ctx.fillText(item.label, x + 2, top + chartHeight + 24);
    });
}

function drawAdminEnvironmentChart(summary) {
    if (!adminEnvironmentChartCanvas || !adminEnvironmentChartContext) {
        return;
    }

    const ctx = adminEnvironmentChartContext;
    const width = adminEnvironmentChartCanvas.width;
    const height = adminEnvironmentChartCanvas.height;

    ctx.clearRect(0, 0, width, height);

    ctx.fillStyle = "#ffffff";
    ctx.fillRect(0, 0, width, height);

    ctx.strokeStyle = "#e2e8f0";
    ctx.lineWidth = 1;
    ctx.strokeRect(0.5, 0.5, width - 1, height - 1);

    const data = [
        {
            label: "Water",
            value: Number(summary.averageWater || 0),
            color: "#2563eb",
        },
        {
            label: "Light",
            value: Number(summary.averageLight || 0),
            color: "#eab308",
        },
        {
            label: "Temp",
            value: Number(summary.averageTemperature || 0),
            color: "#ef4444",
        },
        {
            label: "Humidity",
            value: Number(summary.averageHumidity || 0),
            color: "#06b6d4",
        },
    ];

    const maxValue = Math.max(100, ...data.map((item) => item.value));

    const paddingLeft = 48;
    const paddingRight = 24;
    const paddingTop = 28;
    const paddingBottom = 42;

    const chartWidth = width - paddingLeft - paddingRight;
    const chartHeight = height - paddingTop - paddingBottom;

    drawAdminEnvironmentAxes(ctx, paddingLeft, paddingTop, chartWidth, chartHeight, maxValue);
    drawAdminEnvironmentBars(ctx, data, paddingLeft, paddingTop, chartWidth, chartHeight, maxValue);
}

function drawAdminEnvironmentAxes(ctx, left, top, chartWidth, chartHeight, maxValue) {
    ctx.strokeStyle = "#cbd5e1";
    ctx.lineWidth = 1;

    ctx.beginPath();
    ctx.moveTo(left, top);
    ctx.lineTo(left, top + chartHeight);
    ctx.lineTo(left + chartWidth, top + chartHeight);
    ctx.stroke();

    ctx.font = "12px Arial";
    ctx.fillStyle = "#64748b";

    const ySteps = 4;

    for (let i = 0; i <= ySteps; i++) {
        const value = Math.round((maxValue / ySteps) * i);
        const y = top + chartHeight - (value / maxValue) * chartHeight;

        ctx.strokeStyle = "#f1f5f9";
        ctx.beginPath();
        ctx.moveTo(left, y);
        ctx.lineTo(left + chartWidth, y);
        ctx.stroke();

        ctx.fillStyle = "#64748b";
        ctx.fillText(String(value), 16, y + 4);
    }
}

function drawAdminEnvironmentBars(ctx, data, left, top, chartWidth, chartHeight, maxValue) {
    const gap = 18;
    const barWidth = (chartWidth - gap * (data.length + 1)) / data.length;

    data.forEach((item, index) => {
        const x = left + gap + index * (barWidth + gap);
        const barHeight = (item.value / maxValue) * chartHeight;
        const y = top + chartHeight - barHeight;

        ctx.fillStyle = item.color;
        ctx.fillRect(x, y, barWidth, barHeight);

        ctx.fillStyle = "#0f172a";
        ctx.font = "bold 13px Arial";
        ctx.fillText(item.value.toFixed(1), x + barWidth / 2 - 12, y - 8);

        ctx.fillStyle = "#64748b";
        ctx.font = "12px Arial";
        ctx.fillText(item.label, x + 2, top + chartHeight + 24);
    });
}

function drawGrowthChart(timeline, activeIndex = -1) {
    if (!growthChartCanvas || !growthChartContext) {
        return;
    }

    const ctx = growthChartContext;
    const width = growthChartCanvas.width;
    const height = growthChartCanvas.height;

    ctx.clearRect(0, 0, width, height);

    drawChartBackground(ctx, width, height);

    if (!timeline || timeline.length === 0) {
        ctx.font = "16px Arial";
        ctx.fillStyle = "#64748b";
        ctx.fillText("No growth timeline data.", 24, 42);
        return;
    }

    const paddingLeft = 52;
    const paddingRight = 24;
    const paddingTop = 28;
    const paddingBottom = 42;

    const chartWidth = width - paddingLeft - paddingRight;
    const chartHeight = height - paddingTop - paddingBottom;

    const days = timeline.map((item) => Number(item.day || 0));
    const growthScores = timeline.map((item) => Number(item.growthScore || 0));
    const totalEnergies = timeline.map((item) => Number(item.totalEnergy || 0));

    const maxDay = Math.max(...days, 1);
    const maxValue = Math.max(160, ...growthScores, ...totalEnergies);

    drawAxes(ctx, paddingLeft, paddingTop, chartWidth, chartHeight, maxDay, maxValue);

    drawLineSeries(
        ctx,
        timeline,
        "growthScore",
        paddingLeft,
        paddingTop,
        chartWidth,
        chartHeight,
        maxDay,
        maxValue,
        "#2563eb"
    );

    drawLineSeries(
        ctx,
        timeline,
        "totalEnergy",
        paddingLeft,
        paddingTop,
        chartWidth,
        chartHeight,
        maxDay,
        maxValue,
        "#16a34a"
    );

    drawActiveDayMarker(
        ctx,
        timeline,
        activeIndex,
        paddingLeft,
        paddingTop,
        chartWidth,
        chartHeight,
        maxDay,
        maxValue
    );

    drawChartLegend(ctx, width);
}

function drawChartBackground(ctx, width, height) {
    ctx.fillStyle = "#ffffff";
    ctx.fillRect(0, 0, width, height);

    ctx.strokeStyle = "#e2e8f0";
    ctx.lineWidth = 1;
    ctx.strokeRect(0.5, 0.5, width - 1, height - 1);
}

function drawAxes(ctx, left, top, chartWidth, chartHeight, maxDay, maxValue) {
    ctx.strokeStyle = "#cbd5e1";
    ctx.lineWidth = 1;

    ctx.beginPath();
    ctx.moveTo(left, top);
    ctx.lineTo(left, top + chartHeight);
    ctx.lineTo(left + chartWidth, top + chartHeight);
    ctx.stroke();

    ctx.font = "12px Arial";
    ctx.fillStyle = "#64748b";

    const ySteps = 4;

    for (let i = 0; i <= ySteps; i++) {
        const value = Math.round((maxValue / ySteps) * i);
        const y = top + chartHeight - (value / maxValue) * chartHeight;

        ctx.strokeStyle = "#f1f5f9";
        ctx.beginPath();
        ctx.moveTo(left, y);
        ctx.lineTo(left + chartWidth, y);
        ctx.stroke();

        ctx.fillStyle = "#64748b";
        ctx.fillText(String(value), 12, y + 4);
    }

    const xSteps = Math.min(6, maxDay);

    for (let i = 0; i <= xSteps; i++) {
        const day = Math.round((maxDay / xSteps) * i);
        const x = left + (day / maxDay) * chartWidth;

        ctx.fillStyle = "#64748b";
        ctx.fillText(`D${day}`, x - 10, top + chartHeight + 24);
    }
}

function drawLineSeries(
    ctx,
    timeline,
    key,
    left,
    top,
    chartWidth,
    chartHeight,
    maxDay,
    maxValue,
    color
) {
    ctx.strokeStyle = color;
    ctx.fillStyle = color;
    ctx.lineWidth = 3;

    ctx.beginPath();

    timeline.forEach((item, index) => {
        const day = Number(item.day || 0);
        const value = Number(item[key] || 0);

        const x = left + (day / maxDay) * chartWidth;
        const y = top + chartHeight - (value / maxValue) * chartHeight;

        if (index === 0) {
            ctx.moveTo(x, y);
        } else {
            ctx.lineTo(x, y);
        }
    });

    ctx.stroke();

    timeline.forEach((item) => {
        const day = Number(item.day || 0);
        const value = Number(item[key] || 0);

        const x = left + (day / maxDay) * chartWidth;
        const y = top + chartHeight - (value / maxValue) * chartHeight;

        ctx.beginPath();
        ctx.arc(x, y, 3.5, 0, Math.PI * 2);
        ctx.fill();
    });
}

function drawActiveDayMarker(
    ctx,
    timeline,
    activeIndex,
    left,
    top,
    chartWidth,
    chartHeight,
    maxDay,
    maxValue
) {
    if (!timeline || timeline.length === 0) {
        return;
    }

    if (activeIndex < 0 || activeIndex >= timeline.length) {
        return;
    }

    const dayData = timeline[activeIndex];

    const day = Number(dayData.day || 0);
    const growthScore = Number(dayData.growthScore || 0);
    const totalEnergy = Number(dayData.totalEnergy || 0);

    const x = left + (day / maxDay) * chartWidth;
    const growthY = top + chartHeight - (growthScore / maxValue) * chartHeight;
    const energyY = top + chartHeight - (totalEnergy / maxValue) * chartHeight;

    ctx.save();

    ctx.strokeStyle = "#0f172a";
    ctx.lineWidth = 1.5;
    ctx.setLineDash([5, 5]);

    ctx.beginPath();
    ctx.moveTo(x, top);
    ctx.lineTo(x, top + chartHeight);
    ctx.stroke();

    ctx.setLineDash([]);

    ctx.fillStyle = "#0f172a";
    ctx.font = "bold 12px Arial";
    ctx.fillText(`Day ${day}`, x + 8, top + 14);

    ctx.fillStyle = "#2563eb";
    ctx.beginPath();
    ctx.arc(x, growthY, 6, 0, Math.PI * 2);
    ctx.fill();

    ctx.fillStyle = "#16a34a";
    ctx.beginPath();
    ctx.arc(x, energyY, 6, 0, Math.PI * 2);
    ctx.fill();

    ctx.restore();
}

function drawChartLegend(ctx, width) {
    ctx.font = "13px Arial";

    ctx.fillStyle = "#2563eb";
    ctx.fillRect(width - 210, 18, 12, 12);
    ctx.fillStyle = "#334155";
    ctx.fillText("Growth Score", width - 190, 29);

    ctx.fillStyle = "#16a34a";
    ctx.fillRect(width - 105, 18, 12, 12);
    ctx.fillStyle = "#334155";
    ctx.fillText("Energy", width - 85, 29);
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

function logoutUser() {
    localStorage.removeItem("bioOsCurrentUser");
    localStorage.removeItem("bioOsJwtToken");
    currentUser = null;
    window.location.href = "auth.html";
}

function loadCurrentUser() {
    const savedUser = localStorage.getItem("bioOsCurrentUser");

    if (!savedUser) {
        return null;
    }

    try {
        return JSON.parse(savedUser);
    } catch (error) {
        localStorage.removeItem("bioOsCurrentUser");
        localStorage.removeItem("bioOsJwtToken");
        return null;
    }
}

function getAuthToken() {
    if (currentUser && currentUser.token) {
        return currentUser.token;
    }

    return localStorage.getItem("bioOsJwtToken") || "";
}

function buildAuthHeaders() {
    const token = getAuthToken();

    if (!token) {
        return {};
    }

    return {
        Authorization: `Bearer ${token}`,
    };
}

function buildJsonHeaders() {
    const headers = {
        "Content-Type": "application/json",
    };

    const token = getAuthToken();

    if (token) {
        headers.Authorization = `Bearer ${token}`;
    }

    return headers;
}

function handleAuthError(response) {
    if (response.status === 401 || response.status === 403) {
        alert("로그인 세션이 만료되었거나 토큰이 올바르지 않습니다. 다시 로그인해 주세요.");
        logoutUser();
        return true;
    }

    return false;
}

function handleForbiddenError(response, message) {
    if (response.status === 403) {
        alert(message || "이 기능을 사용할 권한이 없습니다.");
        return true;
    }

    return false;
}

function renderAuthState() {
    if (!currentUser) {
        window.location.href = "auth.html";
        return;
    }

    applyAuthGuard();
}

function applyAuthGuard() {
    const loggedIn = Boolean(currentUser);
    const isAdmin = loggedIn && currentUser.role === "ADMIN";

    setButtonEnabled(runButton, loggedIn);
    setButtonEnabled(randomButton, loggedIn);
    setButtonEnabled(loadLogsButton, loggedIn);
    setButtonEnabled(resetButton, loggedIn);

    setButtonEnabled(runGrowthButton, loggedIn);
    setButtonEnabled(loadGrowthSimulationsButton, loggedIn);

    setButtonEnabled(addRuleButton, isAdmin);
    setButtonEnabled(loadAdminSummaryButton, isAdmin);

    if (adminDashboardCard) {
        adminDashboardCard.classList.toggle("locked-card", !isAdmin);
    }

    if (!authGuardMessage) {
        return;
    }

    if (!loggedIn) {
        authGuardMessage.textContent =
            "Login required: Simulation, Growth Simulation, Logs, and Admin Dashboard are locked.";
        authGuardMessage.className = "auth-guard-message auth-guard-locked";
        return;
    }

    if (!isAdmin) {
        authGuardMessage.textContent =
            "USER mode: You can run simulations and view your own logs. Admin Dashboard is locked.";
        authGuardMessage.className = "auth-guard-message auth-guard-user";
        return;
    }

    authGuardMessage.textContent =
        "ADMIN mode: All BIO-OS features are available.";
    authGuardMessage.className = "auth-guard-message auth-guard-admin";
}

function setButtonEnabled(button, enabled) {
    if (!button) {
        return;
    }

    button.disabled = !enabled;
    button.classList.toggle("disabled-button", !enabled);
}

function showDashboardMessage(message) {
    if (authGuardMessage) {
        authGuardMessage.textContent = message;
    }
}

function isAdminUser() {
    return Boolean(currentUser && currentUser.role === "ADMIN");
}

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

drawGrowthChart([]);

drawAdminRiskChart({
    criticalGrowthCount: 0,
    highGrowthCount: 0,
    mediumGrowthCount: 0,
    lowGrowthCount: 0,
});

drawAdminEnvironmentChart({
    averageWater: 0,
    averageLight: 0,
    averageTemperature: 0,
    averageHumidity: 0,
});

renderAdminInsights([]);

loadPlantTypes();
renderAuthState();

if (isAdminUser()) {
    loadGeneRules();
    loadAdminSummary();
} else if (ruleList) {
    ruleList.innerHTML = "";

    const lockedItem = document.createElement("li");
    lockedItem.textContent = "ADMIN only: Gene Rule list is locked.";
    ruleList.appendChild(lockedItem);
}