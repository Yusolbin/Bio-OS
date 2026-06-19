package com.yusolbin.bio_os.repository;

import com.yusolbin.bio_os.model.PlantType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PlantTypeRepository extends JpaRepository<PlantType, Long> {

    Optional<PlantType> findByName(String name);
}