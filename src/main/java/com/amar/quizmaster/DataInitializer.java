package com.amar.quizmaster;

import com.amar.quizmaster.model.*;
import com.amar.quizmaster.repositories.QuestionRepository;
import com.amar.quizmaster.repositories.QuizRepository;
import com.amar.quizmaster.repositories.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Objects;

import static java.util.Objects.requireNonNull;

@Component
public class DataInitializer implements CommandLineRunner {

    private final QuestionRepository questionRepository;
    private final QuizRepository quizRepository;
    private final UserRepository userRepository;

    public DataInitializer(final QuestionRepository questionRepository,
                           final QuizRepository quizRepository,
                           final UserRepository userRepository) {
        this.questionRepository = requireNonNull(questionRepository);
        this.quizRepository = requireNonNull(quizRepository);
        this.userRepository = requireNonNull(userRepository);
    }

    @Override
    public void run(String... args) {
        /// User
        User adminUser = new User("admin", "admin", Role.ADMIN);
        userRepository.save(adminUser);

        /// Quizze und deren Questions
        Quiz quiz1 = new Quiz("Allgemeinwissen", "Quiz über das Allgemeinwissen", QuizType.LERNQUIZ, Difficulty.EASY, null);
        quizRepository.save(quiz1);

        Question question1_1 = new Question(
                "Was ist die Hauptstadt von Deutschland?",
                List.of("Berlin", "Hamburg", "München"),
                0,
                quiz1
        );

        Question question1_2 = new Question(
                "Welche Programmiersprache wird häufig in Android verwendet?",
                List.of("Java", "C++", "Python"),
                0,
                quiz1
        );

        questionRepository.save(question1_1);
        questionRepository.save(question1_2);

        /// ----------

        Quiz quiz2 = new Quiz("Allgemeinwissen", "Quiz über das Allgemeinwissen", QuizType.TESTQUIZ, Difficulty.MEDIUM, "abc");
        quizRepository.save(quiz2);

        Question question2_1 = new Question(
                "Was ist die Hauptstadt von Deutschland?",
                List.of("Berlin", "Hamburg", "München"),
                0,
                quiz2
        );

        Question question2_2 = new Question(
                "Welche Programmiersprache wird häufig in Android verwendet?",
                List.of("Java", "C++", "Python"),
                0,
                quiz2
        );

        questionRepository.save(question2_1);
        questionRepository.save(question2_2);

        /// ----------
    }
}