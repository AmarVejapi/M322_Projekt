package com.amar.quizmaster.utils;

import com.amar.quizmaster.views.LoginView;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.PasswordField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.RouterLink;

public class RegisterForm extends VerticalLayout {

    private final TextField usernameField;
    private final PasswordField passwordField;
    private final PasswordField confirmPasswordField;
    private final Span message;
    private final Button registerButton;

    public RegisterForm(RegisterFormListener listener) {
        usernameField = new TextField("Benutzername");
        usernameField.setWidth("300px");
        usernameField.setRequired(true);

        passwordField = new PasswordField("Passwort");
        passwordField.setWidth("300px");
        passwordField.setRequired(true);

        confirmPasswordField = new PasswordField("Passwort bestätigen");
        confirmPasswordField.setWidth("300px");
        confirmPasswordField.setRequired(true);

        message = new Span();
        message.setVisible(false);

        registerButton = new Button("Registrieren");
        registerButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);

        registerButton.addClickListener(event -> validateAndRegister(listener));

        var loginText = new Span("Sie haben schon ein Konto?");
        loginText.getStyle().setMarginRight("5px");

        add(usernameField, passwordField, confirmPasswordField, registerButton, message,
                new Span(loginText, new RouterLink("Jetzt einloggen!", LoginView.class)));
        setAlignItems(Alignment.CENTER);
        setSpacing(true);
    }

    private void validateAndRegister(RegisterFormListener listener) {
        String username = usernameField.getValue();
        String password = passwordField.getValue();
        String confirmPassword = confirmPasswordField.getValue();

        if (username.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
            showMessage("Alle Felder müssen ausgefüllt werden.", "red");
        } else if (!password.equals(confirmPassword)) {
            showMessage("Die Passwörter stimmen nicht überein.", "red");
        } else if (listener != null) {
            listener.onRegister(username, password, this::showMessage);
        }
    }

    private void showMessage(String messageText, String color) {
        message.setText(messageText);
        message.getStyle().setColor(color);
        message.setVisible(true);
    }

    public interface RegisterFormListener {
        void onRegister(String username, String password, MessageUpdater messageUpdater);
    }

    @FunctionalInterface
    public interface MessageUpdater {
        void updateMessage(String message, String color);
    }
}