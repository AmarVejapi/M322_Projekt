package com.amar.quizmaster.views;

import com.amar.quizmaster.model.User;
import com.amar.quizmaster.repositories.UserRepository;
import com.vaadin.flow.component.html.Span;
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

        var loginText = new Span("Noch kein Konto?");
        loginText.getStyle().setMarginRight("5px");

        var formLayout = new VerticalLayout(
                loginForm,
                new Span(loginText, new RouterLink("Jetzt registrieren!", RegisterView.class))
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

    private void configureLoginForm() {
        loginForm.setForgotPasswordButtonVisible(false);

        loginForm.addLoginListener(event -> {
            String username = event.getUsername();
            String password = event.getPassword();

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