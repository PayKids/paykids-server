package depth.main_project.PayKids_Server.domain.quiz.dto;

import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class QuizClearedDTO {
    private String message;
    private Boolean isCleared;
}
