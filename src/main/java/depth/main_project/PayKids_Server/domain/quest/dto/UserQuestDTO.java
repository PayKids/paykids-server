package depth.main_project.PayKids_Server.domain.quest.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
@Getter
@Setter
@Builder
public class UserQuestDTO {
    private String name;
    private Boolean isComplete;
    private int count;
    private int maxCount;
}
