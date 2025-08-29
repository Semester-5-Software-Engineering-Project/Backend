package com.tutoring.Tutorverse.Services;

import com.tutoring.Tutorverse.Model.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.oauth2.jwt.JwtException;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Component
public class JwtServices {

    private static final Logger logger = LoggerFactory.getLogger(JwtServices.class);

    // Strong secret key for HS512 - must be at least 64 bytes (512 bits)
    private final String jwtSecret = "ThisIsAVerySecureSecretKeyForHS512AlgorithmThatIsAtLeast64BytesLongAndShouldBeKeptSecretInProduction123456789";
    private final long jwtExpirationMs = 86400000; // 1 day

    private Key getSigningKey() {
        return Keys.hmacShaKeyFor(jwtSecret.getBytes());
    }

    public String generateJwtToken(User user){
        logger.info("=== JWT TOKEN GENERATION STARTED ===");
        logger.info("Generating JWT token for user: ID={}, Email={}", user.getId(), user.getEmail());

        Map<String,Object> claims = new HashMap<>();
        claims.put("userId",user.getId());
        claims.put("roles",user.getRole().getId()); // Use role name instead of enum

        logger.info("Claims added - userId: {}, roles: {}", user.getId(), user.getRole().getId());

        String token = Jwts.builder()
                .setClaims(claims)
                .setSubject(user.getEmail())
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + jwtExpirationMs))
                .signWith(getSigningKey(), SignatureAlgorithm.HS512)
                .compact();

        logger.info("JWT token generated successfully (length: {} chars)", token.length());
        logger.info("=== JWT TOKEN GENERATION COMPLETED ===");
        return token;
    }

    public boolean validateJwtToken(String authToken){
        logger.info("=== JWT TOKEN VALIDATION STARTED ===");
        logger.info("Validating token (length: {} chars)", authToken.length());

        try {
            Claims claims = Jwts.parserBuilder()
                    .setSigningKey(getSigningKey())
                    .build()
                    .parseClaimsJws(authToken)
                    .getBody();

            logger.info("Token validation successful");
            logger.info("Token subject: {}", claims.getSubject());
            logger.info("Token expiration: {}", claims.getExpiration());
            logger.info("=== JWT TOKEN VALIDATION COMPLETED - VALID ===");
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            logger.error("=== JWT TOKEN VALIDATION FAILED ===");
            logger.error("Validation error: {}", e.getMessage());
            logger.error("Exception type: {}", e.getClass().getSimpleName());
        }
        return false;
    }

    public String getEmailFromJwtToken(String token) {
        logger.info("=== EXTRACTING EMAIL FROM JWT TOKEN ===");
        logger.info("Token length: {} chars", token.length());

        try {
            String email = Jwts.parserBuilder()
                    .setSigningKey(getSigningKey())
                    .build()
                    .parseClaimsJws(token)
                    .getBody()
                    .getSubject();

            logger.info("Email extracted successfully: {}", email);
            logger.info("=== EMAIL EXTRACTION COMPLETED ===");
            return email;
        } catch (Exception e) {
            logger.error("=== EMAIL EXTRACTION FAILED ===");
            logger.error("Error extracting email: {}", e.getMessage());
            throw e;
        }
    }

    public Long getIdFromJwtToken(String token) {
        logger.info("=== EXTRACTING USER ID FROM JWT TOKEN ===");
        logger.info("Token length: {} chars", token.length());
        logger.info("Token preview: {}...{}",
                   token.substring(0, Math.min(20, token.length())),
                   token.substring(Math.max(0, token.length() - 10)));

        try {
            Claims claims = Jwts.parserBuilder()
                    .setSigningKey(getSigningKey())
                    .build()
                    .parseClaimsJws(token)
                    .getBody();

            Long userId = claims.get("userId", Long.class);
            logger.info("User ID extracted successfully: {}", userId);
            logger.info("Token subject (email): {}", claims.getSubject());
            logger.info("Token issued at: {}", claims.getIssuedAt());
            logger.info("Token expires at: {}", claims.getExpiration());
            logger.info("=== USER ID EXTRACTION COMPLETED ===");

            return userId;
        } catch (Exception e) {
            logger.error("=== USER ID EXTRACTION FAILED ===");
            logger.error("Error extracting user ID: {}", e.getMessage());
            logger.error("Exception type: {}", e.getClass().getSimpleName());
            throw e;
        }
    }
}