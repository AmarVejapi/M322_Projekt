package com.amar.quizmaster.views;

import com.amar.quizmaster.model.Quiz;
import com.amar.quizmaster.model.QuizType;
import com.amar.quizmaster.model.User;
import com.amar.quizmaster.repositories.QuizRepository;
import com.amar.quizmaster.repositories.UserRepository;
import com.amar.quizmaster.utils.Notificator;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.select.Select;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

import java.util.List;

import static java.util.Objects.requireNonNull;

@Route(value = "AdminOverview", layout = MainLayout.class)
@PageTitle("Admin Bereich")
public class AdminOverviewView extends VerticalLayout {

    private final QuizRepository quizRepository;
    private final UserRepository userRepository;
    private final User user;
    private final List<Quiz> allQuizzes;
    private final VerticalLayout quizListLayout;

    public AdminOverviewView(final QuizRepository quizRepository, final UserRepository userRepository) {
        this.quizRepository = requireNonNull(quizRepository);
        this.userRepository = requireNonNull(userRepository);
        this.user = LoginView.currentUser;

        setWidthFull();
        setSpacing(false);

        var createQuizButton = new Button("Neues Quiz erstellen", event -> openQuizCreationDialog());
        createQuizButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        createQuizButton.getStyle().setMarginBottom("15px");

        var searchField = new TextField();
        searchField.setPlaceholder("Quizze suchen ...");
        searchField.setTooltipText("Quizze nach Titel suchen");
        searchField.setClearButtonVisible(true);
        searchField.addValueChangeListener(event -> filterQuizList(event.getValue()));

        var topBar = new HorizontalLayout(searchField, createQuizButton);
        topBar.setAlignItems(FlexComponent.Alignment.BASELINE);
        topBar.setJustifyContentMode(FlexComponent.JustifyContentMode.BETWEEN);
        topBar.setWidthFull();

        quizListLayout = new VerticalLayout();
        quizListLayout.setPadding(false);

        allQuizzes = quizRepository.findByCreator(user);
        displayQuizList(allQuizzes);

        add(topBar, quizListLayout);
    }

    private void filterQuizList(String filterText) {
        if (filterText == null || filterText.isEmpty()) {
            displayQuizList(allQuizzes);
        } else {
            displayQuizList(allQuizzes.stream().filter(quiz -> quiz.getTitle().toLowerCase().contains(filterText.toLowerCase())).toList());
        }
    }

