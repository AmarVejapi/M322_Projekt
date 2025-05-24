package com.amar.quizmaster.views;

import com.amar.quizmaster.model.Leaderboard;
import com.amar.quizmaster.model.Question;
import com.amar.quizmaster.model.Quiz;
import com.amar.quizmaster.model.User;
import com.amar.quizmaster.repositories.LeaderboardRepository;
import com.amar.quizmaster.repositories.QuestionRepository;
import com.amar.quizmaster.repositories.QuizRepository;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.dom.Style;
import com.vaadin.flow.router.BeforeEvent;
import com.vaadin.flow.router.HasUrlParameter;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import static java.util.Objects.requireNonNull;

@Route(value = "TestQuiz", layout = MainLayout.class)
@PageTitle("TestQuiz")
public class TestQuizView extends VerticalLayout implements HasUrlParameter<Long> {

    private final QuestionRepository questionRepository;
    private final QuizRepository quizRepository;
    private final LeaderboardRepository leaderboardRepository;
    private final User user;

    private final H1 titleLabel = new H1();
    private final H1 questionLabel = new H1();
    private final Span scoreLabel = new Span();

    private int score = 0;
    private int questionIndex = 0;
    private long quizStartTime;
    private List<Question> questions = new ArrayList<>();
    private Quiz currentQuiz;
    private final List<Button> currentButtons = new ArrayList<>();

    public TestQuizView(final QuestionRepository questionRepository, final QuizRepository quizRepository,
                        final LeaderboardRepository leaderboardRepository) {
        this.questionRepository = requireNonNull(questionRepository);
        this.quizRepository = requireNonNull(quizRepository);
        this.leaderboardRepository = requireNonNull(leaderboardRepository);
        this.user = LoginView.currentUser;

        setDefaultHorizontalComponentAlignment(Alignment.CENTER);
        setPadding(true);
        setSpacing(true);
        setWidthFull();

        scoreLabel.getStyle()
                .setFontSize("18px")
                .setMargin("10px 0")
                .setColor("gray");

        add(titleLabel, scoreLabel, questionLabel);
    }

    @Override
    public void setParameter(BeforeEvent event, Long quizId) {
        currentQuiz = quizRepository.findById(quizId).orElseThrow(() -> new IllegalArgumentException("Quiz nicht gefunden"));

        this.questions = questionRepository.findByQuizId(quizId);

        if (questions.isEmpty()) {
            LoggerFactory.getLogger(getClass()).warn("Quiz {} hat keine Fragen!", quizId);
            questionLabel.setText("Keine Fragen in diesem Quiz.");
            return;
        }

        quizStartTime = System.currentTimeMillis();

        titleLabel.setText(currentQuiz.getTitle());
        titleLabel.getStyle()
                .setFontSize("24px")
                .setFontWeight("bold")
                .setTextAlign(Style.TextAlign.CENTER);

        scoreLabel.setText("Punktestand: " + score);
        scoreLabel.getElement().getThemeList().add("badge");

        questionLabel.getStyle()
                .setFontSize("20px")
                .setTextAlign(Style.TextAlign.CENTER);

        loadNextQuestion();
    }

    private void loadNextQuestion() {
        questionLabel.setText(questions.get(questionIndex).getText());

        currentButtons.forEach(this::remove);
        currentButtons.clear();

        Question currentQuestion = questions.get(questionIndex);
        List<String> options = currentQuestion.getOptions();

        for (int i = 0; i < options.size(); i++) {
            final int index = i;
            String option = options.get(i);

            var answerButton = new Button(option);
            answerButton.addClickListener(event -> {
                currentButtons.forEach(button -> button.setEnabled(false));
                checkAnswer(index, answerButton);
            });

            answerButton.getStyle()
                    .setWidth("300px")
                    .setMargin("10px 0")
                    .setPadding("10px")
                    .setColor("white")
                    .setBackgroundColor("#007BFF")
                    .setBorder("none")
                    .setBorderRadius("5px")
                    .setFontSize("16px")
                    .setCursor("pointer");

            add(answerButton);
            currentButtons.add(answerButton);
        }
    }

