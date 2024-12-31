package depth.main_project.PayKids_Server.domain.quiz.entity;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Getter
@Entity
@Table(name = "StageName")
public class StageName {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String stageName;
    private int stageNumber;
    private int stageCount;

    @Builder
    public StageName(String stageName, int stageNumber, int stageCount) {
        this.stageName = stageName;
        this.stageNumber = stageNumber;
        this.stageCount = stageCount;
    }
}
