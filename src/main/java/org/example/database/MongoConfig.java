package org.example.database;

import io.vertx.core.Vertx;
import io.vertx.ext.mongo.MongoClient;
import io.vertx.core.json.JsonObject;

public class MongoConfig {
    public static MongoClient createMongoClient(Vertx vertx) {
        JsonObject config = new JsonObject()
                .put("connection_string", "mongodb://localhost:27017")
                .put("db_name", "todo_db");

        return MongoClient.createShared(vertx, config);
    }
}
