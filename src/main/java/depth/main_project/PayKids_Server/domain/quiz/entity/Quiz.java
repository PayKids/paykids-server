package depth.main_project.PayKids_Server.domain.quiz.entity;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Entity
@Getter
@Table(name = "Quiz")
public class Quiz {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private int stage;
    private int number;
    private QuizType quizType;
    private String question;

    @Column(columnDefinition = "JSON", nullable = true)
    private String choices;
    private String answer;

    @Column(nullable = true)
    private String imageURL;

    @Builder
    public Quiz(int stage, int number, QuizType quizType, String question, String choices, String answer, String imageURL) {
        this.stage = stage;
        this.number = number;
        this.quizType = quizType;
        this.question = question;
        this.choices = choices;
        this.answer = answer;
        this.imageURL = imageURL;
    }
}
