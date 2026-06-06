package com.mz.user_service.services;

import com.mz.user_service.dto.AuthResponse;
import com.mz.user_service.dto.UserLoginRequest;
import com.mz.user_service.dto.UserRegisterRequest;
import com.mz.user_service.entities.User;
import com.mz.user_service.exceptions.ConflictException;
import com.mz.user_service.repositories.UserRepository;
import org.finos.fluxnova.bpm.engine.RuntimeService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.Map;

@Service
public class UserServiceImpl implements UserService {
    private static final Logger log = LoggerFactory.getLogger(UserServiceImpl.class);

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final RuntimeService runtimeService;

    public UserServiceImpl(UserRepository userRepository,
                           PasswordEncoder passwordEncoder,
                           JwtService jwtService,
                           RuntimeService runtimeService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
        this.runtimeService = runtimeService;
    }

    @Override
    @Transactional
    public User registerUser(UserRegisterRequest request) {
        log.info("Attempting to register user: {}", request.getUsername());

        // Validate uniqueness of username and email
        if (userRepository.existsByUsername(request.getUsername())) {
            log.warn("Registration failed. Username already exists: {}", request.getUsername());
            throw new ConflictException("Username is already taken");
        }

        if (userRepository.existsByEmail(request.getEmail())) {
            log.warn("Registration failed. Email already exists: {}", request.getEmail());
            throw new ConflictException("Email is already registered");
        }

        // Encrypt password
        String passwordHash = passwordEncoder.encode(request.getPassword());

        // Save User
        User user = new User(request.getUsername(), request.getEmail(), passwordHash);
        User savedUser = userRepository.save(user);

        log.info("User persisted successfully. ID: {}", savedUser.getId());

        // Trigger Fluxnova BPM workflow
        try {
            Map<String, Object> variables = new HashMap<>();
            variables.put("username", savedUser.getUsername());
            variables.put("email", savedUser.getEmail());
            variables.put("userId", savedUser.getId());

            log.info("Starting BPM workflow 'user-registration-process' for user: {}", savedUser.getUsername());
            runtimeService.startProcessInstanceByKey("user-registration-process", variables);
        } catch (Exception e) {
            log.error("Failed to start BPM user-registration-process workflow", e);
            // We do not roll back registration if workflow fails, or do we?
            // In microservices, we can log and alert, or let the transaction continue.
        }

        return savedUser;
    }

    @Override
    @Transactional(readOnly = true)
    public AuthResponse loginUser(UserLoginRequest request) {
        log.info("Attempting login for: {}", request.getUsernameOrEmail());

        // Look up user by username or email
        User user = userRepository.findByUsername(request.getUsernameOrEmail())
                .or(() -> userRepository.findByEmail(request.getUsernameOrEmail()))
                .orElseThrow(() -> {
                    log.warn("Authentication failed. User not found: {}", request.getUsernameOrEmail());
                    return new BadCredentialsException("Invalid username or password");
                });

        // Verify password
        if (!passwordEncoder.matches(request.getPassword(), user.getPasswordHash())) {
            log.warn("Authentication failed. Password mismatch for user: {}", user.getUsername());
            throw new BadCredentialsException("Invalid username or password");
        }

        // Generate Token
        String token = jwtService.generateToken(user);
        log.info("User authenticated successfully. JWT token generated for user: {}", user.getUsername());

        return new AuthResponse(token, user.getUsername(), user.getEmail());
    }
}
