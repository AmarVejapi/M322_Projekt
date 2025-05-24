package com.amar.quizmaster.views;

import com.amar.quizmaster.model.Role;
import com.amar.quizmaster.model.User;
import com.amar.quizmaster.repositories.UserRepository;
import com.amar.quizmaster.utils.RegisterForm;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import org.springframework.beans.factory.annotation.Autowired;

import static java.util.Objects.requireNonNull;

@Route("register")
@PageTitle("Registrierung")
public class RegisterView extends VerticalLayout {

    private final UserRepository userRepository;

    @Autowired
    public RegisterView(UserRepository userRepository) {
        this.userRepository = requireNonNull(userRepository);

        getStyle().setBackgroundColor("white");

        var title = new H2("Registrieren");

        var registerForm = new RegisterForm((username, password, messageUpdater) -> {
            if (userRepository.findByUsername(username).isPresent()) {
                messageUpdater.updateMessage("Benutzername ist bereits vergeben.", "red");
            } else {
                var newUser = new User(username, password, Role.USER);
                userRepository.save(newUser);
                messageUpdater.updateMessage("Registrierung erfolgreich! Du kannst dich jetzt einloggen.", "green");
            }
        });

        setSizeFull();
        setAlignItems(Alignment.CENTER);
        setJustifyContentMode(JustifyContentMode.CENTER);

        add(title, registerForm);
    }
}