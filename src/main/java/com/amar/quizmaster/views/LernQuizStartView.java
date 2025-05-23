package com.amar.quizmaster.views;

import com.amar.quizmaster.model.Leaderboard;
import com.amar.quizmaster.model.Quiz;
import com.amar.quizmaster.model.QuizType;
import com.amar.quizmaster.repositories.LeaderboardRepository;
import com.amar.quizmaster.repositories.QuizRepository;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.component.virtuallist.VirtualList;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

import static java.util.Objects.requireNonNull;

@Route(value = "QuizOverview", layout = MainLayout.class)
@PageTitle("Quiz Übersicht")
public class LernQuizStartView extends VerticalLayout {

    private final QuizRepository quizRepository;
    private final LeaderboardRepository leaderboardRepository;

    private final List<Quiz> allQuizzes;
    private final VirtualList<Quiz> quizList;

    private final Span noQuizzesMessage = new Span("An diesem Tag wurde kein Quiz erstellt.");

    public LernQuizStartView(final QuizRepository quizRepository, final LeaderboardRepository leaderboardRepository) {
        this.quizRepository = requireNonNull(quizRepository);
        this.leaderboardRepository = requireNonNull(leaderboardRepository);

        setHeightFull();

        allQuizzes = quizRepository.findByType(QuizType.LERNQUIZ);

        var searchField = new TextField();
        searchField.setPlaceholder("Quizze suchen ...");
        searchField.setTooltipText("Quizze nach Titel suchen");
        searchField.addValueChangeListener(event -> filterQuizzes(event.getValue()));

        var createdAtPicker = new DatePicker();
        createdAtPicker.setPlaceholder("Nach Datum filtern");
        createdAtPicker.setTooltipText("Quizze nach Datum filtern");
        createdAtPicker.addValueChangeListener(event -> filterQuizzesByCreatedAt(event.getValue()));

        noQuizzesMessage.getElement().getThemeList().add("badge error");
        noQuizzesMessage.setVisible(false);

        quizList = new VirtualList<>();
        quizList.setItems(allQuizzes);
        quizList.setRenderer(new ComponentRenderer<>(this::createQuizLayout));

        add(new HorizontalLayout(searchField, createdAtPicker), noQuizzesMessage, quizList);
        setAlignItems(Alignment.CENTER);
    }

    private VerticalLayout createQuizLayout(Quiz quiz) {
        var layout = new VerticalLayout();

        layout.getStyle()
                .setBackgroundColor("var(--lumo-contrast-10pct)")
                .setBorderRadius("5px")
                .setPadding("10px")
                .setMargin("5px");

        var startQuizButton = new Button("Quiz starten", event -> startQuiz(quiz.getId()));
        var leaderboardButton = new Button("Zur Bestenliste", event -> openLeaderboardDialog(quiz));
        startQuizButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        leaderboardButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);

        var buttonLayout = new HorizontalLayout(startQuizButton, leaderboardButton);

        layout.add(
                new H1(quiz.getTitle()),
                new Span(quiz.getDescription()),
                new Span("Typ: " + quiz.getType()),
                buttonLayout
        );
        return layout;
    }

    private void openLeaderboardDialog(Quiz quiz) {
        var dialog = new Dialog();
        dialog.setHeaderTitle("Bestenliste für " + quiz.getTitle());

        dialog.setWidth("800px");

        var leaderboardGrid = createLeaderboardGrid(quiz);
        var contentLayout = new VerticalLayout(leaderboardGrid);
        contentLayout.setSizeFull();
        dialog.add(contentLayout);

        dialog.getFooter().add(new Button("Schliessen", event -> dialog.close()));

        dialog.open();
    }

    private Grid<Leaderboard> createLeaderboardGrid(Quiz quiz) {
        Grid<Leaderboard> leaderboardGrid = new Grid<>(Leaderboard.class, false);

        leaderboardGrid.addColumn(Leaderboard::getUsername).setHeader("Benutzer").setSortable(true);
        leaderboardGrid.addColumn(Leaderboard::getScore).setHeader("Punktzahl").setSortable(true);
        leaderboardGrid.addColumn(Leaderboard::getTime).setHeader("Zeit (s)").setSortable(true);
        leaderboardGrid.addColumn(entry ->
                        entry.getCompletedAt().format(DateTimeFormatter.ofPattern("dd.MM.yyyy")))
                .setHeader("Abgeschlossen am")
                .setSortable(true);

        leaderboardGrid.setWidthFull();

        List<Leaderboard> leaderboardData = leaderboardRepository.findTop25ByQuizTitleOrderByScoreDesc(quiz.getTitle());
        if (!leaderboardData.isEmpty()) {
            leaderboardGrid.setItems(leaderboardData);
        }

        return leaderboardGrid;
    }

    private void filterQuizzes(String filter) {
        List<Quiz> filteredQuizzes = allQuizzes.stream()
                .filter(quiz -> quiz.getTitle().toLowerCase().contains(filter.toLowerCase()))
                .collect(Collectors.toList());
        quizList.setItems(filteredQuizzes);
    }

    private void filterQuizzesByCreatedAt(LocalDate selectedDate) {
        if (selectedDate != null) {
            List<Quiz> filteredQuizzes = allQuizzes.stream()
                    .filter(quiz -> quiz.getCreatedAt() != null && quiz.getCreatedAt().toLocalDate().isEqual(selectedDate))
                    .collect(Collectors.toList());

            noQuizzesMessage.setVisible(filteredQuizzes.isEmpty());
            quizList.setItems(filteredQuizzes);
        } else {
            quizList.setItems(allQuizzes);
            noQuizzesMessage.setVisible(false);
        }
    }

    private void startQuiz(Long quizId) {
        getUI().ifPresent(ui -> ui.navigate(LernQuizView.class, quizId));
    }
}