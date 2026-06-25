package com.yusolbin.bio_os.controller;

import com.yusolbin.bio_os.dto.AiPredictionRequest;
import com.yusolbin.bio_os.dto.AiPredictionResponse;
import com.yusolbin.bio_os.security.CurrentUserService;
import com.yusolbin.bio_os.service.AiPredictionService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/ai")
@CrossOrigin(origins = "*")
public class AiPredictionController {

    private final AiPredictionService aiPredictionService;
    private final CurrentUserService currentUserService;

    public AiPredictionController(
            AiPredictionService aiPredictionService,
            CurrentUserService currentUserService
    ) {
        this.aiPredictionService = aiPredictionService;
        this.currentUserService = currentUserService;
    }

    @PostMapping("/predict")
    public AiPredictionResponse predict(@RequestBody AiPredictionRequest request) {
        currentUserService.getCurrentUserId();

        return aiPredictionService.predict(request);
    }
}