package ru.kata.spring.boot_security.demo.security;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import ru.kata.spring.boot_security.demo.models.User;

import java.util.Collection;


public class UserPrincipal implements UserDetails {

    private final User UserPrincipal;


    public UserPrincipal(User UserPrincipal) {
        this.UserPrincipal = UserPrincipal;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return UserPrincipal.getRoles();
    }

    @Override
    public String getPassword() {
        return this.UserPrincipal.getPassword();
    }

    @Override
    public String getUsername() {
        return this.UserPrincipal.getName();
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    public User getUserDetails() {
        return this.UserPrincipal;
    }

}
