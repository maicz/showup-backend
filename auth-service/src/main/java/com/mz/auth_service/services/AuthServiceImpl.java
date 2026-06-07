package com.mz.auth_service.services;

import com.mz.auth_service.dto.AuthResponse;
import com.mz.auth_service.dto.UserLoginRequest;
import com.mz.auth_service.entities.User;
import com.mz.auth_service.repositories.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {
    private static final Logger log = LoggerFactory.getLogger(AuthServiceImpl.class);

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    @Override
    @Transactional(readOnly = true)
    public AuthResponse login(UserLoginRequest request) {
        log.info("Attempting login in auth-service for user/email: {}", request.getUsernameOrEmail());

        User user = userRepository.findByUsername(request.getUsernameOrEmail())
                .or(() -> userRepository.findByEmail(request.getUsernameOrEmail()))
                .orElseThrow(() -> {
                    log.warn("Login failed. User not found: {}", request.getUsernameOrEmail());
                    return new BadCredentialsException("Invalid username or password");
                });

        if (!passwordEncoder.matches(request.getPassword(), user.getPasswordHash())) {
            log.warn("Login failed. Password mismatch for user: {}", user.getUsername());
            throw new BadCredentialsException("Invalid username or password");
        }

        String token = jwtService.generateToken(user);
        log.info("JWT token generated successfully for user: {}", user.getUsername());

        return new AuthResponse(token, user.getUsername(), user.getEmail());
    }
}
