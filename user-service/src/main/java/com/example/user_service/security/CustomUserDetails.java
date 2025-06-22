package com.example.user_service.security;

import com.example.user_service.model.User;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

public class CustomUserDetails implements UserDetails {
    
    private final User user;
    
    public CustomUserDetails(User user) {
        this.user = user;
    }
    
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority("ROLE_" + user.getRole()));
    }
    
    @Override
    public String getPassword() {
        return user.getPassword();
    }
    
    @Override
    public String getUsername() {
        return user.getEmail(); // Using email as username for login
    }
    
    @Override
    public boolean isAccountNonExpired() {
        return user.getIsActive();
    }
    
    @Override
    public boolean isAccountNonLocked() {
        return user.isAccountNonLocked();
    }
    
    @Override
    public boolean isCredentialsNonExpired() {
        return true; // Can implement password expiration logic here
    }
    
    @Override
    public boolean isEnabled() {
        return user.getIsActive() && !user.getDeleted();
    }
    
    // Getter for the User entity
    public User getUser() {
        return user;
    }
    
    public Long getUserId() {
        return user.getUserId();
    }
    
    public String getEmail() {
        return user.getEmail();
    }
    
    public String getRole() {
        return user.getRole();
    }
    
    public String getUserName() {
        return user.getUserName();
    }
} 