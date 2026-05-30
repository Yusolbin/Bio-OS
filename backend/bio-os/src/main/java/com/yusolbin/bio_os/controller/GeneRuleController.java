package com.yusolbin.bio_os.controller;

import com.yusolbin.bio_os.dto.GeneRuleRequest;
import com.yusolbin.bio_os.dto.GeneRuleResponse;
import com.yusolbin.bio_os.service.GeneRuleService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/rules")
@CrossOrigin(origins = "*")
public class GeneRuleController {

    private final GeneRuleService geneRuleService;

    public GeneRuleController(GeneRuleService geneRuleService) {
        this.geneRuleService = geneRuleService;
    }

    @PostMapping
    public GeneRuleResponse createRule(@RequestBody GeneRuleRequest request) {
        return geneRuleService.createRule(request);
    }

    @GetMapping
    public List<GeneRuleResponse> getRules() {
        return geneRuleService.getRules();
    }
}