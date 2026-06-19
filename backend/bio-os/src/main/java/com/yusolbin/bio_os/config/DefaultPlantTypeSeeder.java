package com.yusolbin.bio_os.config;

import com.yusolbin.bio_os.model.PlantType;
import com.yusolbin.bio_os.repository.PlantTypeRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class DefaultPlantTypeSeeder implements CommandLineRunner {

    private final PlantTypeRepository plantTypeRepository;

    public DefaultPlantTypeSeeder(PlantTypeRepository plantTypeRepository) {
        this.plantTypeRepository = plantTypeRepository;
    }

    @Override
    public void run(String... args) {
        createIfNotExists(
                "Basil",
                50, 80,
                60, 90,
                20, 28,
                40, 70,
                1.2
        );

        createIfNotExists(
                "Succulent",
                10, 35,
                70, 100,
                18, 32,
                20, 50,
                0.8
        );

        createIfNotExists(
                "Monstera",
                40, 70,
                40, 75,
                20, 30,
                50, 80,
                1.0
        );
    }

    private void createIfNotExists(
            String name,
            double optimalWaterMin,
            double optimalWaterMax,
            double optimalLightMin,
            double optimalLightMax,
            double optimalTemperatureMin,
            double optimalTemperatureMax,
            double optimalHumidityMin,
            double optimalHumidityMax,
            double baseGrowthRate
    ) {
        boolean exists = plantTypeRepository.findByName(name).isPresent();

        if (exists) {
            return;
        }

        PlantType plantType = new PlantType(
                name,
                optimalWaterMin,
                optimalWaterMax,
                optimalLightMin,
                optimalLightMax,
                optimalTemperatureMin,
                optimalTemperatureMax,
                optimalHumidityMin,
                optimalHumidityMax,
                baseGrowthRate
        );

        plantTypeRepository.save(plantType);
    }
}
