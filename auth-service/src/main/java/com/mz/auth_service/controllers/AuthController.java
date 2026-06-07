package com.mz.auth_service.controllers;

import com.auth0.jwt.interfaces.DecodedJWT;
import com.mz.auth_service.dto.AuthResponse;
import com.mz.auth_service.dto.UserLoginRequest;
import com.mz.auth_service.services.AuthService;
import com.mz.auth_service.services.JwtService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {
    private static final Logger log = LoggerFactory.getLogger(AuthController.class);

    private final AuthService authService;
    private final JwtService jwtService;

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody UserLoginRequest request) {
        log.info("REST request to login user/email: {}", request.getUsernameOrEmail());
        AuthResponse response = authService.login(request);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/validate")
    public ResponseEntity<Map<String, Object>> validateToken(@RequestParam("token") String token) {
        log.info("REST request to validate JWT token");
        try {
            DecodedJWT jwt = jwtService.validateToken(token);
            Map<String, Object> claims = new HashMap<>();
            claims.put("valid", true);
            claims.put("username", jwt.getSubject());
            claims.put("userId", jwt.getClaim("userId").asLong());
            claims.put("email", jwt.getClaim("email").asString());
            return ResponseEntity.ok(claims);
        } catch (Exception e) {
            log.warn("Token validation failed: {}", e.getMessage());
            Map<String, Object> claims = new HashMap<>();
            claims.put("valid", false);
            return ResponseEntity.badRequest().body(claims);
        }
    }
}
