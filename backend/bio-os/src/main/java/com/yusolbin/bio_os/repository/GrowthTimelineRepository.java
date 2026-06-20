package com.yusolbin.bio_os.repository;

import com.yusolbin.bio_os.model.GrowthTimeline;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface GrowthTimelineRepository extends JpaRepository<GrowthTimeline, Long> {

    List<GrowthTimeline> findByGrowthSimulationIdOrderByDayAsc(Long growthSimulationId);
}