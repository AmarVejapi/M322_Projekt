package com.amar.quizmaster.views;

import com.amar.quizmaster.model.Quiz;
import com.amar.quizmaster.model.QuizType;
import com.amar.quizmaster.repositories.QuizRepository;
import com.amar.quizmaster.utils.Notificator;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.confirmdialog.ConfirmDialog;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

import java.util.Optional;

import static java.util.Objects.requireNonNull;

@Route(value = "TestQuizOverview", layout = MainLayout.class)
@PageTitle("Test Quiz Übersicht")
public class TestQuizStartView extends VerticalLayout {

    private final QuizRepository quizRepository;

    public TestQuizStartView(final QuizRepository quizRepository) {
        this.quizRepository = requireNonNull(quizRepository);

        setHeightFull();

        var codeField = new TextField();
        codeField.setPlaceholder("Code eingeben ...");
        codeField.setTooltipText("Bitte gib den Zugriffs-Code für das Test Quiz ein");

        var submitButton = new Button("Quiz finden");
        submitButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        submitButton.addClickListener(event -> {
            String code = codeField.getValue();
            if (validateCode(code)) {
                navigateToTestQuiz(code);
            } else {
                Notificator.notification("Ungültiger Code. Bitte versuche es erneut.");
            }
        });

        var title = new HorizontalLayout(codeField, submitButton);
        title.getStyle().setPaddingTop("15px");

        add(title);
        setAlignItems(Alignment.CENTER);
    }

    private boolean validateCode(String code) {
        return quizRepository.findAll().stream()
                .filter(quiz -> quiz.getType() == QuizType.TESTQUIZ)
                .anyMatch(quiz -> code.equals(quiz.getAccessCode()));
    }

    private void navigateToTestQuiz(String accessCode) {
        Optional<Quiz> matchingQuiz = quizRepository.findAll().stream()
                .filter(quiz -> quiz.getType() == QuizType.TESTQUIZ)
                .filter(quiz -> accessCode.equals(quiz.getAccessCode()))
                .findFirst();

        if (matchingQuiz.isPresent()) {
            Quiz quiz = matchingQuiz.get();

            var dialog = new ConfirmDialog();
            dialog.setHeader("Quiz gefunden: " + quiz.getTitle());
            dialog.setText("Beschreibung: " + quiz.getDescription() + "\nSchwierigkeit: " + quiz.getDifficulty());
            dialog.setCancelable(true);
            dialog.setConfirmButton("Fortfahren", event -> getUI().ifPresent(ui -> ui.navigate(TestQuizView.class, quiz.getId())));
            dialog.open();
        } else {
            Notificator.notification("Kein passendes Quiz mit dem Zugangscode gefunden!");
        }
    }
}