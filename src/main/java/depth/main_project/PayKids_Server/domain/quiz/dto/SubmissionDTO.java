package depth.main_project.PayKids_Server.domain.quiz.dto;

import depth.main_project.PayKids_Server.domain.quiz.entity.Status;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@AllArgsConstructor
@Getter
@Builder
public class SubmissionDTO {
    private Long id;
    private Status status;
    private LocalDateTime submitted;
    private Boolean isAnswerTrue;
}
