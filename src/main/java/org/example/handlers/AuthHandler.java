package org.example.handlers;

import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.mongo.MongoClient;
import io.vertx.ext.web.RoutingContext;
import io.vertx.redis.client.RedisAPI;
import org.example.utils.JwtUtil;
import org.example.utils.PasswordUtil;
import org.example.utils.ResetTokenUtil;

import java.util.Arrays;
import java.util.Collections;

public class AuthHandler {

    private final MongoClient mongo;
    private final Vertx vertx;
    private final RedisAPI redis;

    public AuthHandler(MongoClient mongo, Vertx vertx, RedisAPI redis) {
        this.mongo = mongo;
        this.vertx = vertx;
        this.redis = redis;
    }

    // ----------------- Registration -----------------
    public void handleRegister(RoutingContext ctx) {
        JsonObject body = ctx.getBodyAsJson();
        String email = body.getString("email");
        String name = body.getString("name");

        if (email == null || name == null) {
            ctx.response().setStatusCode(400).end("Email and name required");
            return;
        }

        mongo.findOne("users", new JsonObject().put("email", email), null, res -> {
            if (res.succeeded()) {
                if (res.result() != null) {
                    ctx.response().setStatusCode(409).end("Email already registered");
                } else {
                    String rawPassword = PasswordUtil.generateRandomPassword(10);
                    String hashedPassword = PasswordUtil.hashPassword(rawPassword);

                    JsonObject user = new JsonObject()
                            .put("email", email)
                            .put("name", name)
                            .put("password", hashedPassword);

                    mongo.insert("users", user, insertRes -> {
                        if (insertRes.succeeded()) {
                            String subject = "Welcome to To-Do App";
                            String bodyText = "Your password is: " + rawPassword;
                            vertx.eventBus().send("email.send", email + "|" + subject + "|" + bodyText);
                            ctx.response().setStatusCode(201).end("User registered. Password sent via email.");
                        } else {
                            ctx.response().setStatusCode(500).end("Registration failed");
                        }
                    });
                }
            } else {
                ctx.response().setStatusCode(500).end("Database error");
            }
        });
    }

    // ----------------- Login -----------------
    public void handleLogin(RoutingContext ctx) {
        JsonObject body = ctx.getBodyAsJson();
        String email = body.getString("email");
        String password = body.getString("password");

        mongo.findOne("users", new JsonObject().put("email", email), null, res -> {
            if (res.succeeded() && res.result() != null) {
                JsonObject user = res.result();
                String hashedPassword = user.getString("password");

                if (PasswordUtil.verifyPassword(password, hashedPassword)) {
                    String token = JwtUtil.generateToken(email);

                    // ✅ Store token in Redis
                    redis.set(Arrays.asList("token:" + token, "valid"), r -> {
                        if (r.succeeded()) {
                            ctx.response().putHeader("Content-Type", "application/json")
                                    .end(new JsonObject().put("token", token).encode());
                        } else {
                            ctx.response().setStatusCode(500).end("Token storage failed");
                        }
                    });

                } else {
                    ctx.response().setStatusCode(401).end("Invalid credentials");
                }
            } else {
                ctx.response().setStatusCode(401).end("Invalid credentials");
            }
        });
    }


    // ----------------- Logout -----------------
    public void handleLogout(RoutingContext ctx) {
        String token = ctx.get("token"); // make sure JWTMiddleware puts this in ctx
        if (token != null) {
            redis.del(Arrays.asList("token:" + token), r -> {});
        }
        ctx.response().end("Logged out");
    }


    // ----------------- Password Reset -----------------
    public void handleResetPassword(RoutingContext ctx) {
        JsonObject body = ctx.getBodyAsJson();
        String email = body.getString("email");

        mongo.findOne("users", new JsonObject().put("email", email), null, res -> {
            if (res.succeeded() && res.result() != null) {
                String resetToken = ResetTokenUtil.generateResetToken(6);

                // ✅ FIXED: setex requires list of 3 strings: key, expiration, value
                redis.setex("reset:" + email, "600", resetToken, r -> {});


                String subject = "To-Do App Password Reset";
                String content = "Your reset code is: " + resetToken;
                vertx.eventBus().send("email.send", email + "|" + subject + "|" + content);
                ctx.response().end("Reset token sent to email");
            } else {
                ctx.response().setStatusCode(404).end("Email not registered");
            }
        });
    }
    public void handleRefreshToken(RoutingContext ctx) {
        String token = ctx.get("token");
        String email = JwtUtil.extractEmail(token);

        if (!JwtUtil.verifyToken(token)) {
            ctx.response().setStatusCode(401).end("Invalid token");
            return;
        }

        if (!JwtUtil.isTokenExpiringSoon(token)) {
            ctx.response().end("Token still valid");
            return;
        }

        String newToken = JwtUtil.generateToken(email);
        redis.set(Arrays.asList("token:" + newToken, "valid"), r -> {});
        ctx.response().putHeader("Content-Type", "application/json")
                .end(new JsonObject().put("token", newToken).encode());
    }

}
