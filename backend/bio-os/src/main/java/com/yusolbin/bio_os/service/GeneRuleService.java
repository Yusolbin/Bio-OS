package com.yusolbin.bio_os.service;

import com.yusolbin.bio_os.dto.GeneRuleRequest;
import com.yusolbin.bio_os.dto.GeneRuleResponse;
import com.yusolbin.bio_os.model.GeneRule;
import com.yusolbin.bio_os.repository.GeneRuleRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class GeneRuleService {

    private final GeneRuleRepository geneRuleRepository;

    public GeneRuleService(GeneRuleRepository geneRuleRepository) {
        this.geneRuleRepository = geneRuleRepository;
    }

    public GeneRuleResponse createRule(GeneRuleRequest request) {
        GeneRule geneRule = new GeneRule(
                request.getFieldName(),
                request.getOperator(),
                request.getThreshold(),
                request.getTargetState()
        );

        GeneRule savedRule = geneRuleRepository.save(geneRule);

        return new GeneRuleResponse(savedRule);
    }

    public List<GeneRuleResponse> getRules() {
        return geneRuleRepository.findAllByOrderByIdDesc()
                .stream()
                .map(GeneRuleResponse::new)
                .toList();
    }
}