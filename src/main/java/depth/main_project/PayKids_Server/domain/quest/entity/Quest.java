package depth.main_project.PayKids_Server.domain.quest.entity;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Getter
@Entity
@Table(name = "Quest")
public class Quest {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private QuestType questType;
    private int realCount;
    private String name;

    @Builder
    public Quest(QuestType questType, int realCount, String name) {
        this.questType = questType;
        this.realCount = realCount;
        this.name = name;
    }
}
