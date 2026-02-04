package com.back.demo.config;

import com.back.demo.model.User;
import com.back.demo.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.Set;

@Component
@RequiredArgsConstructor
public class DataLoader implements CommandLineRunner {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {
        if (userRepository.count() == 0) {
            userRepository.save(User.builder()
                    .username("user")
                    .password(passwordEncoder.encode("user123"))
                    .roles(Set.of("ROLE_USER"))
                    .build());
            userRepository.save(User.builder()
                    .username("admin")
                    .password(passwordEncoder.encode("admin123"))
                    .roles(Set.of("ROLE_USER", "ROLE_ADMIN"))
                    .build());
        }
    }
}
