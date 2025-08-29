package com.tutoring.Tutorverse.Services;

import com.tutoring.Tutorverse.Model.User;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Service
public class MeetingService {

    private static final Logger logger = LoggerFactory.getLogger(MeetingService.class);

    public String generateToken(User user) {
        logger.info("=== MEETING TOKEN GENERATION STARTED ===");
        logger.info("Generating token for user: ID={}, Name={}, Email={}",
                   user.getId(), user.getName(), user.getEmail());

        String appId = "mydeploy1";
        String appSecret = "wEoG/Y5keRbH4yrjMe7UxWiBzqO8a8VRqY8cVR4oXro=";

        logger.info("Step 1: Determining user role and moderator status");
        boolean isModerator = user.getRole().getId() == 2;
        logger.info("User role: {} (ID: {}), Is Moderator: {}",
                   user.getRole().getName(), user.getRole().getId(), isModerator);

        long nowMillis = System.currentTimeMillis();
        long expMillis = nowMillis + 36000_000; // 1 hour
        Date expirationDate = new Date(expMillis);
        logger.info("Step 2: Token expiration set to: {}", expirationDate);

        // Build context.user
        logger.info("Step 3: Building user context for JWT");
        Map<String, Object> userContext = new HashMap<>();
        userContext.put("name", user.getName());
        userContext.put("email", user.getEmail());
        logger.info("User context created with name: {} and email: {}", user.getName(), user.getEmail());

        Map<String, Object> context = new HashMap<>();
        context.put("user", userContext);

        logger.info("Step 4: Preparing JWT signing key");
        byte[] keyBytes = appSecret.getBytes(StandardCharsets.UTF_8);
        logger.info("Signing key prepared (length: {} bytes)", keyBytes.length);

        logger.info("Step 5: Building JWT token with Jitsi configuration");
        logger.info("App ID: {}", appId);
        logger.info("Subject: jit.shancloudservice.com");
        logger.info("Room: * (wildcard)");
        logger.info("Moderator: {}", isModerator);

        // Build JWT token
        String token = Jwts.builder()
                .setHeaderParam("typ", "JWT")
                .setAudience("jitsi")
                .setIssuer(appId)
                .setSubject("jit.shancloudservice.com")
                .claim("room", "*") //ace * with actual room name
                .claim("moderator", isModerator) // only true for TEACHER
                .setExpiration(expirationDate)
                .claim("context", context)
                .signWith(Keys.hmacShaKeyFor(keyBytes), SignatureAlgorithm.HS256)
                .compact();

        logger.info("Step 6: JWT token generated successfully");
        logger.info("Token length: {} characters", token.length());
        logger.info("Token preview: {}...{}",
                   token.substring(0, Math.min(30, token.length())),
                   token.substring(Math.max(0, token.length() - 10)));
        logger.info("=== MEETING TOKEN GENERATION COMPLETED ===");

        return token;
    }
}
