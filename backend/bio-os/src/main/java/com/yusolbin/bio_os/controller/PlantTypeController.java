package com.yusolbin.bio_os.controller;

import com.yusolbin.bio_os.dto.PlantTypeResponse;
import com.yusolbin.bio_os.service.PlantTypeService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/plants")
@CrossOrigin(origins = "*")
public class PlantTypeController {

    private final PlantTypeService plantTypeService;

    public PlantTypeController(PlantTypeService plantTypeService) {
        this.plantTypeService = plantTypeService;
    }

    @GetMapping
    public List<PlantTypeResponse> getPlantTypes() {
        return plantTypeService.getPlantTypes();
    }
}