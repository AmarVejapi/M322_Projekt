package com.amar.quizmaster.views;

import com.amar.quizmaster.model.Question;
import com.amar.quizmaster.model.Quiz;
import com.amar.quizmaster.repositories.QuestionRepository;
import com.amar.quizmaster.repositories.QuizRepository;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.BeforeEvent;
import com.vaadin.flow.router.HasUrlParameter;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

import java.util.List;

@Route(value = "LernQuiz", layout = MainLayout.class)
@PageTitle("LernQuiz")
public class LernQuizView extends VerticalLayout implements HasUrlParameter<Long> {

    private final QuestionRepository questionRepository;
    private final QuizRepository quizRepository;
    private final H1 titleLabel = new H1();
    private final H1 questionLabel = new H1();
    private final Span scoreLabel = new Span();

    private int score = 0;
    private int questionIndex = 0;
    private List<Question> questions;

    public LernQuizView(QuestionRepository questionRepository, QuizRepository quizRepository) {
        this.questionRepository = questionRepository;
        this.quizRepository = quizRepository;

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
        Quiz quiz = quizRepository.findById(quizId)
                .orElseThrow(() -> new IllegalArgumentException("Quiz nicht gefunden"));
        this.questions = questionRepository.findByQuizId(quizId);

        titleLabel.setText(quiz.getTitle());
        titleLabel.getStyle()
                .setFontSize("24px")
                .setFontWeight("bold")
                .set("text-align", "center");
        scoreLabel.setText("Punktestand: " + score);

        if (!questions.isEmpty()) {
            questionLabel.setText(questions.get(questionIndex).getText());
            questionLabel.getStyle()
                    .setFontSize("20px")
                    .set("text-align", "center");
            loadQuestionButtons();
        } else {
            questionLabel.setText("Keine Fragen in diesem Quiz.");
        }
    }

    private void loadQuestionButtons() {
        Question currentQuestion = questions.get(questionIndex);

        getChildren().filter(component -> component instanceof Button).forEach(this::remove);

        currentQuestion.getOptions().forEach(option -> {
            Button answerButton = new Button(option, event -> checkAnswer(currentQuestion.getOptions().indexOf(option)));
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
            answerButton.addClickListener(click -> answerButton.getStyle().setBackgroundColor("#0056b3"));
            add(answerButton);
        });
    }

    private void checkAnswer(int selectedAnswer) {
        Question currentQuestion = questions.get(questionIndex);

        if (selectedAnswer == currentQuestion.getCorrectAnswerIndex()) {
            score++;
            Notification.show("Richtig! Punktestand: " + score, 3000, Notification.Position.MIDDLE);
        } else {
            Notification.show("Falsch! Die richtige Antwort war: " +
                    currentQuestion.getOptions().get(currentQuestion.getCorrectAnswerIndex()), 3000, Notification.Position.MIDDLE);
        }

        questionIndex++;
        scoreLabel.setText("Punktestand: " + score);

        if (questionIndex < questions.size()) {
            loadNextQuestion();
        } else {
            endQuiz();
        }
    }

    private void loadNextQuestion() {
        questionLabel.setText(questions.get(questionIndex).getText());
        loadQuestionButtons();
    }

    private void endQuiz() {
        removeAll();
        add(new H1("Quiz beendet!"));
        add(new H1("Dein Punktestand: " + score));
    }
}