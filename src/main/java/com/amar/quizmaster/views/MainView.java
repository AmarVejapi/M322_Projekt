package com.amar.quizmaster.views;

import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.VaadinSession;

@Route(value = "main", layout = MainLayout.class)
@PageTitle("Home")
public class MainView extends VerticalLayout implements BeforeEnterObserver {

    public MainView() {
        setSizeFull();

        add(new H3("Willkommen beim Quiz Master!"));
    }

    @Override
    public void beforeEnter(BeforeEnterEvent event) {
        if (VaadinSession.getCurrent().getAttribute("user") == null) {
            event.rerouteTo("login");
        }
    }
}