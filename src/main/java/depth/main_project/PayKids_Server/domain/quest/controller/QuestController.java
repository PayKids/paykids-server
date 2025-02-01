package depth.main_project.PayKids_Server.domain.quest.controller;

import depth.main_project.PayKids_Server.domain.quest.dto.UserQuestDTO;
import depth.main_project.PayKids_Server.domain.quest.service.QuestService;
import depth.main_project.PayKids_Server.global.dto.ApiResult;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/Quest")
@RequiredArgsConstructor
public class QuestController {
    private final QuestService questService;

    @Operation(summary = "퀘스트 조회", description = "토큰으로 유저를 판별 한 후 업적 리스트를 반환합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "성공적으로 확인됨"),
            @ApiResponse(responseCode = "400", description = "잘못된 입력 값"),
            @ApiResponse(responseCode = "500", description = "서버 오류 발생")
    })
    @GetMapping("/list")
    public ApiResult<List<UserQuestDTO>> getAllAchievement(@RequestHeader("Authorization") String token) {
        return ApiResult.ok(questService.getAllQuest(token));
    }
}
