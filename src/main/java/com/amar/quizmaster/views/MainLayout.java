package com.amar.quizmaster.views;

import com.vaadin.flow.component.applayout.AppLayout;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.html.H1;
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
        var title = new H1("Home");
        title.getStyle()
                .setMargin("0")
                .setFontSize("1.5em");

        var logoutButton = new Button("Logout");
        logoutButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY, ButtonVariant.LUMO_ERROR);
        logoutButton.addClickListener(e -> getUI().ifPresent(ui -> ui.navigate("login")));

        var header = new HorizontalLayout(title);
        header.setWidthFull();
        header.setJustifyContentMode(FlexComponent.JustifyContentMode.BETWEEN);
        header.setAlignItems(FlexComponent.Alignment.CENTER);
        header.add(logoutButton);
        header.setPadding(true);
        addToNavbar(header);
    }

    private void createDrawer() {
        var homeLink = new RouterLink("Home", MainView.class);
        var lernQuizLink = new RouterLink("Lern-Quiz", LernQuizStartView.class);
        var testQuizLink = new RouterLink("Test-Quiz", TestQuizStartView.class);
        var bestenlisteLink = new RouterLink("Bestenliste", BestenlisteView.class);
        var adminBereichLink = new RouterLink("Admin-Bereich", AdminOverviewView.class);

        var menuLayout = new VerticalLayout(
                homeLink,
                lernQuizLink,
                testQuizLink,
                bestenlisteLink,
                adminBereichLink
        );

        addToDrawer(menuLayout);
    }
}
