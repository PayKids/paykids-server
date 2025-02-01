package depth.main_project.PayKids_Server.domain.achievement.service;

import depth.main_project.PayKids_Server.domain.achievement.entity.Achievement;
import depth.main_project.PayKids_Server.domain.achievement.entity.UserAchievement;
import depth.main_project.PayKids_Server.domain.achievement.dto.UserAchievementUpdateEvent;
import depth.main_project.PayKids_Server.domain.achievement.repository.AchievementRepository;
import depth.main_project.PayKids_Server.domain.achievement.repository.UserAchievementRepository;
import depth.main_project.PayKids_Server.domain.user.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

import java.util.Optional;

@RequiredArgsConstructor
@Component
public class AchievementManager {
    private final UserAchievementRepository achievementUserRepository;
    private final AchievementRepository achievementRepository;

    @TransactionalEventListener(phase = TransactionPhase.BEFORE_COMMIT)
    public void updateAchievement(UserAchievementUpdateEvent event) {
        User user = event.getUser();
        Long id = event.getAchievementId();

        Optional<Achievement> achievement = achievementRepository.findById(id);

        Optional<UserAchievement> userAchievement = achievementUserRepository.findByAchievementAndUser(achievement.get(), Optional.ofNullable(user));

        if (userAchievement.isEmpty()) {
            UserAchievement realUserAchievement = UserAchievement.builder()
                .achievement(achievement.get())
                .user(user)
                .isCompleted(true)
                .build();

            achievementUserRepository.save(realUserAchievement);
        }
    }
}
