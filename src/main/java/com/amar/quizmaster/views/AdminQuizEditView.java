package com.amar.quizmaster.views;

import com.amar.quizmaster.model.Question;
import com.amar.quizmaster.model.Quiz;
import com.amar.quizmaster.repositories.QuestionRepository;
import com.amar.quizmaster.repositories.QuizRepository;
import com.amar.quizmaster.utils.Notificator;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.html.H4;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.radiobutton.RadioButtonGroup;
import com.vaadin.flow.component.radiobutton.RadioGroupVariant;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.BeforeEvent;
import com.vaadin.flow.router.HasUrlParameter;
import com.vaadin.flow.router.Route;

import java.util.List;
import java.util.stream.IntStream;

import static java.util.Objects.requireNonNull;

@Route(value = "AdminQuizEditView", layout = MainLayout.class)
public class AdminQuizEditView extends VerticalLayout implements HasUrlParameter<Long> {

    private final QuizRepository quizRepository;
    private final QuestionRepository questionRepository;
    private Quiz quiz;

    public AdminQuizEditView(final QuizRepository quizRepository, final QuestionRepository questionRepository) {
        this.quizRepository = requireNonNull(quizRepository);
        this.questionRepository = requireNonNull(questionRepository);

        setPadding(true);
        add(new H1("Quiz bearbeiten"));
    }

    @Override
    public void setParameter(BeforeEvent event, Long quizId) {
        this.quiz = quizRepository.findById(quizId).orElseThrow(() -> new IllegalArgumentException("Ungültige Quiz-ID: " + quizId));

        List<Question> questions = questionRepository.findByQuiz(this.quiz);

        quiz.setQuestions(questions);

        initializeEditor();
    }

    private void initializeEditor() {
        removeAll();

        var titleField = new TextField("Titel");
        titleField.setValue(quiz.getTitle());
        titleField.setWidthFull();

        var descriptionField = new TextArea("Beschreibung");
        descriptionField.setValue(quiz.getDescription());
        descriptionField.setWidthFull();

        var saveQuizButton = new Button("Speichern");
        saveQuizButton.addClickListener(e -> {
            quiz.setTitle(titleField.getValue());
            quiz.setDescription(descriptionField.getValue());
            quizRepository.save(quiz);
            Notificator.notification("Quiz-Daten aktualisiert");
        });
        saveQuizButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);

        add(new H1("Quiz bearbeiten"), titleField, descriptionField, saveQuizButton);

        add(new H3("Fragen bearbeiten"));

        for (Question question : quiz.getQuestions()) {
            add(createQuestionEditor(question));
        }

        var addQuestionButton = new Button("Neue Frage hinzufügen", e -> addNewQuestion());
        addQuestionButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        add(addQuestionButton);
    }

    private void addNewQuestion() {
        var newQuestion = new Question("Neue Frage", List.of("Option 1", "Option 2", "Option 3"), 0, quiz);

        quiz.getQuestions().add(newQuestion);
        questionRepository.save(newQuestion);

        add(createQuestionEditor(newQuestion));
        Notificator.notification("Neue Frage hinzugefügt");

        UI.getCurrent().getPage().reload();
    }

    private VerticalLayout createQuestionEditor(Question question) {
        var questionLayout = new VerticalLayout();
        questionLayout.getStyle()
                .setBackgroundColor("#f5f5f5")
                .setPadding("15px")
                .setBorderRadius("8px")
                .setMarginBottom("10px");

        var questionField = new TextArea("Frage");
        questionField.setValue(question.getText());
        questionField.setWidthFull();

        var optionsLayout = new VerticalLayout();
        optionsLayout.setSpacing(false);

        for (int i = 0; i < question.getOptions().size(); i++) {
            int index = i;
            var optionField = new TextField("Option " + (i + 1));
            optionField.setValue(question.getOptions().get(i));
            optionField.setWidthFull();
            optionField.addValueChangeListener(e -> question.getOptions().set(index, e.getValue()));
            optionsLayout.add(optionField);
        }

        var correctAnswerGroup = new RadioButtonGroup<Integer>();
        correctAnswerGroup.addThemeVariants(RadioGroupVariant.LUMO_VERTICAL);
        correctAnswerGroup.setLabel("Richtige Antwort");
        correctAnswerGroup.setItems(IntStream.range(0, question.getOptions().size()).boxed().toList());
        correctAnswerGroup.setItemLabelGenerator(index -> "Option " + (index + 1));
        correctAnswerGroup.setValue(question.getCorrectAnswerIndex());

        correctAnswerGroup.addValueChangeListener(event -> question.setCorrectAnswerIndex(event.getValue()));

        var saveButton = new Button("Speichern", event -> {
            question.setText(questionField.getValue());
            questionRepository.save(question);
            Notificator.notification("Frage erfolgreich gespeichert");
        });
        saveButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);

        var deleteButton = new Button("Löschen", event -> deleteQuestion(question, questionLayout));
        deleteButton.addThemeVariants(ButtonVariant.LUMO_ERROR);

        questionLayout.add(
                questionField,
                new H4("Antwortoptionen"),
                optionsLayout,
                correctAnswerGroup,
                new HorizontalLayout(saveButton, deleteButton)
        );

        return questionLayout;
    }

    private void deleteQuestion(Question question, VerticalLayout component) {
        quiz.getQuestions().remove(question);
        questionRepository.delete(question);
        remove(component);
        Notificator.notification("Frage gelöscht");
    }
}