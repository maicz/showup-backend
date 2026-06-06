package com.mz.user_service.controllers;

import com.auth0.jwt.interfaces.DecodedJWT;
import com.mz.user_service.dto.AuthResponse;
import com.mz.user_service.dto.UserLoginRequest;
import com.mz.user_service.dto.UserRegisterRequest;
import com.mz.user_service.entities.User;
import com.mz.user_service.services.JwtService;
import com.mz.user_service.services.UserService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/users")
public class UserController {
    private static final Logger log = LoggerFactory.getLogger(UserController.class);

    private final UserService userService;
    private final JwtService jwtService;

    public UserController(UserService userService, JwtService jwtService) {
        this.userService = userService;
        this.jwtService = jwtService;
    }

    @PostMapping("/register")
    public ResponseEntity<Map<String, Object>> registerUser(@Valid @RequestBody UserRegisterRequest request) {
        log.info("REST request to register user: {}", request.getUsername());
        User registeredUser = userService.registerUser(request);
        
        Map<String, Object> response = new HashMap<>();
        response.put("message", "User registered successfully");
        response.put("userId", registeredUser.getId());
        response.put("username", registeredUser.getUsername());
        response.put("email", registeredUser.getEmail());
        
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> loginUser(@Valid @RequestBody UserLoginRequest request) {
        log.info("REST request to login user: {}", request.getUsernameOrEmail());
        AuthResponse response = userService.loginUser(request);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/validate")
    public ResponseEntity<Map<String, Object>> validateToken(@RequestParam("token") String token) {
        log.info("REST request to validate token");
        DecodedJWT decodedJWT = jwtService.validateToken(token);
        
        Map<String, Object> response = new HashMap<>();
        response.put("valid", true);
        response.put("userId", decodedJWT.getClaim("userId").asLong());
        response.put("username", decodedJWT.getSubject());
        response.put("email", decodedJWT.getClaim("email").asString());
        
        return ResponseEntity.ok(response);
    }
}
