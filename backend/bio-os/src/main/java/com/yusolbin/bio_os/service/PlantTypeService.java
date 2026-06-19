package com.yusolbin.bio_os.service;

import com.yusolbin.bio_os.dto.PlantTypeResponse;
import com.yusolbin.bio_os.repository.PlantTypeRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PlantTypeService {

    private final PlantTypeRepository plantTypeRepository;

    public PlantTypeService(PlantTypeRepository plantTypeRepository) {
        this.plantTypeRepository = plantTypeRepository;
    }

    public List<PlantTypeResponse> getPlantTypes() {
        return plantTypeRepository.findAll()
                .stream()
                .map(PlantTypeResponse::new)
                .toList();
    }
}