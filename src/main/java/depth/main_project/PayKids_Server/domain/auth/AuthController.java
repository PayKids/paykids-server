package depth.main_project.PayKids_Server.domain.auth;

import depth.main_project.PayKids_Server.global.dto.ApiResult;
import depth.main_project.PayKids_Server.global.exception.ErrorCode;
import depth.main_project.PayKids_Server.global.exception.MapperException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {
    private final AuthService authService;
    private final TokenService tokenService;

    @Operation(summary = "토큰 발급", description = "이전 토큰을 입력하면 새로운 토큰을 발급합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "성공적으로 확인됨"),
            @ApiResponse(responseCode = "400", description = "잘못된 입력 값"),
            @ApiResponse(responseCode = "500", description = "서버 오류 발생")
    })
    @PostMapping("/login")
    public ApiResult<LoginResponse> login(@RequestParam String idToken) {
        LoginResponse response = authService.processLogin(idToken);
        return ApiResult.ok(response);
    }

    @Operation(summary = "refresh 토큰 발급", description = "기존 AccessToken을 이용해 refreshToken을 재발급하고, AccessToken은 새로 생성")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "성공적으로 확인됨"),
            @ApiResponse(responseCode = "400", description = "잘못된 입력 값"),
            @ApiResponse(responseCode = "500", description = "서버 오류 발생")
    })
    @PostMapping("/refresh")
    public ApiResult<LoginResponse> refresh(@RequestParam String refreshToken) {
        Long userId = tokenService.getUserIdFromToken(refreshToken);

        // Refresh Token 검증 및 사용자 정보 확인
        if (tokenService.expiredToken(refreshToken)) {
            throw new MapperException(ErrorCode.TOKEN_EXPIRED_ERROR);
        }

        String newAccessToken = tokenService.generateAccessToken(userId);
        return ApiResult.ok(new LoginResponse(newAccessToken, refreshToken));
    }
}