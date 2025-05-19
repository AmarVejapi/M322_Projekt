package com.amar.quizmaster.views;

import com.vaadin.flow.component.applayout.AppLayout;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.RouterLink;

public class MainLayout extends AppLayout {

    public MainLayout() {
        createHeader();
        createDrawer();
    }

    private void createHeader() {
        var title = new H2("Home");

        var logoutButton = new Button("Logout", event -> getUI().ifPresent(ui -> ui.navigate("login")));
        logoutButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY, ButtonVariant.LUMO_ERROR);

        var header = new HorizontalLayout(title);
        header.setWidthFull();
        header.setJustifyContentMode(FlexComponent.JustifyContentMode.BETWEEN);
        header.setAlignItems(FlexComponent.Alignment.CENTER);
        header.add(logoutButton);
        header.setPadding(true);
        addToNavbar(header);
    }

    private void createDrawer() {
        var menuLayout = new VerticalLayout(
                new RouterLink("Home", MainView.class),
                new RouterLink("Lern-Quiz", LernQuizStartView.class),
                new RouterLink("Test-Quiz", TestQuizStartView.class),
                new RouterLink("Admin-Bereich", AdminOverviewView.class)
        );

        addToDrawer(menuLayout);
    }
}
