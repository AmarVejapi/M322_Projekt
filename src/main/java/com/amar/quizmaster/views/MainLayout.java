package com.amar.quizmaster.views;

import com.vaadin.flow.component.applayout.AppLayout;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.Span;
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
        H1 title = new H1("Home");
        title.getStyle().set("margin", "0").set("font-size", "1.5em");

        Span username = new Span("Username");
        HorizontalLayout header = new HorizontalLayout(title);
        header.setWidthFull();
        header.setJustifyContentMode(FlexComponent.JustifyContentMode.BETWEEN);
        header.setAlignItems(FlexComponent.Alignment.CENTER);
        header.add(username);
        header.setPadding(true);
        addToNavbar(header);
    }

    private void createDrawer() {
        RouterLink homeLink = new RouterLink("Home", MainView.class);
        RouterLink lernQuizLink = new RouterLink("Lern-Quiz", LernQuizView.class);
        RouterLink testQuizLink = new RouterLink("Test-Quiz", TestQuizView.class);
        RouterLink bestenlisteLink = new RouterLink("Bestenliste", BestenlisteView.class);
        RouterLink adminBereichLink = new RouterLink("Admin-Bereich", AdminOverviewView.class);
        RouterLink logoutLink = new RouterLink("Logout", LoginView.class);

        VerticalLayout menuLayout = new VerticalLayout(
                homeLink,
                lernQuizLink,
                testQuizLink,
                bestenlisteLink,
                adminBereichLink,
                logoutLink
        );

        addToDrawer(menuLayout);
    }
}
