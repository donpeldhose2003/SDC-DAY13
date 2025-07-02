package org.example.worker;

import io.vertx.core.AbstractVerticle;
import org.example.utils.EmailUtil;

import jakarta.mail.MessagingException;

public class EmailWorkerVerticle extends AbstractVerticle {
    @Override
    public void start() {
        vertx.eventBus().consumer("email.send", message -> {
            try {
                String[] parts = message.body().toString().split("\\|");
                String to = parts[0];
                String subject = parts[1];
                String body = parts[2];
                EmailUtil.sendEmail(to, subject, body);
                message.reply("Email sent");
            } catch (MessagingException e) {
                e.printStackTrace();
                message.fail(500, "Email failed");
            }
        });
    }
}
