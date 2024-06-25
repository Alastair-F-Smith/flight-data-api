package com.example.afs.flightdataapi.controllers;

import com.example.afs.flightdataapi.services.TokenService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Log in", description = "Log in to obtain an access token")
@SecurityRequirement(name = "basicAuth")
@RestController
public class AuthController {

    private final TokenService tokenService;
    private final Logger logger = LoggerFactory.getLogger(AuthController.class);

    public AuthController(TokenService tokenService) {
        this.tokenService = tokenService;
    }

    @Operation(summary = "Sign in to obtain an access token")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Authenticated",
                    content = { @Content}),
            @ApiResponse(responseCode = "401", description = "Not authenticated",
                    content = { @Content})
    })
    @PostMapping("/api/token")
    public String requestToken(Authentication auth) {
        logger.debug("Requesting token for {}", auth.getName());
        String token = tokenService.generateToken(auth);
        logger.debug("Token generated: {}", token);
        return token;
    }
}
