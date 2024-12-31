package depth.main_project.PayKids_Server.domain.quiz.repository;

import depth.main_project.PayKids_Server.domain.quiz.entity.Quiz;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface QuizRepository extends JpaRepository<Quiz, Long> {
    Optional<Quiz> findByNumberAndStage(int number, int stage);
}
