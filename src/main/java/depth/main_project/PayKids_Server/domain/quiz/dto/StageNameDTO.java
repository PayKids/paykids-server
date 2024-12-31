package depth.main_project.PayKids_Server.domain.quiz.dto;

import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class StageNameDTO {
    private Long id;
    private String stageName;
    private int stageNum;
    private int stageCount;
}
