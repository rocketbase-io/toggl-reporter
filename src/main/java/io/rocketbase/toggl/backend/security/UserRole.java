package io.rocketbase.toggl.backend.security;

import org.springframework.security.core.GrantedAuthority;

public enum UserRole implements GrantedAuthority {

    ROLE_ADMIN,
    ROLE_USER;

    public String getAuthority() {
        return name();
    }

}
