package com.cookandroid.bioosapp;

public class BioOSEngine {
    static{
        System.loadLibrary("bio_os_native");
    }

    private long nativeHandle = 0;

    public BioOSEngine() {
        nativeHandle = nativeCreateEngine();
        nativeInitializeDefaultPlant(nativeHandle);
    }

    public void addRule(String ruleText) {
        nativeAddRule(nativeHandle, ruleText);
    }

    public void runTick(double water, double light, double temperature) {
        nativeRunTick(nativeHandle, water, light, temperature);
    }

    public String getSnapshotJson() {
        return nativeGetSnapshotJson(nativeHandle);
    }

    public void destroy() {
        if (nativeHandle != 0) {
            nativeDestroyEngine(nativeHandle);
            nativeHandle = 0;
        }
    }

    private native long nativeCreateEngine();

    private native void nativeDestroyEngine(long handle);

    private native void nativeInitializeDefaultPlant(long handle);

    private native void nativeAddRule(long handle, String ruleText);

    private native void nativeRunTick(
        long handle,
        double water,
        double light,
        double temperature
    );

    private native String nativeGetSnapshotJson(long handle);
}
