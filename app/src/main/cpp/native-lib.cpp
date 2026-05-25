#include <jni.h>
#include <string>

#include "api/EngineNativeBridge.hpp"

extern "C"
JNIEXPORT jlong JNICALL
Java_com_cookandroid_bioosapp_BioOSEngine_nativeCreateEngine(
    JNIEnv* env,
    jobject thiz
) {
    void* engine = BioOS_CreateEngine();
    return reinterpret_cast<jlong>(engine);
}

extern "C"
JNIEXPORT void JNICALL
Java_com_cookandroid_bioosapp_BioOSEngine_nativeDestroyEngine(
    JNIEnv* env,
    jobject thiz,
    jlong handle
) {
    BioOS_DestroyEngine(reinterpret_cast<void*>(handle));
}

extern "C"
JNIEXPORT void JNICALL
Java_com_cookandroid_bioosapp_BioOSEngine_nativeInitializeDefaultPlant(
    JNIEnv* env,
    jobject thiz,
    jlong handle
) {
    BioOS_InitializeDefaultPlant(reinterpret_cast<void*>(handle));
}

extern "C"
JNIEXPORT void JNICALL
Java_com_cookandroid_bioosapp_BioOSEngine_nativeAddRule(
    JNIEnv* env,
    jobject thiz,
    jlong handle,
    jstring ruleText
) {
    const char* ruleChars = env->GetStringUTFChars(ruleText, nullptr);

    BioOS_AddRule(
        reinterpret_cast<void*>(handle),
        ruleChars
    );

    env->ReleaseStringUTFChars(ruleText, ruleChars);
}

extern "C"
JNIEXPORT void JNICALL
Java_com_cookandroid_bioosapp_BioOSEngine_nativeRunTick(
    JNIEnv* env,
    jobject thiz,
    jlong handle,
    jdouble waterInput,
    jdouble light,
    jdouble temperature
) {
    BioOS_RunTick(
        reinterpret_cast<void*>(handle),
        waterInput,
        light,
        temperature
    );
}

extern "C"
JNIEXPORT jstring JNICALL
Java_com_cookandroid_bioosapp_BioOSEngine_nativeGetSnapshotJson(
    JNIEnv* env,
    jobject thiz,
    jlong handle
) {
    const char* json = BioOS_GetSnapshotJson(
        reinterpret_cast<void*>(handle)
    );

    return env->NewStringUTF(json);
}