package depth.main_project.PayKids_Server.domain.quiz.entity;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Entity
@Getter
@Setter
@Table(name = "Quiz")
public class Quiz {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private int stage;
    private int number;
    private int count;
    private QuizType quizType;
    private String question;

    @Column(columnDefinition = "JSON", nullable = true)
    private String choices;
    private String answer;

    @Column(columnDefinition = "JSON", nullable = true)
    private String imageURL;

    @Builder
    public Quiz(int stage, int number, int count, QuizType quizType, String question, String choices, String answer, String imageURL) {
        this.stage = stage;
        this.number = number;
        this.count = count;
        this.quizType = quizType;
        this.question = question;
        this.choices = choices;
        this.answer = answer;
        this.imageURL = imageURL;
    }
}
