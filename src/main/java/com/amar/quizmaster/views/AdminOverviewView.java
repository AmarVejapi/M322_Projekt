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
import com.vaadin.flow.component.html.H1;
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

        var title = new H1("Ihre Quizze");
        var createQuizButton = new Button("Neues Quiz erstellen", event -> openQuizCreationDialog());
        createQuizButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        createQuizButton.getStyle().setMarginBottom("15px");

        var searchField = new TextField();
        searchField.setPlaceholder("Quiz suchen ...");
        searchField.setTooltipText("Nach Quiztitel suchen");
        searchField.setClearButtonVisible(true);
        searchField.addValueChangeListener(event -> filterQuizList(event.getValue()));

        var topBar = new HorizontalLayout(title, searchField, createQuizButton);
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
            List<Quiz> filteredQuizzes = allQuizzes.stream().filter(quiz -> quiz.getTitle().toLowerCase().contains(filterText.toLowerCase())).toList();
            displayQuizList(filteredQuizzes);
        }
    }

    private void displayQuizList(List<Quiz> quizzes) {
        quizListLayout.removeAll();

        if (quizzes.isEmpty()) {
            quizListLayout.add(new Span("Keine Quizze gefunden."));
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

                var accessCodeLabel = new Span();

                var startTestQuizButton = new Button("Access-Code generieren");
                if (quiz.getType() == QuizType.LERNQUIZ) {
                    startTestQuizButton.setVisible(false);
                }
                startTestQuizButton.addClickListener(event -> {
                    String accessCode = generateAndSaveAccessCode(quiz);
                    accessCodeLabel.setText(String.format("Access-Code: %s", accessCode));
                    accessCodeLabel.getElement().getThemeList().add("badge success");
                    Notificator.notification(String.format("Ihr neuer Access-Code lautet: %s (gültig für 1 Stunde)", accessCode));
                    startAccessCodeExpirationTimer();
                });
                startTestQuizButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY, ButtonVariant.LUMO_SUCCESS);

                var buttonLayout = new HorizontalLayout(editButton, deleteButton, startTestQuizButton, accessCodeLabel);
                buttonLayout.setAlignItems(Alignment.BASELINE);

                var quizLayout = new VerticalLayout(quizTitle, description, quizTypeLayout, buttonLayout);

                quizLayout.getStyle().setBackgroundColor("lightgray").setBorderRadius("5px").setPadding("10px").setMarginBottom("10px");

                quizListLayout.add(quizLayout);
            }
        }
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