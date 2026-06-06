package com.mz.user_service.services;

import com.mz.user_service.dto.AuthResponse;
import com.mz.user_service.dto.UserLoginRequest;
import com.mz.user_service.dto.UserRegisterRequest;
import com.mz.user_service.entities.User;

public interface UserService {
    User registerUser(UserRegisterRequest request);
    AuthResponse loginUser(UserLoginRequest request);
}
