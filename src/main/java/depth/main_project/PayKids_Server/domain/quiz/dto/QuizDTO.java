package depth.main_project.PayKids_Server.domain.quiz.dto;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import depth.main_project.PayKids_Server.domain.quiz.entity.Quiz;
import depth.main_project.PayKids_Server.domain.quiz.entity.QuizType;
import lombok.*;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class QuizDTO {
    private Long id;
    private int stage;
    private int number;
    private QuizType quizType;
    private String question;
    private List<String> choices;
    private String answer;
    private String imageURL;

    public static QuizDTO fromEntity(Quiz quiz, ObjectMapper objectMapper) throws JsonProcessingException {
        try {
            QuizDTO dto = new QuizDTO();
            dto.id = quiz.getId();
            dto.stage = quiz.getStage();
            dto.number = quiz.getNumber();
            dto.quizType = quiz.getQuizType();
            dto.question = quiz.getQuestion();

            // JSON을 List<String>으로 변환
            dto.choices = objectMapper.readValue(quiz.getChoices(), new TypeReference<List<String>>() {});

            dto.answer = quiz.getAnswer();
            dto.imageURL = quiz.getImageURL();
            return dto;
        } catch (JsonProcessingException e) {
            // 예외 발생 시 GlobalExceptionHandler로 위임
            throw new RuntimeException("JSON 처리 중 오류 발생", e);
        }
    }

}
