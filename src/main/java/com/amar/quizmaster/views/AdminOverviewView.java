package com.amar.quizmaster.views;

import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

@Route(value = "AdminOverview", layout = MainLayout.class)
@PageTitle("AdminOverview")
public class AdminOverviewView extends VerticalLayout {
    public AdminOverviewView() {
        add(new Span("Seite in Arbeit..."));
    }
}
