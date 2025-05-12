package com.amar.quizmaster.model;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
public class Leaderboard {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private int score;

    private double time;

    private String quizTitle;

    private LocalDateTime completedAt;

    @ManyToOne
    private User user;

    public Leaderboard() {
    }

    public Leaderboard(int score, double time, String quizTitle, LocalDateTime completedAt) {
        this.score = score;
        this.time = time;
        this.quizTitle = quizTitle;
        this.completedAt = completedAt;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public double getTime() {
        return time;
    }

    public void setTime(double time) {
        this.time = time;
    }

    public String getQuizTitle() {
        return quizTitle;
    }

    public void setQuizTitle(String quizTitle) {
        this.quizTitle = quizTitle;
    }

    public LocalDateTime getCompletedAt() {
        return completedAt;
    }

    public void setCompletedAt(LocalDateTime completedAt) {
        this.completedAt = completedAt;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getUsername() {
        return user != null ? user.getUsername() : "Unbekannter Benutzer";
    }
}