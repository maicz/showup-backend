package com.mz.auth_service.services;

import com.mz.auth_service.dto.AuthResponse;
import com.mz.auth_service.dto.UserLoginRequest;

public interface AuthService {
    AuthResponse login(UserLoginRequest request);
}
