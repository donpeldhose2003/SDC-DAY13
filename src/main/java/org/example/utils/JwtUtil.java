package org.example.utils;

import io.jsonwebtoken.*;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.auth.User;

import java.util.Date;

public class JwtUtil {

    private static final String SECRET = "tS2F!9$hk@3^bQ8l#WzX&7uM0rLpVcKd";
    private static final long EXPIRATION_TIME = 86400000; // 1 day

    public static String generateToken(String email) {
        return Jwts.builder()
                .setSubject(email)
                .setExpiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME))
                .signWith(SignatureAlgorithm.HS256, SECRET)
                .compact();
    }

    public static boolean verifyToken(String token) {
        try {
            Jwts.parser().setSigningKey(SECRET).parseClaimsJws(token);
            return true;
        } catch (JwtException e) {
            return false;
        }
    }

    // Extract email only
    public static String extractEmail(String token) {
        Claims claims = Jwts.parser().setSigningKey(SECRET).parseClaimsJws(token).getBody();
        return claims.getSubject();
    }

    // Extract full User
    public static User extractUser(String token) {
        Claims claims = Jwts.parser().setSigningKey(SECRET).parseClaimsJws(token).getBody();
        String email = claims.getSubject();
        JsonObject principal = new JsonObject().put("email", email).put("token", token);
        return User.create(principal);
    }
}
