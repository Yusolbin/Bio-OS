package com.yusolbin.bio_os.controller;

import com.yusolbin.bio_os.dto.SimulationLogResponse;
import com.yusolbin.bio_os.dto.SimulationRequest;
import com.yusolbin.bio_os.dto.SimulationResponse;
import com.yusolbin.bio_os.security.CurrentUserService;
import com.yusolbin.bio_os.service.SimulationService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/simulations")
@CrossOrigin(origins = "*")
public class SimulationController {

    private final SimulationService simulationService;
    private final CurrentUserService currentUserService;

    public SimulationController(
            SimulationService simulationService,
            CurrentUserService currentUserService
    ) {
        this.simulationService = simulationService;
        this.currentUserService = currentUserService;
    }

    @PostMapping("/run")
    public SimulationResponse runSimulation(@RequestBody SimulationRequest request) {
        Long currentUserId = currentUserService.getCurrentUserId();
        request.setUserId(currentUserId);

        return simulationService.runSimulation(request);
    }

    @GetMapping("/logs")
    public List<SimulationLogResponse> getSimulationLogs() {
        Long currentUserId = currentUserService.getCurrentUserId();

        return simulationService.getSimulationLogs(currentUserId);
    }

    @DeleteMapping("/logs")
    public void clearSimulationLogs() {
        Long currentUserId = currentUserService.getCurrentUserId();

        simulationService.clearSimulationLogs(currentUserId);
    }
}