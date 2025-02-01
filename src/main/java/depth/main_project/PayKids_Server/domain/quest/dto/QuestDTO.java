package depth.main_project.PayKids_Server.domain.quest.dto;

import depth.main_project.PayKids_Server.domain.quest.entity.QuestType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
@Getter
@Setter
@Builder
public class QuestDTO {
    private String questName;
    private int realCount;
    private QuestType questType;
}
