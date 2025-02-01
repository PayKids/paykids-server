package depth.main_project.PayKids_Server.domain.quiz.repository;

import depth.main_project.PayKids_Server.domain.quiz.entity.Quiz;
import depth.main_project.PayKids_Server.domain.quiz.entity.Submission;
import depth.main_project.PayKids_Server.domain.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SubmissionRepository extends JpaRepository<Submission, Long> {
    List<Submission> findAllByUser(User user);
    List<Submission> findAllByUserAndIsAnswerTrueTrue(User user);
    Optional<Submission> findByUserAndQuiz(User user, Quiz quiz);

    @Query("SELECT q.stage FROM Submission s JOIN s.quiz q WHERE s.id = :submissionId")
    Integer findQuizStageBySubmissionId(@Param("submissionId") Long submissionId);

    @Query("SELECT q.count FROM Submission s JOIN s.quiz q WHERE s.id = :submissionId")
    Integer findQuizCountBySubmissionId(@Param("submissionId") Long submissionId);

    @Query("SELECT q.number FROM Submission s JOIN s.quiz q WHERE s.id = :submissionId")
    Integer findQuizNumberBySubmissionId(@Param("submissionId") Long submissionId);
}
