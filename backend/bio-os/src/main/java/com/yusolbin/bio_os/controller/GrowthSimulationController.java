package com.yusolbin.bio_os.controller;

import com.yusolbin.bio_os.dto.GrowthSimulationRequest;
import com.yusolbin.bio_os.dto.GrowthSimulationResponse;
import com.yusolbin.bio_os.dto.GrowthSimulationSummaryResponse;
import com.yusolbin.bio_os.security.CurrentUserService;
import com.yusolbin.bio_os.service.GrowthSimulationService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/growth")
@CrossOrigin(origins = "*")
public class GrowthSimulationController {

    private final GrowthSimulationService growthSimulationService;
    private final CurrentUserService currentUserService;

    public GrowthSimulationController(
            GrowthSimulationService growthSimulationService,
            CurrentUserService currentUserService
    ) {
        this.growthSimulationService = growthSimulationService;
        this.currentUserService = currentUserService;
    }

    @PostMapping("/simulate")
    public GrowthSimulationResponse simulateGrowth(
            @RequestBody GrowthSimulationRequest request
    ) {
        Long currentUserId = currentUserService.getCurrentUserId();
        request.setUserId(currentUserId);

        return growthSimulationService.simulateGrowth(request);
    }

    @GetMapping("/simulations")
    public List<GrowthSimulationSummaryResponse> getGrowthSimulations() {
        Long currentUserId = currentUserService.getCurrentUserId();

        return growthSimulationService.getGrowthSimulations(currentUserId);
    }

    @GetMapping("/simulations/{simulationId}")
    public GrowthSimulationResponse getGrowthSimulation(
            @PathVariable Long simulationId
    ) {
        Long currentUserId = currentUserService.getCurrentUserId();

        return growthSimulationService.getGrowthSimulation(simulationId, currentUserId);
    }
}