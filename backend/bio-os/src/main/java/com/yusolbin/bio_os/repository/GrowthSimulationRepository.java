package com.yusolbin.bio_os.repository;

import com.yusolbin.bio_os.model.GrowthSimulation;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface GrowthSimulationRepository extends JpaRepository<GrowthSimulation, Long> {

    List<GrowthSimulation> findAllByOrderByIdDesc();

    List<GrowthSimulation> findAllByUserAccount_IdOrderByIdDesc(Long userId);

    Optional<GrowthSimulation> findByIdAndUserAccount_Id(Long simulationId, Long userId);
}