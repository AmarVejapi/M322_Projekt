package com.amar.quizmaster.model;

import jakarta.persistence.*;

import java.util.List;

@Entity
public class Question {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String text;

    @ElementCollection(fetch = FetchType.EAGER)
    private List<String> options;

    private int correctAnswerIndex;

    @ManyToOne
    private Quiz quiz;

    public Question() {
    }

    public Question(String text, List<String> options, int correctAnswerIndex, Quiz quiz) {
        this.text = text;
        this.options = options;
        this.correctAnswerIndex = correctAnswerIndex;
        this.quiz = quiz;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getText() {
        return text;
    }

    public List<String> getOptions() {
        return options;
    }

    public int getCorrectAnswerIndex() {
        return correctAnswerIndex;
    }

    public Quiz getQuiz() {
        return quiz;
    }

    public void setQuiz(Quiz quiz) {
        this.quiz = quiz;
    }
}