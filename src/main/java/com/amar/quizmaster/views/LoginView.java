package com.amar.quizmaster.views;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.PasswordField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

@Route("login")
@PageTitle("Login")
public class LoginView extends VerticalLayout {

    public LoginView() {
        H2 title = new H2("Bitte einloggen");
        TextField username = new TextField("Benutzername");
        PasswordField password = new PasswordField("Passwort");
        Button loginButton = new Button("Login");

        VerticalLayout formLayout = new VerticalLayout(title, username, password, loginButton);
        formLayout.setAlignItems(Alignment.CENTER);

        add(formLayout);
        setAlignItems(Alignment.CENTER);
    }
}
