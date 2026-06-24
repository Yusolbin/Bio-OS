package com.yusolbin.bio_os.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.yusolbin.bio_os.dto.CppEngineResult;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

@Service
public class CppEngineBridgeService {

    private final ObjectMapper objectMapper;

    public CppEngineBridgeService(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    public CppEngineResult runEngine(
            double water,
            double light,
            double temperature,
            double humidity
    ) {
        try {
            Path enginePath = resolveEnginePath();

            if (!Files.exists(enginePath)) {
                return CppEngineResult.failure(
                        -1,
                        "",
                        "C++ engine executable not found: " + enginePath
                );
            }

            ProcessBuilder processBuilder = new ProcessBuilder(
                    enginePath.toString(),
                    String.valueOf(water),
                    String.valueOf(light),
                    String.valueOf(temperature),
                    String.valueOf(humidity)
            );

            processBuilder.redirectErrorStream(true);

            Process process = processBuilder.start();

            List<String> outputLines = new ArrayList<>();

            try (BufferedReader reader = new BufferedReader(
                    new InputStreamReader(process.getInputStream(), StandardCharsets.UTF_8)
            )) {
                String line;

                while ((line = reader.readLine()) != null) {
                    outputLines.add(line);
                }
            }

            int exitCode = process.waitFor();
            String rawOutput = String.join(System.lineSeparator(), outputLines);

            String snapshotJson = findLastJsonLine(outputLines);

            if (exitCode != 0) {
                return CppEngineResult.failure(
                        exitCode,
                        rawOutput,
                        "C++ engine exited with non-zero code: " + exitCode
                );
            }

            if (snapshotJson == null) {
                return CppEngineResult.failure(
                        exitCode,
                        rawOutput,
                        "C++ engine output did not contain JSON snapshot."
                );
            }

            JsonNode snapshot = objectMapper.readTree(snapshotJson);

            return CppEngineResult.success(
                    exitCode,
                    rawOutput,
                    snapshotJson,
                    snapshot
            );
        } catch (Exception e) {
            return CppEngineResult.failure(
                    -1,
                    "",
                    e.getMessage()
            );
        }
    }

    private Path resolveEnginePath() {
        Path currentPath = Paths.get("").toAbsolutePath();

        Path fromBackendPath = currentPath
                .resolve("../../engine/bio_os_engine.exe")
                .normalize();

        if (Files.exists(fromBackendPath)) {
            return fromBackendPath;
        }

        Path fromRootPath = currentPath
                .resolve("engine/bio_os_engine.exe")
                .normalize();

        if (Files.exists(fromRootPath)) {
            return fromRootPath;
        }

        return fromBackendPath;
    }

    private String findLastJsonLine(List<String> outputLines) {
        for (int i = outputLines.size() - 1; i >= 0; i--) {
            String line = outputLines.get(i).trim();

            if (line.startsWith("{") && line.endsWith("}")) {
                return line;
            }
        }

        return null;
    }
}