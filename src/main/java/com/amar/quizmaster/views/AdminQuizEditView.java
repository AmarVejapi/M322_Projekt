package com.amar.quizmaster.views;

import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

@Route(value = "AdminQuizEdit", layout = MainLayout.class)
@PageTitle("AdminQuizEdit")
public class AdminQuizEditView extends VerticalLayout {
    public AdminQuizEditView() {
        add(new Span("Seite in Arbeit..."));
    }
}
