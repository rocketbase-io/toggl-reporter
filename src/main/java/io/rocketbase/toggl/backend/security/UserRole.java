package io.rocketbase.toggl.backend.security;

import org.springframework.security.core.GrantedAuthority;

public enum UserRole implements GrantedAuthority {

    ROLE_USER,
    ROLE_ADMIN;

    public String getAuthority() {
        return name();
    }

}
