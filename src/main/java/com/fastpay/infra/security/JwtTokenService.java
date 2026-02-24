package com.fastpay.infra.security;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTCreationException;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.fastpay.domain.model.User;
import com.fastpay.domain.port.out.TokenGeneratorPort;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.time.Instant;
import java.util.Date;

@Slf4j
@Service
@RequiredArgsConstructor
public class JwtTokenService implements TokenGeneratorPort {

    private final RSAPublicKey publicKey;
    private final RSAPrivateKey privateKey;

    @Value("${jwt.issuer:fastpay-api}")
    private String issuer;

    @Value("${jwt.expiration.seconds}")
    private Long expirationSeconds;

    @Override
    public String generateToken(User user) {
        try {
            Algorithm algorithm = Algorithm.RSA256(publicKey, privateKey);
            Instant now = Instant.now();

            return JWT.create()
                    .withIssuer(issuer)
                    .withSubject(user.getEmail())
                    .withClaim("userId", user.getId().toString())
                    .withClaim("userName", user.getName())
                    .withIssuedAt(Date.from(now))
                    .withExpiresAt(Date.from(now.plusSeconds(expirationSeconds)))
                    .sign(algorithm);
        } catch (JWTCreationException e) {
            log.error("Error generating JWT token for email {}: {}", user.getEmail(), e.getMessage());
            throw new RuntimeException("Error generating JWT token", e);
        }
    }

    public String validateTokenAndGetEmail(String token) {
        try {
            Algorithm algorithm = Algorithm.RSA256(publicKey, null);

            return JWT.require(algorithm)
                    .withIssuer(issuer)
                    .build()
                    .verify(token)
                    .getSubject();
        } catch (JWTVerificationException e) {
            log.warn("Invalid or expired JWT token: {}", e.getMessage());
            return null;
        }
    }
}