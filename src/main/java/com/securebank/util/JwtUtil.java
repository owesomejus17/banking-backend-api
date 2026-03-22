package com.securebank.util;

import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

/**
 * Utility for creating and validating JSON Web Tokens.
 *
 * Token structure:
 *  - Subject: user email
 *  - Claim "role": user role (USER / ADMIN)
 *  - Expiry: configurable via jwt.expiration (default 24 hours)
 *
 * Algorithm: HMAC-SHA256 (HS256)
 * Secret key length: minimum 256 bits (32 chars), loaded from application.properties.
 */
@Component
public class JwtUtil {

    private static final Logger log = LoggerFactory.getLogger(JwtUtil.class);

    @Value("${jwt.secret}")
    private String secret;

    @Value("${jwt.expiration}")
    private long expirationMs;

    // ───────── Token Generation ─────────

    /**
     * Generates a JWT token for an authenticated user.
     *
     * @param userDetails Spring Security user object (email as username)
     * @param role        the user's role to embed in claims
     * @return signed JWT string
     */
    public String generateToken(UserDetails userDetails, String role) {
        Map<String, Object> extraClaims = new HashMap<>();
        extraClaims.put("role", role);
        return buildToken(extraClaims, userDetails.getUsername());
    }

    private String buildToken(Map<String, Object> extraClaims, String subject) {
        return Jwts.builder()
                .setClaims(extraClaims)
                .setSubject(subject)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + expirationMs))
                .signWith(getSigningKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    // ───────── Token Validation ─────────

    /**
     * Validates the token against a loaded UserDetails object.
     * Checks: subject matches email + token is not expired.
     *
     * @param token       the JWT string (without "Bearer " prefix)
     * @param userDetails the UserDetails to validate against
     * @return true if valid
     */
    public boolean isTokenValid(String token, UserDetails userDetails) {
        final String email = extractEmail(token);
        return email.equals(userDetails.getUsername()) && !isTokenExpired(token);
    }

    private boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    // ───────── Claim Extraction ─────────

    /**
     * Extracts the user's email (subject) from the token.
     */
    public String extractEmail(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    /**
     * Extracts the user's role from custom claims.
     */
    public String extractRole(String token) {
        return extractClaim(token, claims -> claims.get("role", String.class));
    }

    private Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    private Claims extractAllClaims(String token) {
        // parserBuilder() is the non-deprecated API in jjwt 0.11.x
        return Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    // ───────── Key Helper ─────────

    /**
     * Derives the HMAC-SHA256 signing key from the configured secret.
     * The secret is treated as UTF-8 bytes, then Base64-decoded if necessary.
     * For simplicity, we use the raw bytes from the secret string.
     */
    private Key getSigningKey() {
        byte[] keyBytes = secret.getBytes();
        return Keys.hmacShaKeyFor(keyBytes);
    }
}
