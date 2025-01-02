package depth.main_project.PayKids_Server.domain.user;

import depth.main_project.PayKids_Server.domain.auth.TokenService;
import depth.main_project.PayKids_Server.domain.user.dto.UserDTO;
import depth.main_project.PayKids_Server.global.dto.ApiResult;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;
    private final TokenService tokenService;


    @Operation(summary = "유저확인", description = "Access Token을 인증하고, 사용자 정보를 반환하는 유저 확인")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "성공적으로 확인됨"),
            @ApiResponse(responseCode = "400", description = "잘못된 입력 값"),
            @ApiResponse(responseCode = "500", description = "서버 오류 발생")
    })
    @GetMapping("/info")
    public ApiResult<UserDTO> getUserInfo(@RequestHeader("Authorization") String accessToken) {
        String userUUID = tokenService.getUserUuidFromToken(accessToken);
        UserDTO userDTO = userService.getUserInfo(userUUID);
        return ApiResult.ok(userDTO);
    }

    @Operation(summary = "닉네임 저장", description = "닉네임 저장")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "성공적으로 확인됨"),
            @ApiResponse(responseCode = "400", description = "잘못된 입력 값"),
            @ApiResponse(responseCode = "500", description = "서버 오류 발생")
    })
    @PostMapping("/nickname/save")
    public ApiResult<String> saveNickname(@RequestHeader("Authorization") String accessToken,
                                          @RequestParam String nickname) {
        String userUUID = tokenService.getUserUuidFromToken(accessToken);
        String result = userService.saveNickname(userUUID, nickname);
        return ApiResult.ok(result);
    }

    @Operation(summary = "닉네임 변경", description = "닉네임 변경")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "성공적으로 확인됨"),
            @ApiResponse(responseCode = "400", description = "잘못된 입력 값"),
            @ApiResponse(responseCode = "500", description = "서버 오류 발생")
    })
    @PostMapping("/nickname/change")
    public ApiResult<String> changeNickname(@RequestHeader("Authorization") String accessToken,
                                            @RequestParam String newNickname) {
        String userUUID = tokenService.getUserUuidFromToken(accessToken);
        String result = userService.changeNickname(userUUID, newNickname);
        return ApiResult.ok(result);
    }

    @Operation(summary = "이메일변경", description = "이메일을 변경")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "성공적으로 확인됨"),
            @ApiResponse(responseCode = "400", description = "잘못된 입력 값"),
            @ApiResponse(responseCode = "500", description = "서버 오류 발생")
    })
    @PostMapping("/email/change")
    public ApiResult<String> changeEmail(@RequestHeader("Authorization") String accessToken,
                                         @RequestParam String newEmail) {
        String userUUID = tokenService.getUserUuidFromToken(accessToken);
        String result = userService.changeEmail(userUUID, newEmail);
        return ApiResult.ok(result);
    }
}
