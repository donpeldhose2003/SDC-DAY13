package org.example.handlers;



import io.vertx.core.json.JsonObject;
import io.vertx.ext.mongo.MongoClient;
import io.vertx.ext.web.RoutingContext;

import java.util.List;
public class TodoHandler {

    private final MongoClient mongo;

    public TodoHandler(MongoClient mongo) {
        this.mongo = mongo;
    }

    public void getTodos(RoutingContext ctx) {
        String userEmail = ctx.user().principal().getString("email");
        JsonObject query = new JsonObject().put("owner", userEmail);

        mongo.find("todos", query, res -> {
            if (res.succeeded()) {
                ctx.response().putHeader("Content-Type", "application/json")
                        .end(res.result().toString());
            } else {
                ctx.response().setStatusCode(500).end("Failed to fetch todos");
            }
        });
    }

    public void addTodo(RoutingContext ctx) {
        JsonObject body = ctx.getBodyAsJson();
        String userEmail = ctx.user().principal().getString("email");
        body.put("owner", userEmail);

        mongo.insert("todos", body, res -> {
            if (res.succeeded()) {
                ctx.response().setStatusCode(201).end("Todo added");
            } else {
                ctx.response().setStatusCode(500).end("Failed to add todo");
            }
        });
    }

    public void updateTodo(RoutingContext ctx) {
        String id = ctx.pathParam("id");
        JsonObject body = ctx.getBodyAsJson();
        String userEmail = ctx.user().principal().getString("email");

        JsonObject query = new JsonObject()
                .put("_id", id)
                .put("owner", userEmail);

        JsonObject update = new JsonObject().put("$set", body);

        mongo.updateCollection("todos", query, update, res -> {
            if (res.succeeded()) {
                ctx.response().end("Todo updated");
            } else {
                ctx.response().setStatusCode(500).end("Failed to update todo");
            }
        });
    }

    public void deleteTodo(RoutingContext ctx) {
        String id = ctx.pathParam("id");
        String userEmail = ctx.user().principal().getString("email");

        JsonObject query = new JsonObject()
                .put("_id", id)
                .put("owner", userEmail);

        mongo.removeDocument("todos", query, res -> {
            if (res.succeeded()) {
                ctx.response().end("Todo deleted");
            } else {
                ctx.response().setStatusCode(500).end("Failed to delete todo");
            }
        });
    }
    public void markComplete(RoutingContext ctx) {
        String id = ctx.pathParam("id");
        boolean complete = Boolean.parseBoolean(ctx.request().getParam("complete"));

        mongo.updateCollection("todos",
                new JsonObject().put("_id", id),
                new JsonObject().put("$set", new JsonObject().put("completed", complete).put("updatedAt", System.currentTimeMillis())),
                res -> ctx.response().end("Updated task status"));
    }
}
