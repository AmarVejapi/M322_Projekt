package com.amar.quizmaster.views;

import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

@Route(value = "TestQuizStart", layout = MainLayout.class)
@PageTitle("TestQuizStart")
public class TestQuizStartView extends VerticalLayout {
    public TestQuizStartView() {
        add(new Span("Seite in Arbeit..."));
    }
}
