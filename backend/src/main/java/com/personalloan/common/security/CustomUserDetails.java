package com.personalloan.common.security;

import com.personalloan.module.auth.internal.entity.User;
import com.personalloan.module.auth.internal.entity.Role;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Collections;

public class CustomUserDetails implements UserDetails {

    private final User user;

    public CustomUserDetails(User user) {
        this.user = user;
    }

    public CustomUserDetails(Long userId, String email, String passwordHash, Collection<? extends GrantedAuthority> authorities, String status) {
        String roleCode = "CUSTOMER";
        if (authorities != null && !authorities.isEmpty()) {
            String authStr = authorities.iterator().next().getAuthority();
            if (authStr.startsWith("ROLE_")) {
                roleCode = authStr.substring(5);
            } else {
                roleCode = authStr;
            }
        }
        this.user = User.builder()
                .userId(userId)
                .email(email)
                .passwordHash(passwordHash)
                .status(status)
                .role(Role.builder().roleCode(roleCode).build())
                .build();
    }

    public User getUser() {
        return user;
    }

    public Long getUserId() {
        return user.getUserId();
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        if (user.getRole() == null) {
            return Collections.emptyList();
        }
        // Prefix with ROLE_ to support standard Spring Security pre-authorized checks
        return Collections.singletonList(new SimpleGrantedAuthority("ROLE_" + user.getRole().getRoleCode()));
    }

    @Override
    public String getPassword() {
        return user.getPasswordHash();
    }

    @Override
    public String getUsername() {
        return user.getEmail();
    }

    @Override
    public boolean isAccountNonExpired() {
        return !user.getIsDeleted();
    }

    @Override
    public boolean isAccountNonLocked() {
        return !"LOCKED".equalsIgnoreCase(user.getStatus()) && !"SUSPENDED".equalsIgnoreCase(user.getStatus());
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return "ACTIVE".equalsIgnoreCase(user.getStatus());
    }
}
