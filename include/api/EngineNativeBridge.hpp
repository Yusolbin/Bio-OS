#pragma once

#ifdef _WIN32
#define BIO_OS_API __declspec(dllexport)
#else
#define BIO_OS_API
#endif

#ifdef __cplusplus
extern "C" {
#endif

    BIO_OS_API void* BioOS_CreateEngine();

    BIO_OS_API void BioOS_DestroyEngine(void* engineHandle);

    BIO_OS_API void BioOS_InitializeDefaultPlant(void* engineHandle);

    BIO_OS_API void BioOS_AddRule(void* engineHandle, const char* ruleText);

    BIO_OS_API void BioOS_RunTick(
        void* engineHandle,
        double waterInput,
        double light,
        double temperature
    );

    BIO_OS_API const char* BioOS_GetSnapshotJson(void* engineHandle);

    BIO_OS_API void BioOS_ClearLastString();

#ifdef __cplusplus
}
#endif
