package com.yusolbin.bio_os.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HomeController {

    @GetMapping("/")
    public String home() {
        return "BIO-OS Spring Boot 서버 실행 성공!";
    }
}