package org.example.utils;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.auth.User;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

public class JwtUtil {

    // Secure 256-bit secret key (Base64-encoded)
    private static final String SECRET = "Q3r6v0D6FY6+RzWNEUGvI5tBbUrM8nEoO3X3c6rRXy4=";
    private static final SecretKey SECRET_KEY = Keys.hmacShaKeyFor(SECRET.getBytes(StandardCharsets.UTF_8));

    private static final long EXPIRATION_TIME = 86400000; // 1 day

    public static String generateToken(String email) {
        return Jwts.builder()
                .setSubject(email)
                .setExpiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME))
                .signWith(SECRET_KEY)
                .compact();
    }

    public static boolean verifyToken(String token) {
        try {
            Jwts.parserBuilder().setSigningKey(SECRET_KEY).build().parseClaimsJws(token);
            return true;
        } catch (JwtException e) {
            return false;
        }
    }

    public static String extractEmail(String token) {
        Claims claims = Jwts.parserBuilder().setSigningKey(SECRET_KEY).build().parseClaimsJws(token).getBody();
        return claims.getSubject();
    }

    public static User extractUser(String token) {
        Claims claims = Jwts.parserBuilder().setSigningKey(SECRET_KEY).build().parseClaimsJws(token).getBody();
        String email = claims.getSubject();
        JsonObject principal = new JsonObject().put("email", email).put("token", token);
        return User.create(principal);
    }

    public static boolean isTokenExpiringSoon(String token) {
        Claims claims = Jwts.parserBuilder().setSigningKey(SECRET_KEY).build().parseClaimsJws(token).getBody();
        long exp = claims.getExpiration().getTime();
        return exp - System.currentTimeMillis() < 2 * 60 * 1000; // less than 2 minutes left
    }
}
