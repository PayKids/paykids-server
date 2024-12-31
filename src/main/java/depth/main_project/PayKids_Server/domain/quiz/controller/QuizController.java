package depth.main_project.PayKids_Server.domain.quiz.controller;

import depth.main_project.PayKids_Server.domain.quiz.dto.QuizClearedDTO;
import depth.main_project.PayKids_Server.domain.quiz.dto.QuizDTO;
import depth.main_project.PayKids_Server.domain.quiz.service.QuizService;
import depth.main_project.PayKids_Server.global.dto.ApiResult;
import depth.main_project.PayKids_Server.global.token.TokenService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/quiz")
@RequiredArgsConstructor
public class QuizController {
    private final QuizService quizService;
    private final TokenService tokenService;

    @GetMapping("/token")
    public ApiResult<String> getToken(@RequestParam Long id){

        return ApiResult.ok(tokenService.generateToken(id));
    }

    @Operation(summary = "스테이지 이름 조회", description = "조회할 스테이지 번호를 입력하면 주제가 조회됩니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "성공적으로 확인됨"),
            @ApiResponse(responseCode = "400", description = "잘못된 입력 값"),
            @ApiResponse(responseCode = "500", description = "서버 오류 발생")
    })
    @GetMapping("/stage-name")
    public ApiResult<String> getStageName(@RequestParam int stage){

        return ApiResult.ok(quizService.getStageName(stage));
    }

    @Operation(summary = "진행해야하는 스테이지 조회", description = "진행해야하는 스테이지 번호가 반환됩니다. 이전 번호는 클리어를 의미합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "성공적으로 확인됨"),
            @ApiResponse(responseCode = "400", description = "잘못된 입력 값"),
            @ApiResponse(responseCode = "500", description = "서버 오류 발생")
    })
    @GetMapping("/stage-to-go")
    public ApiResult<Integer> getStageNumber(@RequestParam String token){
        return ApiResult.ok(quizService.getUserStageStatus(token));
    }

    @Operation(summary = "스테이지 별 퀴즈 조회", description = "스테이지와 퀴즈 번호를 입력하면 퀴즈를 반환합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "성공적으로 확인됨"),
            @ApiResponse(responseCode = "400", description = "잘못된 입력 값"),
            @ApiResponse(responseCode = "500", description = "서버 오류 발생")
    })
    @GetMapping("")
    public ApiResult<QuizDTO> getQuiz(@RequestParam int stage, @RequestParam int number){
        return ApiResult.ok(quizService.getQuiz(stage, number));
    }

    @Operation(summary = "스테이지 별 오답 번호 리스트 조회", description = "스테이지별 오답 번호 리스트를 반환합니다. 빈 배열이 반환되면 틀린게 없는것을 의미하고, 0이 반환되면 도전 이력이 없음을 의미합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "성공적으로 확인됨"),
            @ApiResponse(responseCode = "400", description = "잘못된 입력 값"),
            @ApiResponse(responseCode = "500", description = "서버 오류 발생")
    })
    @GetMapping("/incorrect-list")
    public ApiResult<List<Integer>> getIncorrectQuizList(@RequestParam String token, @RequestParam int stage){
        if (quizService.isNotYet(stage, token)){
            List<Integer> list = new ArrayList<>();
            list.add(0);
            return ApiResult.ok(list);
        }

        return ApiResult.ok(quizService.getIncorrectQuizList(stage, token));
    }

    @Operation(summary = "퀴즈 정답 여부 조회", description = "스테이지와 퀴즈 번호, 정답, 토큰을 입력하면 퀴즈 정답 여부를 반환합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "성공적으로 확인됨"),
            @ApiResponse(responseCode = "400", description = "잘못된 입력 값"),
            @ApiResponse(responseCode = "500", description = "서버 오류 발생")
    })
    @GetMapping("/check-answer")
    public ApiResult<Boolean> checkQuizAnswer(@RequestParam String token, @RequestParam int stage, @RequestParam int number, @RequestParam String answer){
        return ApiResult.ok(quizService.isQuizAnswerTrue(stage, number, answer, token));
    }

    @Operation(summary = "스테이지 클리어 여부 조회", description = "스테이지와 토큰을 입력하면 스테이지 클리어 여부를 반환합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "성공적으로 확인됨"),
            @ApiResponse(responseCode = "400", description = "잘못된 입력 값"),
            @ApiResponse(responseCode = "500", description = "서버 오류 발생")
    })
    @GetMapping("/check-stage")
    public ApiResult<QuizClearedDTO> checkStageCleared(@RequestParam int stage, @RequestParam String token){
        //복습인 경우
        if (quizService.isRestudy(stage, token)){
            QuizClearedDTO quizClearedDTO = QuizClearedDTO.builder()
                    .message("복습")
                    .isCleared(true)
                    .build();

            return ApiResult.ok(quizClearedDTO);
        }

        //오답인 경우
        if (quizService.isIncorrect(stage, token)){
            if (quizService.isPassedStage(stage, token)){
                QuizClearedDTO quizClearedDTO = QuizClearedDTO.builder()
                        .message("오답 노트")
                        .isCleared(true)
                        .build();

                return ApiResult.ok(quizClearedDTO);
            }

            QuizClearedDTO quizClearedDTO = QuizClearedDTO.builder()
                    .message("오답 노트")
                    .isCleared(false)
                    .build();

            return ApiResult.ok(quizClearedDTO);
        }

        //올클리어 경우
        if (quizService.isAllCleared(stage, token)){
            QuizClearedDTO quizClearedDTO = QuizClearedDTO.builder()
                    .message("All Clear")
                    .isCleared(true)
                    .build();

            return ApiResult.ok(quizClearedDTO);
        }

        //최초 클리어 여부
        if (quizService.isPassedStage(stage, token)){
            QuizClearedDTO quizClearedDTO = QuizClearedDTO.builder()
                    .message("First")
                    .isCleared(true)
                    .build();

            return ApiResult.ok(quizClearedDTO);
        }

        QuizClearedDTO quizClearedDTO = QuizClearedDTO.builder()
                .message("First")
                .isCleared(false)
                .build();

        return ApiResult.ok(quizClearedDTO);
    }
}