package com.fastpay.infra.security;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTCreationException;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.fastpay.domain.model.User;
import com.fastpay.domain.port.out.TokenGeneratorPort;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.KeyFactory;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.time.Instant;
import java.util.Base64;
import java.util.Date;

@Slf4j
@Service
public class JwtTokenService implements TokenGeneratorPort {

    @Value("${jwt.issuer:fastpay-api}")
    private String issuer;

    @Value("${jwt.key.private.path}")
    private String privateKeyPath;

    @Value("${jwt.key.public.path}")
    private String publicKeyPath;

    private RSAPrivateKey privateKey;
    private RSAPublicKey publicKey;

    @PostConstruct
    public void loadKeys() {
        try {
            byte[] privateKeyBytes = loadKeyBytes(privateKeyPath);
            PKCS8EncodedKeySpec keySpecPKCS8 = new PKCS8EncodedKeySpec(privateKeyBytes);
            KeyFactory keyFactory = KeyFactory.getInstance("RSA");
            this.privateKey = (RSAPrivateKey) keyFactory.generatePrivate(keySpecPKCS8);

            byte[] publicKeyBytes = loadKeyBytes(publicKeyPath);
            X509EncodedKeySpec keySpecX509 = new X509EncodedKeySpec(publicKeyBytes);
            this.publicKey = (RSAPublicKey) keyFactory.generatePublic(keySpecX509);

            log.info("RSA keys loaded successfully.");
        } catch (Exception e) {
            log.error("Failed to load RSA keys", e);
            throw new RuntimeException("Failed to load RSA keys", e);
        }
    }

    private byte[] loadKeyBytes(String path) throws Exception {
        String content;
        if (path.startsWith("classpath:")) {
            InputStream is = getClass().getResourceAsStream("/" + path.substring(10));
            content = new String(is.readAllBytes(), StandardCharsets.UTF_8);
        } else {
            content = new String(Files.readAllBytes(Paths.get(path)));
        }

        String key = content
                .replaceAll("\\n", "")
                .replaceAll("-----(BEGIN|END) (PRIVATE|PUBLIC) KEY-----", "");

        return Base64.getDecoder().decode(key);
    }

    @Override
    public String generateToken(User user) {
        try {
            Algorithm algorithm = Algorithm.RSA256(publicKey, privateKey);
            Instant now = Instant.now();
            long expirationSeconds = 3600L * 24;

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