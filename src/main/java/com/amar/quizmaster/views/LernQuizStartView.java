package com.amar.quizmaster.views;

import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

@Route(value = "LernQuizStart", layout = MainLayout.class)
@PageTitle("LernQuizStart")
public class LernQuizStartView extends VerticalLayout {
    public LernQuizStartView() {
        add(new Span("Seite in Arbeit..."));
    }
}
