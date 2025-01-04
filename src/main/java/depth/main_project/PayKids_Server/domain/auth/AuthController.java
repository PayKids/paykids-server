package depth.main_project.PayKids_Server.domain.auth;

import depth.main_project.PayKids_Server.global.dto.ApiResult;
import depth.main_project.PayKids_Server.global.exception.ErrorCode;
import depth.main_project.PayKids_Server.global.exception.MapperException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {
    private final AuthService authService;
    private final TokenService tokenService;

    @Operation(summary = "회원가입 및 로그인시 토큰 발급", description = "회원가입 및 로그인시, idToken을 이용해 accessToken과 refreshToken발급")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "성공적으로 확인됨"),
            @ApiResponse(responseCode = "400", description = "잘못된 입력 값"),
            @ApiResponse(responseCode = "500", description = "서버 오류 발생")
    })
    @PostMapping("/login")
    public ApiResult<LoginResponse> signupOrLogin(@RequestParam String idToken) {
        LoginResponse response = authService.signupOrLogin(idToken);
        return ApiResult.ok(response);
    }


    @Operation(summary = "refresh 토큰 발급", description = "기존 refreshToken을 이용해 accessToken을 재발급하고, refreshToken은 새로 생성")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "성공적으로 확인됨"),
            @ApiResponse(responseCode = "400", description = "잘못된 입력 값"),
            @ApiResponse(responseCode = "500", description = "서버 오류 발생")
    })
    @PostMapping("/refresh")
    public ApiResult<LoginResponse> refresh(@RequestParam String refreshToken) {
        String UserUUID = tokenService.getUserUuidFromToken(refreshToken);

        // Refresh Token 검증 및 사용자 정보 확인
        if (!tokenService.expiredToken(refreshToken)) {
            throw new MapperException(ErrorCode.TOKEN_EXPIRED_ERROR);
        }
        String newAccessToken = tokenService.generateAccessToken(UserUUID);
        return ApiResult.ok(new LoginResponse(newAccessToken, refreshToken, "Bearer", true));
    }
}
