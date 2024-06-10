package com.example.afs.flightdataapi.controllers;

import com.example.afs.flightdataapi.services.TokenService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class AuthController {

    private final TokenService tokenService;
    private final Logger logger = LoggerFactory.getLogger(AuthController.class);

    public AuthController(TokenService tokenService) {
        this.tokenService = tokenService;
    }

    @PostMapping("/api/token")
    public String requestToken(Authentication auth) {
        logger.debug("Requesting token for {}", auth.getName());
        String token = tokenService.generateToken(auth);
        logger.debug("Token generated: {}", token);
        return token;
    }
}
