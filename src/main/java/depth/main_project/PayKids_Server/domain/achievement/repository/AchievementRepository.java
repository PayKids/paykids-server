package depth.main_project.PayKids_Server.domain.achievement.repository;

import depth.main_project.PayKids_Server.domain.achievement.entity.Achievement;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AchievementRepository extends JpaRepository<Achievement, Long> {
    Optional<Achievement> findById(long id);
}
