package depth.main_project.PayKids_Server.domain.quiz.dto;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import depth.main_project.PayKids_Server.domain.quiz.entity.Quiz;
import depth.main_project.PayKids_Server.domain.quiz.entity.QuizType;
import lombok.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class QuizDTO {
    private Long id;
    private int stage;
    private int number;
    private int count;
    private QuizType quizType;
    private String question;
    private List<String> choices;
    private String answer;
    private List<String> imageURL;

    public static QuizDTO fromEntity(Quiz quiz, ObjectMapper objectMapper) {
        try {
            QuizDTO dto = new QuizDTO();
            dto.id = quiz.getId();
            dto.stage = quiz.getStage();
            dto.count = quiz.getCount();
            dto.number = quiz.getNumber();
            dto.quizType = quiz.getQuizType();
            dto.question = quiz.getQuestion();

            // null이나 빈 문자열 처리
            if (quiz.getChoices() != null && !quiz.getChoices().isEmpty()) {
                dto.choices = objectMapper.readValue(quiz.getChoices(), new TypeReference<List<String>>() {});

            } else {
                dto.choices = new ArrayList<>();
            }

            dto.answer = quiz.getAnswer();

            // null이나 빈 문자열 처리
            if (quiz.getImageURL() != null && !quiz.getImageURL().isEmpty()) {
                dto.imageURL = objectMapper.readValue(quiz.getImageURL(), new TypeReference<List<String>>() {});
            } else {
                dto.choices = new ArrayList<>();
            }

            return dto;
        } catch (JsonProcessingException e) {
            // 예외 발생 시 GlobalExceptionHandler로 위임
            throw new RuntimeException("JSON 처리 중 오류 발생", e);
        }
    }

}
