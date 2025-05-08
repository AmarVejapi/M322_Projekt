/*package com.amar.quizmaster.views;

import com.amar.quizmaster.repositories.UserRepository;
import com.vaadin.flow.component.login.LoginForm;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.*;
import com.vaadin.flow.server.VaadinSession;
import org.springframework.beans.factory.annotation.Autowired;

@Route("login")
@PageTitle("Login")
public class LoginView extends VerticalLayout implements BeforeEnterObserver {

    private final LoginForm loginForm = new LoginForm();
    private final UserRepository userRepository;

    @Autowired
    public LoginView(final UserRepository userRepository) {
        this.userRepository = userRepository;

        loginForm.setForgotPasswordButtonVisible(false);

        loginForm.addLoginListener(e -> {
            String username = e.getUsername();
            String password = e.getPassword();

            userRepository.findByUsername(username).ifPresentOrElse(user -> {
                if (user.getPassword().equals(password)) {
                    VaadinSession.getCurrent().setAttribute("user", username);
                    getUI().ifPresent(ui -> ui.navigate("main"));
                } else {
                    loginForm.setError(true);
                }
            }, () -> loginForm.setError(true));
        });

        RouterLink registerLink = new RouterLink("Noch kein Konto? Jetzt registrieren!", RegisterView.class);

        add(loginForm, registerLink);
        setSizeFull();
        setAlignItems(Alignment.CENTER);
        setJustifyContentMode(JustifyContentMode.CENTER);
    }

    @Override
    public void beforeEnter(BeforeEnterEvent event) {
        if (VaadinSession.getCurrent().getAttribute("user") != null) {
            event.forwardTo("main");
        }
    }
}*/

package com.amar.quizmaster.views;

import com.amar.quizmaster.model.User;
import com.amar.quizmaster.repositories.UserRepository;
import com.vaadin.flow.component.login.LoginForm;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.*;
import com.vaadin.flow.server.VaadinSession;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Optional;

@Route("login")
@PageTitle("Login")
public class LoginView extends VerticalLayout implements BeforeEnterObserver {

    private final LoginForm loginForm = new LoginForm();
    private final UserRepository userRepository;

    @Autowired
    public LoginView(final UserRepository userRepository) {
        this.userRepository = userRepository;

        configureLoginForm();

        RouterLink registerLink = new RouterLink("Noch kein Konto? Jetzt registrieren!", RegisterView.class);

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

            if (user.get().getPassword().equals(password)) {
                VaadinSession.getCurrent().setAttribute("user", username);
                loginForm.setEnabled(false);
                getUI().ifPresent(ui -> ui.navigate("main"));
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