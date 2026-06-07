package com.mz.group_service.security;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class UserPrincipal {
    private final Long id;
    private final String username;
    private final String email;
}
