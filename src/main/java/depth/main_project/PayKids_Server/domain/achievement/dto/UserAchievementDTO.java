package depth.main_project.PayKids_Server.domain.achievement.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
@Getter
@Setter
@Builder
public class UserAchievementDTO {
    private Boolean isCompleted;
    private String name;
}
