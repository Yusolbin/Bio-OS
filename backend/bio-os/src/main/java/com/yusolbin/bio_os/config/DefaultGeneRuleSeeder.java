package com.yusolbin.bio_os.config;

import com.yusolbin.bio_os.model.GeneRule;
import com.yusolbin.bio_os.repository.GeneRuleRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class DefaultGeneRuleSeeder implements CommandLineRunner {

    private final GeneRuleRepository geneRuleRepository;

    public DefaultGeneRuleSeeder(GeneRuleRepository geneRuleRepository) {
        this.geneRuleRepository = geneRuleRepository;
    }

    @Override
    public void run(String... args) {
        createDefaultRuleIfMissing("Water", "LT", 30, "DroughtMode");
        createDefaultRuleIfMissing("Light", "GT", 70, "PhotosynthesisBoost");
        createDefaultRuleIfMissing("Temperature", "GT", 35, "HeatStress");
        createDefaultRuleIfMissing("Water", "LT", 10, "PruningMode");
        createDefaultRuleIfMissing("Water", "GT", 100, "RecoveryMode");
    }

    private void createDefaultRuleIfMissing(
            String fieldName,
            String operator,
            double threshold,
            String targetState
    ) {
        boolean exists = geneRuleRepository
                .existsByFieldNameAndOperatorAndThresholdAndTargetState(
                        fieldName,
                        operator,
                        threshold,
                        targetState
                );

        if (!exists) {
            GeneRule geneRule = new GeneRule(
                    fieldName,
                    operator,
                    threshold,
                    targetState
            );

            geneRuleRepository.save(geneRule);
        }
    }
}