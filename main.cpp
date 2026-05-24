#include <iostream>
#include "api/EngineFacade.hpp"
#include "api/EngineNativeBridge.hpp"

int main() {
    std::cout << "======================================" << std::endl;
    std::cout << " Bio-OS: Algorithmic Bio-System Engine" << std::endl;
    std::cout << "======================================" << std::endl;
    std::cout << std::endl;

    EngineFacade engine;

    std::cout << "[1] Initialize Default Plant" << std::endl;
    engine.initializeDefaultPlant();

    std::cout << std::endl;
    engine.printPlantTree();

    std::cout << std::endl;
    std::cout << "[2] Register Gene Rules" << std::endl;
    engine.addRule("IF Water < 30 THEN DroughtMode = ON");
    engine.addRule("IF Light > 70 THEN PhotosynthesisBoost = ON");
    engine.addRule("IF Temperature > 35 THEN HeatStress = ON");
    engine.addRule("IF Water < 10 THEN PruningMode = ON");
    engine.addRule("IF Water > 100 THEN RecoveryMode = ON");

    std::cout << std::endl;
    engine.printRules();

    std::cout << std::endl;
    std::cout << "[3] Run Multi-Tick Environment Scenario" << std::endl;

    std::cout << std::endl;
    std::cout << "Scenario Tick 1: Dry but bright environment" << std::endl;
    std::cout << "Water=15, Light=80, Temperature=32" << std::endl;
    engine.runTick(15.0, 80.0, 32.0);

    std::cout << std::endl;
    std::cout << "Scenario Tick 2: Severe drought environment" << std::endl;
    std::cout << "Water=5, Light=85, Temperature=34" << std::endl;
    engine.runTick(5.0, 85.0, 34.0);

    std::cout << std::endl;
    std::cout << "Scenario Tick 3: Recovery environment" << std::endl;
    std::cout << "Water=120, Light=75, Temperature=26" << std::endl;
    engine.runTick(120.0, 75.0, 26.0);

    engine.runTick(15.0, 80.0, 32.0);

    std::cout << std::endl;
    std::cout << "[4] Plant Tree After Simulation" << std::endl;
    engine.printPlantTree();

    std::cout << std::endl;
    std::cout << "[5] Inferred Biological States" << std::endl;
    engine.printInferredStates();

    std::cout << std::endl;
    std::cout << "[6] Simulation Summary" << std::endl;
    engine.printSimulationSummary();

    std::cout << std::endl;
    std::cout << "[7] Simulation History" << std::endl;
    engine.printSimulationHistory();

    std::cout << std::endl;
    std::cout << "[8] Algorithm Trace Logs" << std::endl;
    engine.printLogs();

    std::cout << std::endl;
    std::cout << "[9] Engine Snapshot JSON" << std::endl;
    std::cout << engine.getSnapshotJson() << std::endl;

    std::cout << std::endl;
    std::cout << "======================================" << std::endl;
    std::cout << " Bio-OS Demo Completed" << std::endl;
    std::cout << "======================================" << std::endl;


    std::cout << std::endl;
    std::cout << "[10] Native Bridge Test" << std::endl;

    void* nativeEngine = BioOS_CreateEngine();

    BioOS_InitializeDefaultPlant(nativeEngine);
    BioOS_AddRule(nativeEngine, "IF Water < 30 THEN DroughtMode = ON");
    BioOS_AddRule(nativeEngine, "IF Light > 70 THEN PhotosynthesisBoost = ON");
    BioOS_AddRule(nativeEngine, "IF Water > 100 THEN RecoveryMode = ON");

    BioOS_RunTick(nativeEngine, 15.0, 80.0, 32.0);

    const char* snapshot = BioOS_GetSnapshotJson(nativeEngine);

    std::cout << "[Native Bridge Snapshot]" << std::endl;
    std::cout << snapshot << std::endl;

    BioOS_DestroyEngine(nativeEngine);
    BioOS_ClearLastString();

    return 0;
}