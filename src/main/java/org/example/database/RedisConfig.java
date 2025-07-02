package org.example.database;

import io.vertx.core.Vertx;
import io.vertx.redis.client.Redis;
import io.vertx.redis.client.RedisAPI;
import io.vertx.redis.client.RedisOptions;

public class RedisConfig {
    public static RedisAPI createRedisClient(Vertx vertx) {
        RedisOptions options = new RedisOptions()
                .setConnectionString("redis://localhost:6379");

        Redis redisClient = Redis.createClient(vertx, options);
        return RedisAPI.api(redisClient);
    }
}
