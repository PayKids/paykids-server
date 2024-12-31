package depth.main_project.PayKids_Server.domain.quiz.repository;

import depth.main_project.PayKids_Server.domain.quiz.entity.Quiz;
import depth.main_project.PayKids_Server.domain.quiz.entity.Submission;
import depth.main_project.PayKids_Server.domain.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SubmissionRepository extends JpaRepository<Submission, Long> {
    List<Submission> findAllByUser(User user);
    Optional<Submission> findByUserAndQuiz(User user, Quiz quiz);
}
