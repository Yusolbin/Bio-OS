package com.yusolbin.bio_os.repository;

import com.yusolbin.bio_os.model.SimulationLog;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SimulationLogRepository extends JpaRepository<SimulationLog, Long> {
}