package com.mz.group_service.security;

public class UserPrincipal {
    private final Long id;
    private final String username;
    private final String email;

    public UserPrincipal(Long id, String username, String email) {
        this.id = id;
        this.username = username;
        this.email = email;
    }

    public Long getId() {
        return id;
    }

    public String getUsername() {
        return username;
    }

    public String getEmail() {
        return email;
    }
}
