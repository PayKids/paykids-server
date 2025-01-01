package depth.main_project.PayKids_Server.domain.auth;

import depth.main_project.PayKids_Server.domain.user.dto.UserDTO;
import depth.main_project.PayKids_Server.domain.user.entity.User;
import depth.main_project.PayKids_Server.domain.user.repository.UserRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@AllArgsConstructor
@Transactional
@Slf4j
public class AuthService {
    private final KakaoTokenValidator kakaoTokenValidator;
    private final UserRepository userRepository;
    private final TokenService tokenService;

    public LoginResponse processLogin(String idToken) {
        // 1. ID Token 검증
        UserDTO userDTO = kakaoTokenValidator.validateAndExtract(idToken);

        // 2. 사용자 정보 조회 및 저장
        User user = userRepository.findByEmail(userDTO.getEmail())
                .orElseGet(() -> registerNewUser(userDTO));

        // 3. Access Token 및 Refresh Token 생성
        String accessToken = tokenService.generateAccessToken(user.getId());
        String refreshToken = tokenService.generateRefreshToken(user.getId());

        return new LoginResponse(accessToken, refreshToken);
    }

    private User registerNewUser(UserDTO userDTO) {
        User newUser = User.builder()
                .email(userDTO.getEmail())
                .nickname(userDTO.getNickname())
                .profileImageURL(userDTO.getProfileImageURL())
                .build();
        return userRepository.save(newUser);
    }

}
