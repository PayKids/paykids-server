package depth.main_project.PayKids_Server.domain.achievement.repository;

import depth.main_project.PayKids_Server.domain.achievement.entity.Achievement;
import depth.main_project.PayKids_Server.domain.achievement.entity.UserAchievement;
import depth.main_project.PayKids_Server.domain.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UserAchievementRepository extends JpaRepository<UserAchievement, Long> {
    Optional<UserAchievement> findByAchievementAndUser(Achievement achievement, Optional<User> user);
    List<UserAchievement> findAllByUser(User user);
}
