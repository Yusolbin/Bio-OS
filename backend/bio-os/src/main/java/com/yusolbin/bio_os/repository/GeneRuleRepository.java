package com.yusolbin.bio_os.repository;

import com.yusolbin.bio_os.model.GeneRule;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface GeneRuleRepository extends JpaRepository<GeneRule, Long> {
    
    List<GeneRule> findAllByOrderByIdDesc();

    List<GeneRule> findByActiveTrue();

    boolean existsByFieldNameAndOperatorAndThresholdAndTargetState(
        String fieldName,
        String operator,
        double threshold,
        String targetState
    );
}
