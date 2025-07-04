package org.example.worker;

import io.vertx.core.AbstractVerticle;
import io.vertx.ext.mongo.MongoClient;
import org.example.utils.ReminderUtil;

import java.text.SimpleDateFormat;
import java.util.Date;

public class ReminderWorkerVerticle extends AbstractVerticle {

    @Override
    public void start() {
        MongoClient mongo = MongoClient.createShared(vertx, config());

        vertx.setPeriodic(3600000, id -> { // every 1 hour
            mongo.find("todos", new io.vertx.core.json.JsonObject().put("completed", false), res -> {
                if (res.succeeded()) {
                    long now = System.currentTimeMillis();
                    res.result().forEach(task -> {
                        String dueDate = task.getString("dueDate");
                        String email = task.getString("email");
                        String title = task.getString("title");

                        try {
                            long dueMillis = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm").parse(dueDate).getTime();
                            if (dueMillis - now <= 3600000 && dueMillis - now > 0) {
                                ReminderUtil.sendReminder(email, title, dueDate);
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    });
                }
            });
        });
    }
}
