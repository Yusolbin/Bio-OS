package com.yusolbin.bio_os.dto;

import com.fasterxml.jackson.databind.JsonNode;

public class CppEngineResult {

    private boolean success;
    private String engineSource;
    private int exitCode;
    private String rawOutput;
    private String snapshotJson;
    private JsonNode snapshot;
    private String errorMessage;

    public CppEngineResult() {
    }

    public static CppEngineResult success(
            int exitCode,
            String rawOutput,
            String snapshotJson,
            JsonNode snapshot
    ) {
        CppEngineResult result = new CppEngineResult();
        result.success = true;
        result.engineSource = "CPP_SHARED_ENGINE_CLI";
        result.exitCode = exitCode;
        result.rawOutput = rawOutput;
        result.snapshotJson = snapshotJson;
        result.snapshot = snapshot;
        result.errorMessage = null;
        return result;
    }

    public static CppEngineResult failure(
            int exitCode,
            String rawOutput,
            String errorMessage
    ) {
        CppEngineResult result = new CppEngineResult();
        result.success = false;
        result.engineSource = "CPP_SHARED_ENGINE_CLI";
        result.exitCode = exitCode;
        result.rawOutput = rawOutput;
        result.snapshotJson = null;
        result.snapshot = null;
        result.errorMessage = errorMessage;
        return result;
    }

    public boolean isSuccess() {
        return success;
    }

    public String getEngineSource() {
        return engineSource;
    }

    public int getExitCode() {
        return exitCode;
    }

    public String getRawOutput() {
        return rawOutput;
    }

    public String getSnapshotJson() {
        return snapshotJson;
    }

    public JsonNode getSnapshot() {
        return snapshot;
    }

    public String getErrorMessage() {
        return errorMessage;
    }
}