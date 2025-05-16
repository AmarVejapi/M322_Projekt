package com.amar.quizmaster.repositories;

import com.amar.quizmaster.model.Question;
import com.amar.quizmaster.model.Quiz;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface QuestionRepository extends JpaRepository<Question, Long> {
    List<Question> findByQuizId(Long id);

    List<Question> findByQuiz(Quiz quiz);
}