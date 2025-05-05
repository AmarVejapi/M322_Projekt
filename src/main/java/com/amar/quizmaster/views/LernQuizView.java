package com.amar.quizmaster.views;

import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

@Route(value = "LernQuiz", layout = MainLayout.class)
@PageTitle("LernQuiz")
public class LernQuizView extends VerticalLayout {
    public LernQuizView() {
        add(new Span("Seite in Arbeit..."));
    }
}
