package com.amar.quizmaster.repositories;

import com.amar.quizmaster.model.Leaderboard;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LeaderboardRepository extends JpaRepository<Leaderboard, Long> {
    List<Leaderboard> findTop25ByQuizTitleOrderByScoreDesc(String quizTitle);
}