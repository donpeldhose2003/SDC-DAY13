package org.example;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Vertx;
import io.vertx.ext.mongo.MongoClient;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.handler.BodyHandler;
import io.vertx.ext.web.handler.StaticHandler;
import io.vertx.redis.client.RedisAPI;

import org.example.database.MongoConfig;
import org.example.database.RedisConfig;
import org.example.handlers.AuthHandler;
import org.example.handlers.TodoHandler;
import org.example.middleware.JWTMiddleware;
import org.example.worker.EmailWorkerVerticle;

public class Main extends AbstractVerticle {

    public static void main(String[] args) {
        Vertx vertx = Vertx.vertx();
        vertx.deployVerticle(new Main());
    }

    @Override
    public void start() {
        MongoClient mongo = MongoConfig.createMongoClient(vertx);
        RedisAPI redis = RedisConfig.createRedisClient(vertx);

        AuthHandler authHandler = new AuthHandler(mongo, vertx, redis);
        TodoHandler todoHandler = new TodoHandler(mongo);
        JWTMiddleware jwtMiddleware = new JWTMiddleware(redis);

        Router router = Router.router(vertx);
        router.route().handler(BodyHandler.create());
        router.route("/web/*").handler(StaticHandler.create().setCachingEnabled(false).setWebRoot("web"));

        // Auth routes
        router.post("/register").handler(authHandler::handleRegister);
        router.post("/login").handler(authHandler::handleLogin);
        router.post("/logout").handler(jwtMiddleware.handle()).handler(authHandler::handleLogout);

        router.post("/reset-password").handler(authHandler::handleResetPassword);

        // ToDo routes
        router.get("/todos").handler(jwtMiddleware.handle()).handler(todoHandler::getTodos);
        router.post("/todos").handler(jwtMiddleware.handle()).handler(todoHandler::addTodo);
        router.put("/todos/:id").handler(jwtMiddleware.handle()).handler(todoHandler::updateTodo);
        router.delete("/todos/:id").handler(jwtMiddleware.handle()).handler(todoHandler::deleteTodo);



        // Worker verticle for sending emails
        vertx.deployVerticle(new EmailWorkerVerticle());


        // Start HTTP server
        vertx.createHttpServer().requestHandler(router).listen(8888, res -> {
            if (res.succeeded()) {
                System.out.println("✅ Server started on port 8888");
            } else {
                System.err.println("failed to start server: " +res.cause());
            }
        });
    }
}
