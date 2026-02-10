package com.back.demo.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Propiedades de JWT enlazadas a {@code app.jwt.*}.
 * Configuración tipada con {@link ConfigurationProperties}.
 */
@ConfigurationProperties(prefix = "app.jwt")
public record JwtProperties(String secret, long expirationMs) {

    public static final String DEFAULT_SECRET = "my-secret-key-at-least-256-bits-for-hs256-algorithm-please-change-in-production";
    public static final long DEFAULT_EXPIRATION_MS = 86_400_000L; // 24 h

    public String secret() {
        return (secret != null && !secret.isBlank()) ? secret : DEFAULT_SECRET;
    }

    public long expirationMs() {
        return expirationMs > 0 ? expirationMs : DEFAULT_EXPIRATION_MS;
    }
}
