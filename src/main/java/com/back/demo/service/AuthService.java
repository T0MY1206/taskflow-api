package com.back.demo.service;

import com.back.demo.dto.LoginRequest;
import com.back.demo.dto.LoginResponse;
import com.back.demo.security.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;

    public LoginResponse login(LoginRequest request) {
        Authentication auth = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword()));
        String token = jwtService.generateToken((org.springframework.security.core.userdetails.UserDetails) auth.getPrincipal());
        return LoginResponse.builder()
                .token(token)
                .type("Bearer")
                .username(request.getUsername())
                .build();
    }
}
