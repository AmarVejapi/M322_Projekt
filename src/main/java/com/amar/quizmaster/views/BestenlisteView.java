package com.amar.quizmaster.views;

import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

@Route(value = "Bestenliste", layout = MainLayout.class)
@PageTitle("Bestenliste")
public class BestenlisteView extends VerticalLayout {
    public BestenlisteView() {
        add(new Span("BestenlisteView"));
    }
}
