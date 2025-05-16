package com.amar.quizmaster.utils;

import com.vaadin.flow.component.notification.Notification;

public class Notificator {

    public static Notification notification(String message) {
        var notification = new Notification();
        notification.setText(message);
        notification.setDuration(5000);
        notification.setPosition(Notification.Position.MIDDLE);

        return notification;
    }

    public static Notification pointNotification(String message) {
        var notification = new Notification();
        notification.setText(message);
        notification.setDuration(2000);
        notification.setPosition(Notification.Position.MIDDLE);

        return notification;
    }
}