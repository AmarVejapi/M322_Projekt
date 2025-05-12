package com.amar.quizmaster.views;

import com.amar.quizmaster.model.User;
import com.amar.quizmaster.repositories.UserRepository;
import com.vaadin.flow.component.login.LoginForm;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.*;
import com.vaadin.flow.server.VaadinSession;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Optional;

import static java.util.Objects.requireNonNull;

@Route("login")
@PageTitle("Login")
public class LoginView extends VerticalLayout implements BeforeEnterObserver {

    private final UserRepository userRepository;

    public static User currentUser;

    private final LoginForm loginForm = new LoginForm();

    @Autowired
    public LoginView(final UserRepository userRepository) {
        this.userRepository = requireNonNull(userRepository);

        configureLoginForm();

        var registerLink = new RouterLink("Noch kein Konto? Jetzt registrieren!", RegisterView.class);

        add(loginForm, registerLink);
        setSizeFull();
        setAlignItems(Alignment.CENTER);
        setJustifyContentMode(JustifyContentMode.CENTER);
    }

    private void configureLoginForm() {
        loginForm.setForgotPasswordButtonVisible(false);

        loginForm.addLoginListener(e -> {
            String username = e.getUsername();
            String password = e.getPassword();

            Optional<User> user = userRepository.findByUsername(username);

            if (user.isPresent() && user.get().getPassword().equals(password)) {
                currentUser = user.get();
                VaadinSession.getCurrent().setAttribute("user", username);
                loginForm.setEnabled(false);
                getUI().ifPresent(ui -> ui.navigate("main"));
            } else {
                loginForm.setError(true);
            }
        });
    }

    @Override
    public void beforeEnter(BeforeEnterEvent event) {
        if (VaadinSession.getCurrent().getAttribute("user") == null) {
            event.rerouteTo("login");
        }
    }
}