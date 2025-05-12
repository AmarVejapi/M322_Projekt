package com.amar.quizmaster.repositories;

import com.amar.quizmaster.model.Quiz;
import com.amar.quizmaster.model.QuizType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface QuizRepository extends JpaRepository<Quiz, Long> {
    List<Quiz> findByType(QuizType type);
}
