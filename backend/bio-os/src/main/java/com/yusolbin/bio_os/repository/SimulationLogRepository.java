package com.yusolbin.bio_os.repository;

import com.yusolbin.bio_os.model.SimulationLog;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SimulationLogRepository extends JpaRepository<SimulationLog, Long> {

    List<SimulationLog> findAllByOrderByIdDesc();

    List<SimulationLog> findAllByUserAccount_IdOrderByIdDesc(Long userId);

    void deleteByUserAccount_Id(Long userId);
}