    private void checkAnswer(int selectedAnswer, Button answerButton) {
        Question currentQuestion = questions.get(questionIndex);
        int correctIndex = currentQuestion.getCorrectAnswerIndex();

        if (selectedAnswer == correctIndex) {
            score++;
            Notification.show("Richtig!", 2000, Notification.Position.MIDDLE);
            answerButton.getStyle().setBackgroundColor("green");
        } else {
            Notification.show("Falsch!", 2000, Notification.Position.MIDDLE);
            answerButton.getStyle().setBackgroundColor("red");
        }

        questionIndex++;
        scoreLabel.setText("Punktestand: " + score);
        scoreLabel.getElement().getThemeList().add("badge");

        new Thread(() -> {
            try {
                Thread.sleep(1000);
                getUI().ifPresent(ui -> ui.access(() -> {
                    if (questionIndex < questions.size()) {
                        loadNextQuestion();
                    } else {
                        endQuiz();
                    }
                }));
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }).start();
    }

    private void endQuiz() {
        removeAll();
        long quizEndTime = System.currentTimeMillis();
        long durationInSeconds = (quizEndTime - quizStartTime) / 1000;

        var resultInfo = new Span(String.format("Dein Punktestand: %s Punkte", score));
        var timeInfo = new Span(String.format("Benötigte Zeit: %s Sekunden", durationInSeconds));
        resultInfo.getStyle().setMargin("10px 0").setFontSize("18px");
        timeInfo.getStyle().setMargin("10px 0").setFontSize("18px");
        resultInfo.getElement().getThemeList().add("badge success");
        timeInfo.getElement().getThemeList().add("badge success");

        add(resultInfo, timeInfo);

        saveLeaderboardEntry(durationInSeconds);
        showLeaderboard();
    }

    private void saveLeaderboardEntry(long durationInSeconds) {
        var entry = new Leaderboard();
        entry.setScore(score);
        entry.setTime(durationInSeconds);
        entry.setQuizTitle(currentQuiz.getTitle());
        entry.setCompletedAt(LocalDateTime.now());
        entry.setUser(user);

        leaderboardRepository.save(entry);
    }

    private void showLeaderboard() {
        var leaderboardTitle = new H1("Bestenliste");
        leaderboardTitle.getStyle().setMargin("20px 0").setTextAlign(Style.TextAlign.CENTER);

        Grid<Leaderboard> leaderboardGrid = new Grid<>(Leaderboard.class, false);
        leaderboardGrid.addColumn(Leaderboard::getUsername).setHeader("Benutzer").setSortable(true);
        leaderboardGrid.addColumn(Leaderboard::getScore).setHeader("Punktzahl").setSortable(true);
        leaderboardGrid.addColumn(Leaderboard::getTime).setHeader("Zeit (s)").setSortable(true);
        leaderboardGrid.addColumn(entry ->
                        entry.getCompletedAt().format(DateTimeFormatter.ofPattern("dd.MM.yyyy")))
                .setHeader("Abgeschlossen am")
                .setSortable(true);

        leaderboardGrid.setWidthFull();
        leaderboardGrid.addThemeVariants(GridVariant.LUMO_ROW_STRIPES);

        List<Leaderboard> leaderboardData = leaderboardRepository.findTop25ByQuizTitleOrderByScoreDesc(currentQuiz.getTitle());
        if (!leaderboardData.isEmpty()) {
            leaderboardGrid.setItems(leaderboardData);
            add(leaderboardTitle, leaderboardGrid);
        } else {
            var noData = new Span("Noch keine Einträge in der Bestenliste für dieses Quiz.");
            noData.getStyle().setTextAlign(Style.TextAlign.CENTER).setMargin("10px 0");
            add(leaderboardTitle, noData);
        }
    }
}