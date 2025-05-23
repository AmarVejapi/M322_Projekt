package com.amar.quizmaster.model;

import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.List;

@Entity
public class Quiz {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;
    private String description;

    @Enumerated(EnumType.STRING)
    private QuizType type;

    @Enumerated(EnumType.STRING)
    private Difficulty difficulty;

    @OneToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL, mappedBy = "quiz", orphanRemoval = true)
    private List<Question> questions;

    private String accessCode;

    @ManyToOne
    private User creator;

    private LocalDateTime createdAt;

    public Quiz() {
    }

    public Quiz(String title, String description, QuizType type, Difficulty difficulty, String accessCode, User creator, LocalDateTime createdAt) {
        this.title = title;
        this.description = description;
        this.type = type;
        this.difficulty = difficulty;
        this.accessCode = accessCode;
        this.creator = creator;
        this.createdAt = createdAt;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public QuizType getType() {
        return type;
    }

    public void setType(QuizType type) {
        this.type = type;
    }

    public Difficulty getDifficulty() {
        return difficulty;
    }

    public void setDifficulty(Difficulty difficulty) {
        this.difficulty = difficulty;
    }

    public List<Question> getQuestions() {
        return questions;
    }

    public void setQuestions(List<Question> questions) {
        this.questions = questions;
    }

    public String getAccessCode() {
        return accessCode;
    }

    public void setAccessCode(String accessCode) {
        this.accessCode = accessCode;
    }

    public User getCreator() {
        return creator;
    }

    public void setCreator(User creator) {
        this.creator = creator;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}