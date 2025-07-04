package org.example.utils;

import jakarta.mail.MessagingException;

public class ReminderUtil {
    public static void sendReminder(String to, String title, String dueDate) {
        String subject = "⏰ Task Reminder: " + title;
        String body = "Reminder! Your task \"" + title + "\" is due on " + dueDate;
        try {
            EmailUtil.sendEmail(to, subject, body);
        } catch (MessagingException e) {
            e.printStackTrace();
        }
    }
}
