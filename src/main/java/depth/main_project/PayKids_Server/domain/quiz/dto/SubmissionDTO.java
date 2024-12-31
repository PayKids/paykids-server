package depth.main_project.PayKids_Server.domain.quiz.dto;

import depth.main_project.PayKids_Server.domain.quiz.entity.Quiz;
import depth.main_project.PayKids_Server.domain.quiz.entity.Status;
import depth.main_project.PayKids_Server.domain.user.entity.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@AllArgsConstructor
@Getter
@Setter
@Builder
public class SubmissionDTO {
    private Long id;
    private Status status;
    private LocalDateTime submitted;
    private Boolean isAnswerTrue;
    private User user;
    private Quiz quiz;
}
