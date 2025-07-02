package org.example.middleware;

import io.vertx.core.Handler;
import io.vertx.ext.web.RoutingContext;
import io.vertx.redis.client.RedisAPI;
import org.example.utils.JwtUtil;

import java.util.Collections;

public class JWTMiddleware {

    private final RedisAPI redis;

    public JWTMiddleware(RedisAPI redis) {
        this.redis = redis;
    }

    public Handler<RoutingContext> handle() {
        return ctx -> {
            String authHeader = ctx.request().getHeader("Authorization");
            if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                ctx.response().setStatusCode(401).end("Missing or invalid Authorization header");
                return;
            }

            String token = authHeader.substring(7);
            if (!JwtUtil.verifyToken(token)) {
                ctx.response().setStatusCode(401).end("Invalid or expired token");
                return;
            }

            // ✅ Fix: RedisAPI.get() takes a List<String>, but only one key, so use Collections.singletonList
            redis.get("token:" + token, res -> {

                if (res.succeeded() && res.result() != null) {
                    ctx.setUser(JwtUtil.extractUser(token)); // properly returns io.vertx.ext.auth.User
                    ctx.next();
                } else {
                    ctx.response().setStatusCode(401).end("Token not found or expired");
                }
            });
        };
    }
}
