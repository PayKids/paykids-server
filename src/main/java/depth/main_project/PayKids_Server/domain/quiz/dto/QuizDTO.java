package depth.main_project.PayKids_Server.domain.quiz.dto;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import depth.main_project.PayKids_Server.domain.quiz.entity.Quiz;
import depth.main_project.PayKids_Server.domain.quiz.entity.QuizType;
import lombok.*;

import java.util.*;

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
    private Map<String, String> choices;
    private String answer;
    private Map<String, String> imageURL;

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
                List<String> list = objectMapper.readValue(quiz.getChoices(), new TypeReference<List<String>>() {});

                Map<String, String> map = new LinkedHashMap<>();
                for (int i = 0; i < list.size(); i++) {
                    // 알파벳 A부터 시작 (A=65 in ASCII)
                    String key = String.valueOf((char) ('A' + i));
                    map.put(key, list.get(i));
                }

                dto.choices = map;

            } else {
                dto.choices = new LinkedHashMap<>();
            }

            dto.answer = quiz.getAnswer();

            // null이나 빈 문자열 처리
            if (quiz.getImageURL() != null && !quiz.getImageURL().isEmpty()) {
                List<String> list = objectMapper.readValue(quiz.getImageURL(), new TypeReference<List<String>>() {});

                Map<String, String> map = new LinkedHashMap<>();
                for (int i = 0; i < list.size(); i++) {
                    // 알파벳 A부터 시작 (A=65 in ASCII)
                    String key = String.valueOf((char) ('A' + i));
                    map.put(key, list.get(i));
                }

                dto.choices = map;

            } else {
                dto.choices = new LinkedHashMap<>();
            }

            return dto;
        } catch (JsonProcessingException e) {
            // 예외 발생 시 GlobalExceptionHandler로 위임
            throw new RuntimeException("JSON 처리 중 오류 발생", e);
        }
    }

}
