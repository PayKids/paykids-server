package depth.main_project.PayKids_Server.domain.achievement.dto;

import depth.main_project.PayKids_Server.domain.user.entity.User;
import lombok.*;
import org.springframework.context.ApplicationEvent;

@Getter
@Setter
public class UserAchievementUpdateEvent extends ApplicationEvent{
    private final User user;
    private final Long achievementId;

    public UserAchievementUpdateEvent(User user, Long achievementId) {
        super(user);
        this.user = user;
        this.achievementId = achievementId;
    }

    public User getUser() {
        return user;
    }

    public Long getAchievementId() {
        return achievementId;
    }
}
