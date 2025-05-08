package com.amar.quizmaster.views;

import com.amar.quizmaster.model.Role;
import com.amar.quizmaster.model.User;
import com.amar.quizmaster.repositories.UserRepository;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.PasswordField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.RouterLink;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

@Route("register")
@PageTitle("Registrierung")
public class RegisterView extends VerticalLayout {

    private final UserRepository userRepository;

    @Autowired
    public RegisterView(final UserRepository userRepository) {
        this.userRepository = userRepository;

        H1 title = new H1("Registrieren");

        TextField usernameField = new TextField("Username");
        usernameField.setWidth("300px");

        PasswordField passwordField = new PasswordField("Password");
        passwordField.setWidth("300px");

        PasswordField confirmPasswordField = new PasswordField("Passwort wiederholen");
        confirmPasswordField.setWidth("300px");

        Button registerButton = new Button("Registrieren");
        registerButton.getStyle().setBackgroundColor("#007bff").setColor("white");

        Span message = new Span();
        message.setVisible(false);

        registerButton.addClickListener(e -> registerUser(usernameField, passwordField, confirmPasswordField, message));

        Span loginText = new Span("Schon ein Konto?");
        loginText.getStyle().setColor("#555").setFontSize("14px").setMarginRight("5px");

        VerticalLayout formLayout = new VerticalLayout(
                title,
                usernameField,
                passwordField,
                confirmPasswordField,
                registerButton,
                message,
                new Span(loginText, new RouterLink("Jetzt einloggen!", LoginView.class))
        );

        formLayout.setSpacing(true);
        formLayout.setPadding(true);
        formLayout.setAlignItems(Alignment.CENTER);
        formLayout.getStyle().setPadding("30px").setBackgroundColor("white").setMinWidth("300px");

        setSizeFull();
        setAlignItems(Alignment.CENTER);
        setJustifyContentMode(JustifyContentMode.CENTER);
        add(formLayout);
    }

    @Transactional
    private void registerUser(TextField usernameField, PasswordField passwordField, PasswordField confirmPasswordField, Span message) {
        String username = usernameField.getValue();
        String password = passwordField.getValue();
        String confirmPassword = confirmPasswordField.getValue();

        if (!password.equals(confirmPassword)) {
            message.setText("Passwörter stimmen nicht überein.");
            message.getStyle().setColor("red");
            message.setVisible(true);
        } else if (userRepository.findByUsername(username).isPresent()) {
            message.setText("Benutzername bereits vergeben. Bitte wähle einen anderen.");
            message.getStyle().setColor("red");
            message.setVisible(true);
        } else {
            User newUser = new User(username, password, Role.USER);
            userRepository.save(newUser);

            message.setText("Registrierung erfolgreich! Du kannst dich jetzt einloggen.");
            message.getStyle().setColor("green");
            message.setVisible(true);
        }
    }
}