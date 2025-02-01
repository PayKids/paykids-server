package depth.main_project.PayKids_Server.domain.achievement.entity;

import depth.main_project.PayKids_Server.domain.user.entity.User;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@NoArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "UserAchievement")
public class UserAchievement {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Boolean isCompleted;
    private LocalDate localDate;

    @ManyToOne (fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne (fetch = FetchType.LAZY)
    @JoinColumn(name = "achievement_id")
    private Achievement achievement;

    @Builder
    public UserAchievement (Boolean isCompleted, User user, Achievement achievement) {
        this.isCompleted = isCompleted;
        this.user = user;
        this.achievement = achievement;
    }
}
