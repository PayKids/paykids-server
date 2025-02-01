package depth.main_project.PayKids_Server.domain.quiz.repository;

import depth.main_project.PayKids_Server.domain.quiz.entity.StageName;
import depth.main_project.PayKids_Server.domain.quiz.entity.UserStage;
import depth.main_project.PayKids_Server.domain.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserStageRepository extends JpaRepository<UserStage, Long> {
    Optional<UserStage> findUserStageByUserAndStageName(User user, StageName stageName);
}
