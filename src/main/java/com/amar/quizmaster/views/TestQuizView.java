package com.amar.quizmaster.views;

import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

@Route(value = "TestQuiz", layout = MainLayout.class)
@PageTitle("TestQuiz")
public class TestQuizView extends VerticalLayout {
    public TestQuizView() {
        add(new Span("Seite in Arbeit..."));
    }
}
