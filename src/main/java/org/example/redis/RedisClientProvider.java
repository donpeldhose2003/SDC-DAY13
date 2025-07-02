package org.example.redis;

import io.vertx.core.Vertx;
import io.vertx.redis.client.Redis;
import io.vertx.redis.client.RedisAPI;

public class RedisClientProvider {
    private static RedisAPI redis;

    public static void init(Vertx vertx) {
        redis = RedisAPI.api(Redis.createClient(vertx, "redis://localhost:6379"));
    }

    public static RedisAPI get() {
        return redis;
    }
}