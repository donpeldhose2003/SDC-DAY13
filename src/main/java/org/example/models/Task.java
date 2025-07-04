package org.example.models;


import io.vertx.core.json.JsonObject;

public class Task {
    public static JsonObject createTask(String email, JsonObject body) {
        return new JsonObject()
                .put("email", email)
                .put("title", body.getString("title"))
                .put("description", body.getString("description"))
                .put("dueDate", body.getString("dueDate"))
                .put("priority", body.getString("priority"))
                .put("completed", false)
                .put("createdAt", System.currentTimeMillis())
                .put("updatedAt", System.currentTimeMillis());
    }
}
