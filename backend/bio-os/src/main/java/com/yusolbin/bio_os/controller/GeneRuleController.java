package com.yusolbin.bio_os.controller;

import com.yusolbin.bio_os.dto.GeneRuleRequest;
import com.yusolbin.bio_os.dto.GeneRuleResponse;
import com.yusolbin.bio_os.security.CurrentUserService;
import com.yusolbin.bio_os.service.GeneRuleService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequestMapping("/api/rules")
@CrossOrigin(origins = "*")
public class GeneRuleController {

    private final GeneRuleService geneRuleService;
    private final CurrentUserService currentUserService;

    public GeneRuleController(
            GeneRuleService geneRuleService,
            CurrentUserService currentUserService
    ) {
        this.geneRuleService = geneRuleService;
        this.currentUserService = currentUserService;
    }

    @PostMapping
    public GeneRuleResponse createRule(@RequestBody GeneRuleRequest request) {
        requireAdmin();

        return geneRuleService.createRule(request);
    }

    @GetMapping
    public List<GeneRuleResponse> getRules() {
        requireAdmin();

        return geneRuleService.getRules();
    }

    @PatchMapping("/{id}/toggle")
    public GeneRuleResponse toggleRule(@PathVariable Long id) {
        requireAdmin();

        return geneRuleService.toggleRule(id);
    }

    @DeleteMapping("/{id}")
    public void deleteRule(@PathVariable Long id) {
        requireAdmin();

        geneRuleService.deleteRule(id);
    }

    private void requireAdmin() {
        if (!currentUserService.isAdmin()) {
            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN,
                    "ADMIN only"
            );
        }
    }
}