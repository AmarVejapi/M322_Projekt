package com.amar.quizmaster.views;

import com.amar.quizmaster.model.Quiz;
import com.amar.quizmaster.model.QuizType;
import com.amar.quizmaster.repositories.QuizRepository;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.component.virtuallist.VirtualList;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

import java.util.List;
import java.util.stream.Collectors;

@Route(value = "TestQuizStart", layout = MainLayout.class)
@PageTitle("TestQuizStart")
public class TestQuizStartView extends VerticalLayout {

    private final List<Quiz> allQuizzes;
    private final VirtualList<Quiz> quizList;

    public TestQuizStartView(QuizRepository quizRepository) {

        setHeightFull();

        allQuizzes = quizRepository.findByType(QuizType.TESTQUIZ);

        H1 title = new H1("Test Quiz Übersicht");

        TextField searchField = new TextField();
        searchField.setPlaceholder("Quiz suchen...");
        searchField.addValueChangeListener(event -> filterQuizzes(event.getValue()));

        quizList = new VirtualList<>();
        quizList.setItems(allQuizzes);
        quizList.setRenderer(new ComponentRenderer<>(this::createQuizLayout));

        add(title, searchField, quizList);
        setAlignItems(Alignment.CENTER);
    }

    private VerticalLayout createQuizLayout(Quiz quiz) {
        VerticalLayout layout = new VerticalLayout();

        layout.getStyle()
                .setBackgroundColor("lightgray")
                .setBorderRadius("5px")
                .setPadding("10px")
                .setMargin("5px");

        layout.add(
                new H1(quiz.getTitle()),
                new Span(quiz.getDescription()),
                new Span("Typ: " + quiz.getType()),
                new Button("Quiz starten", event -> startQuiz(quiz.getId()))
        );
        return layout;
    }

    private void filterQuizzes(String filter) {
        List<Quiz> filteredQuizzes = allQuizzes.stream()
                .filter(quiz -> quiz.getTitle().toLowerCase().contains(filter.toLowerCase()))
                .collect(Collectors.toList());
        quizList.setItems(filteredQuizzes);
    }

    private void startQuiz(Long quizId) {
        getUI().ifPresent(ui -> ui.navigate(TestQuizView.class, quizId));
    }
}