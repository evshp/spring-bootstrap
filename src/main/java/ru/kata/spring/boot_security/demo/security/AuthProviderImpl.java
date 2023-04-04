package ru.kata.spring.boot_security.demo.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import ru.kata.spring.boot_security.demo.service.UserServiceJPA;

import java.util.Collections;


@Component
public class AuthProviderImpl implements AuthenticationProvider {

    private final UserServiceJPA userServiceJPA;

    @Autowired
    public AuthProviderImpl(UserServiceJPA userServiceJPA) {
        this.userServiceJPA = userServiceJPA;
    }

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        String username = authentication.getName();
        UserDetails userDetails = userServiceJPA.loadUserByUsername(username);
        String password = authentication.getCredentials().toString();
        System.out.println(password);
        if (!password.equals(userDetails.getPassword())) {
            throw new BadCredentialsException("Неверный пароль");
        }

        return new UsernamePasswordAuthenticationToken(userDetails, password, Collections.emptyList());

    }

    @Override
    public boolean supports(Class<?> authentication) {
        return true;
    }
}
