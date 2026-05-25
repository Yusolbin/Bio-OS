#include "api/EngineNativeBridge.hpp"
#include "api/EngineFacade.hpp"

#include <string>
#include <exception>
#include <iostream>

static std::string g_lastString;

void* BioOS_CreateEngine() {
    try {
        EngineFacade* engine = new EngineFacade();
        return engine;
    }
    catch (const std::exception& e) {
        std::cerr << "[BioOS_CreateEngine Error] " << e.what() << std::endl;
        return nullptr;
    }
}

void BioOS_DestroyEngine(void* engineHandle) {
    if (engineHandle == nullptr) {
        return;
    }

    EngineFacade* engine = static_cast<EngineFacade*>(engineHandle);
    delete engine;
}

void BioOS_InitializeDefaultPlant(void* engineHandle) {
    if (engineHandle == nullptr) {
        return;
    }

    EngineFacade* engine = static_cast<EngineFacade*>(engineHandle);
    engine->initializeDefaultPlant();
}

void BioOS_AddRule(void* engineHandle, const char* ruleText) {
    if (engineHandle == nullptr || ruleText == nullptr) {
        return;
    }

    try {
        EngineFacade* engine = static_cast<EngineFacade*>(engineHandle);
        engine->addRule(ruleText);
    }
    catch (const std::exception& e) {
        std::cerr << "[BioOS_AddRule Error] " << e.what() << std::endl;
    }
}

void BioOS_RunTick(
    void* engineHandle,
    double waterInput,
    double light,
    double temperature
) {
    if (engineHandle == nullptr) {
        return;
    }

    try {
        EngineFacade* engine = static_cast<EngineFacade*>(engineHandle);
        engine->runTick(waterInput, light, temperature);
    }
    catch (const std::exception& e) {
        std::cerr << "[BioOS_RunTick Error] " << e.what() << std::endl;
    }
}

const char* BioOS_GetSnapshotJson(void* engineHandle) {
    if (engineHandle == nullptr) {
        g_lastString = "{}";
        return g_lastString.c_str();
    }

    try {
        EngineFacade* engine = static_cast<EngineFacade*>(engineHandle);
        g_lastString = engine->getSnapshotJson();
        return g_lastString.c_str();
    }
    catch (const std::exception& e) {
        std::cerr << "[BioOS_GetSnapshotJson Error] " << e.what() << std::endl;
        g_lastString = "{}";
        return g_lastString.c_str();
    }
}

void BioOS_ClearLastString() {
    g_lastString.clear();
}