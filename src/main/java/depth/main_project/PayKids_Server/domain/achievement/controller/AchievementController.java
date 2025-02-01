package depth.main_project.PayKids_Server.domain.achievement.controller;

import depth.main_project.PayKids_Server.domain.achievement.dto.UserAchievementDTO;
import depth.main_project.PayKids_Server.domain.achievement.service.AchievementService;
import depth.main_project.PayKids_Server.global.dto.ApiResult;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/Achievement")
@RequiredArgsConstructor
public class AchievementController {
    private final AchievementService achievementService;

    @Operation(summary = "업적 전부 조회", description = "토큰으로 유저를 판별 한 후 업적 리스트를 반환합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "성공적으로 확인됨"),
            @ApiResponse(responseCode = "400", description = "잘못된 입력 값"),
            @ApiResponse(responseCode = "500", description = "서버 오류 발생")
    })
    @GetMapping("/list")
    public ApiResult<List<UserAchievementDTO>> getAllAchievement(@RequestHeader("Authorization") String token) {
        return ApiResult.ok(achievementService.getUserAchievements(token));
    }
}
