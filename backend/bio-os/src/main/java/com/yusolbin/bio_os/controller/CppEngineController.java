package com.yusolbin.bio_os.controller;

import com.yusolbin.bio_os.dto.CppEngineResult;
import com.yusolbin.bio_os.service.CppEngineBridgeService;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/engine/cpp")
public class CppEngineController {
    
    private final CppEngineBridgeService cppEngineBridgeService;

    public CppEngineController(CppEngineBridgeService cppEngineBridgeService) {
        this.cppEngineBridgeService = cppEngineBridgeService;
    }

    @GetMapping("/run")
    public CppEngineResult runCppEngine(
        @RequestParam double water,
        @RequestParam double light,
        @RequestParam double temperature,
        @RequestParam(defaultValue = "50") double humidity
    ) {
        return cppEngineBridgeService.runEngine(water, light, temperature, humidity);
    }
    
}
