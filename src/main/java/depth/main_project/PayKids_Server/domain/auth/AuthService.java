package depth.main_project.PayKids_Server.domain.auth;

import depth.main_project.PayKids_Server.domain.allowance.entity.AllowanceType;
import depth.main_project.PayKids_Server.domain.allowance.entity.Category;
import depth.main_project.PayKids_Server.domain.allowance.repository.CategoryRepository;
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
    private final CategoryRepository categoryRepository;
    private final TokenService tokenService;

    public LoginResponse signupOrLogin(String idToken) {
        // 1. ID 토큰 검증 및 사용자 정보 추출
        UserDTO userDTO = kakaoTokenValidator.validateAndExtract(idToken);

        // 2. 사용자 존재 여부 확인
        User user = userRepository.findByEmail(userDTO.getEmail())
                .orElseGet(() -> registerNewUser(userDTO)); // 존재하지 않으면 회원가입

        // 3. Access Token 및 Refresh Token 생성
        String accessToken = tokenService.generateAccessToken(user.getUuid());
        String refreshToken = tokenService.generateRefreshToken(user.getUuid());

        // 4. Access Token, Refresh Token 반환
        return new LoginResponse(accessToken, refreshToken);
    }

    private User registerNewUser(UserDTO userDTO) {
        User newUser = User.builder()
                .email(userDTO.getEmail())
                .username(userDTO.getNickname())
                .profileImageURL(userDTO.getProfileImageURL())
                .build();

        Category incomeCategory = Category.builder()
                .user(newUser)
                .title("기타")
                .allowanceType(AllowanceType.INCOME)
                .build();

        Category expenseCategory = Category.builder()
                .user(newUser)
                .title("기타")
                .allowanceType(AllowanceType.EXPENSE)
                .build();

        categoryRepository.save(incomeCategory);
        categoryRepository.save(expenseCategory);

        return userRepository.save(newUser);
    }
}
