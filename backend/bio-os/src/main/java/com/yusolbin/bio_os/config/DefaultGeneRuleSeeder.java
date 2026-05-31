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
        createDefaultRuleIfMissing("Water", "LT", 30, "DroughtMode", -30);
        createDefaultRuleIfMissing("Light", "GT", 70, "PhotosynthesisBoost", 20);
        createDefaultRuleIfMissing("Temperature", "GT", 35, "HeatStress", -20);
        createDefaultRuleIfMissing("Water", "LT", 10, "PruningMode", -35);
        createDefaultRuleIfMissing("Water", "GT", 100, "RecoveryMode", 25);    
    }

    private void createDefaultRuleIfMissing(
        String fieldName,
        String operator,
        double threshold,
        String targetState,
        double energyEffect
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
                targetState,
                energyEffect
        );

        geneRuleRepository.save(geneRule);

        System.out.println("[BIO-OS] Seeded default rule: IF "
                + fieldName + " " + operator + " " + threshold
                + " THEN " + targetState + " = ON"
                + " / energyEffect=" + energyEffect);
    }
}
}