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

        var title = new H1("Admin-Bereich: Ihre Quizze");
        var createQuizButton = new Button("Neues Quiz erstellen", event -> openQuizCreationDialog());
        createQuizButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        createQuizButton.getStyle().setMarginBottom("15px");

        var searchField = new TextField();
        searchField.setPlaceholder("Quiz suchen ...");
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
            List<Quiz> filteredQuizzes = allQuizzes.stream()
                    .filter(quiz -> quiz.getTitle().toLowerCase().contains(filterText.toLowerCase()))
                    .toList();
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
                var description = new Span("Beschreibung: " + quiz.getDescription());
                var type = new Span("Typ: " + quiz.getType());

                var editButton = new Button("Bearbeiten", event -> editQuiz(quiz));
                var deleteButton = new Button("Löschen", event -> deleteQuizWithConfirmation(quiz));

                editButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
                deleteButton.addThemeVariants(ButtonVariant.LUMO_ERROR);

                var quizLayout = new VerticalLayout(quizTitle, description, type, new HorizontalLayout(editButton, deleteButton));
                quizLayout.getStyle()
                        .setBackgroundColor("lightgray")
                        .setBorderRadius("5px")
                        .setPadding("10px")
                        .setMarginBottom("10px");

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

        var saveButton = new Button("Speichern", event -> {
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
        confirmDialog.add(new Span("Möchten Sie das Quiz '" + quiz.getTitle() + "' wirklich löschen?"));

        var confirmButton = new Button("Löschen", event -> {
            quizRepository.delete(quiz);
            allQuizzes.remove(quiz);
            displayQuizList(allQuizzes);
            confirmDialog.close();
            Notificator.notification("Quiz gelöscht");
        });
        confirmButton.addThemeVariants(ButtonVariant.LUMO_ERROR);

        var cancelButton = new Button("Abbrechen", event -> confirmDialog.close());
        confirmDialog.getFooter().add(cancelButton, confirmButton);

        confirmDialog.open();
    }

    // ToDo: accessCode-Option
}