    private void displayQuizList(List<Quiz> quizzes) {
        quizListLayout.removeAll();

        if (quizzes.isEmpty()) {
            var noQuizzesFoundLabel = new Span("Keine Quizze gefunden");
            noQuizzesFoundLabel.getElement().getThemeList().add("badge error");
            quizListLayout.add(noQuizzesFoundLabel);
        } else {
            for (Quiz quiz : quizzes) {
                var quizTitle = new Span("Titel: " + quiz.getTitle());
                quizTitle.getStyle().setFontSize("1.5em").setFontWeight("bold");
                var description = new Span("Beschreibung: " + quiz.getDescription());
                var quizType = new Span(quiz.getType().toString());
                quizType.getElement().getThemeList().add((quiz.getType() == QuizType.LERNQUIZ ? "badge" : "badge success"));
                var quizTypeLayout = new HorizontalLayout(new Span("Typ:"), quizType);

                var editButton = new Button("Bearbeiten", event -> editQuiz(quiz));
                var deleteButton = new Button("Löschen", event -> deleteQuizWithConfirmation(quiz));

                editButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
                deleteButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY, ButtonVariant.LUMO_ERROR);

                var accessCodeLabel = createAccessCodeLabel(quiz);
                var startTestQuizButton = createStartTestQuizButton(quiz, accessCodeLabel);

                var buttonLayout = new HorizontalLayout(editButton, deleteButton, startTestQuizButton, accessCodeLabel);
                buttonLayout.setAlignItems(Alignment.BASELINE);

                var quizLayout = new VerticalLayout(quizTitle, description, quizTypeLayout, buttonLayout);

                quizLayout.getStyle()
                        .setBackgroundColor("var(--lumo-contrast-10pct)")
                        .setBorderRadius("5px")
                        .setPadding("10px")
                        .setMarginBottom("10px");

                quizListLayout.add(quizLayout);
            }
        }
    }

    private Span createAccessCodeLabel(Quiz quiz) {
        var accessCodeLabel = new Span();
        accessCodeLabel.setVisible(false);

        if (quiz.getType() == QuizType.TESTQUIZ && quiz.getAccessCode() != null) {
            updateAccessCodeLabel(accessCodeLabel, quiz.getAccessCode());
        }
        return accessCodeLabel;
    }

    private void updateAccessCodeLabel(Span label, String accessCode) {
        label.setText(String.format("Access-Code: %s", accessCode));
        label.getElement().getThemeList().clear();
        label.getElement().getThemeList().add("badge success");
        label.setVisible(true);
    }

    private Button createStartTestQuizButton(Quiz quiz, Span accessCodeLabel) {
        var startTestQuizButton = new Button("Access-Code generieren");

        startTestQuizButton.setVisible(quiz.getType() == QuizType.TESTQUIZ);

        startTestQuizButton.addClickListener(event -> {
            String accessCode = generateAndSaveAccessCode(quiz);
            updateAccessCodeLabel(accessCodeLabel, accessCode);
            Notificator.notification(String.format("Ihr neuer Access-Code lautet: %s (gültig für 1 Stunde)", accessCode));
            startAccessCodeExpirationTimer();
        });

        startTestQuizButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY, ButtonVariant.LUMO_SUCCESS);
        return startTestQuizButton;
    }

    private void openQuizCreationDialog() {
        var dialog = new Dialog();
        dialog.setWidth("500px");
        dialog.setHeaderTitle("Neues Quiz erstellen");

        var titleField = new TextField("Titel");
        titleField.setWidthFull();

        var descriptionField = new TextArea("Beschreibung");
        descriptionField.setWidthFull();

        var typeField = new Select<QuizType>();
        typeField.setLabel("Quiz-Typ");
        typeField.setItems(QuizType.values());
        typeField.setItemLabelGenerator(QuizType::name);
        typeField.setWidthFull();

        var saveButton = new Button("Speichern");
        saveButton.addClickListener(event -> {
            if (titleField.isEmpty() || descriptionField.isEmpty() || typeField.isEmpty()) {
                Notificator.notification("Bitte füllen Sie alle Felder aus!");
            } else {
                saveNewQuiz(titleField.getValue(), descriptionField.getValue(), typeField.getValue());
                dialog.close();
            }
        });
        saveButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);

        var cancelButton = new Button("Abbrechen", event -> dialog.close());

        dialog.getFooter().add(cancelButton, saveButton);

        var dialogLayout = new VerticalLayout(titleField, descriptionField, typeField);
        dialogLayout.setSpacing(true);
        dialogLayout.setPadding(false);
        dialogLayout.getStyle().set("gap", "10px");

        dialog.add(dialogLayout);
        dialog.open();
    }

    private void saveNewQuiz(String title, String description, QuizType type) {
        Quiz newQuiz = new Quiz();
        newQuiz.setTitle(title);
        newQuiz.setDescription(description);
        newQuiz.setType(type);
        newQuiz.setCreator(user);

        quizRepository.save(newQuiz);
        allQuizzes.add(newQuiz);
        displayQuizList(allQuizzes);
    }

    private void editQuiz(Quiz quiz) {
        getUI().ifPresent(ui -> ui.navigate(AdminQuizEditView.class, quiz.getId()));
    }

    private void deleteQuizWithConfirmation(Quiz quiz) {
        var confirmDialog = new Dialog();
        confirmDialog.setHeaderTitle("Quiz löschen");
        confirmDialog.add(new Span(String.format("Möchten Sie das Quiz '%s' wirklich löschen?", quiz.getTitle())));

        var confirmButton = new Button("Löschen");
        confirmButton.addClickListener(event -> {
            quizRepository.delete(quiz);
            allQuizzes.remove(quiz);
            displayQuizList(allQuizzes);
            confirmDialog.close();
            Notificator.notification("Quiz gelöscht");
        });
        confirmButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY, ButtonVariant.LUMO_ERROR);

        var cancelButton = new Button("Abbrechen", event -> confirmDialog.close());
        cancelButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);

        var buttonLayout = new HorizontalLayout(cancelButton, confirmButton);
        buttonLayout.setAlignItems(Alignment.BASELINE);
        buttonLayout.setWidthFull();
        buttonLayout.setJustifyContentMode(FlexComponent.JustifyContentMode.BETWEEN);

        confirmDialog.getFooter().add(buttonLayout);

        confirmDialog.open();
    }

    private String generateAndSaveAccessCode(Quiz quiz) {
        String characters = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
        StringBuilder accessCode = new StringBuilder();
        for (int i = 0; i < 8; i++) {
            int randomIndex = (int) (Math.random() * characters.length());
            accessCode.append(characters.charAt(randomIndex));
        }

        quiz.setAccessCode(accessCode.toString());
        quizRepository.save(quiz);

        return accessCode.toString();
    }

    private void startAccessCodeExpirationTimer() {
        new Thread(() -> {
            try {
                Thread.sleep(3600000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }).start();
    }
}