package com.yusolbin.bio_os.controller;

import com.yusolbin.bio_os.dto.GrowthSimulationRequest;
import com.yusolbin.bio_os.dto.GrowthSimulationResponse;
import com.yusolbin.bio_os.dto.GrowthSimulationSummaryResponse;
import com.yusolbin.bio_os.service.GrowthSimulationService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/growth")
@CrossOrigin(origins = "*")
public class GrowthSimulationController {

    private final GrowthSimulationService growthSimulationService;

    public GrowthSimulationController(GrowthSimulationService growthSimulationService) {
        this.growthSimulationService = growthSimulationService;
    }

    @PostMapping("/simulate")
    public GrowthSimulationResponse simulateGrowth(
            @RequestBody GrowthSimulationRequest request
    ) {
        return growthSimulationService.simulateGrowth(request);
    }

    @GetMapping("/simulations")
    public List<GrowthSimulationSummaryResponse> getGrowthSimulations() {
        return growthSimulationService.getGrowthSimulations();
    }

    @GetMapping("/simulations/{simulationId}")
    public GrowthSimulationResponse getGrowthSimulation(
            @PathVariable Long simulationId
    ) {
        return growthSimulationService.getGrowthSimulation(simulationId);
    }
}