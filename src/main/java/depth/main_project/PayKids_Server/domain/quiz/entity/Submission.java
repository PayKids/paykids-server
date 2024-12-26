package depth.main_project.PayKids_Server.domain.quiz.entity;

import depth.main_project.PayKids_Server.domain.user.entity.User;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@NoArgsConstructor
@Entity
@Getter
@Setter
@Table(name = "Submission")
public class Submission {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    private Status status;
    private LocalDateTime submitDateTime;
    private Boolean isAnswerTrue;

    @ManyToOne (fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne (fetch = FetchType.LAZY)
    @JoinColumn(name = "quiz_id")
    private Quiz quiz;

    @Builder
    public Submission(Status status, LocalDateTime submitDateTime, Boolean isAnswerTrue, User user, Quiz quiz) {
        this.status = status;
        this.submitDateTime = submitDateTime;
        this.isAnswerTrue = isAnswerTrue;
        this.user = user;
        this.quiz = quiz;
    }
}
