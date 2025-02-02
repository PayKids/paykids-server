package depth.main_project.PayKids_Server.domain.achievement.entity;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Entity
@Getter
@Table(name = "Achievement")
public class Achievement {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private String description;
    private String imageURL;

    @Builder
    public Achievement(String name, String description) {
        this.name = name;
        this.description = description;
    }
}
