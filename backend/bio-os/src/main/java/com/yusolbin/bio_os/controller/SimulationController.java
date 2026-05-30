package com.yusolbin.bio_os.controller;

import com.yusolbin.bio_os.dto.SimulationLogResponse;
import com.yusolbin.bio_os.dto.SimulationRequest;
import com.yusolbin.bio_os.dto.SimulationResponse;
import com.yusolbin.bio_os.service.SimulationService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/simulations")
@CrossOrigin(origins = "*")
public class SimulationController {

    private final SimulationService simulationService;

    public SimulationController(SimulationService simulationService) {
        this.simulationService = simulationService;
    }

    @PostMapping("/run")
    public SimulationResponse runSimulation(@RequestBody SimulationRequest request) {
        return simulationService.runSimulation(request);
    }

    @GetMapping("/logs")
    public List<SimulationLogResponse> getSimulationLogs() {
        return simulationService.getSimulationLogs();
    }

    @DeleteMapping("/logs")
    public void clearSimulationLogs() {
        simulationService.clearSimulationLogs();
    }
}