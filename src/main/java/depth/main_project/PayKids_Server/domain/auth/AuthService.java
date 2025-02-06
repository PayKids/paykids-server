package depth.main_project.PayKids_Server.domain.auth;

import depth.main_project.PayKids_Server.domain.allowance.entity.AllowanceType;
import depth.main_project.PayKids_Server.domain.allowance.entity.Category;
import depth.main_project.PayKids_Server.domain.allowance.repository.CategoryRepository;
import depth.main_project.PayKids_Server.domain.quest.entity.Quest;
import depth.main_project.PayKids_Server.domain.quest.entity.UserQuest;
import depth.main_project.PayKids_Server.domain.quest.repository.QuestRepository;
import depth.main_project.PayKids_Server.domain.quest.repository.UserQuestRepository;
import depth.main_project.PayKids_Server.domain.user.dto.UserDTO;
import depth.main_project.PayKids_Server.domain.user.entity.User;
import depth.main_project.PayKids_Server.domain.user.repository.UserRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.Random;

@Service
@AllArgsConstructor
@Transactional
@Slf4j
public class AuthService {
    private final KakaoTokenValidator kakaoTokenValidator;
    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;
    private final UserQuestRepository userQuestRepository;
    private final QuestRepository questRepository;
    private final TokenService tokenService;

    public LoginResponse signupOrLogin(String idToken) {
        // 1. ID 토큰 검증 및 사용자 정보 추출
        UserDTO userDTO = kakaoTokenValidator.validateAndExtract(idToken);

        // 2. 사용자 존재 여부 확인
        User user = userRepository.findByKakaoId(userDTO.getSub())
                .orElseGet(() -> registerNewUser(userDTO)); // 존재하지 않으면 회원가입

        // 3. Access Token 및 Refresh Token 생성
        String accessToken = tokenService.generateAccessToken(user.getUuid());
        String refreshToken = tokenService.generateRefreshToken(user.getUuid());

        boolean isRegistered = user.getNickname() != null;

        return new LoginResponse( accessToken, refreshToken, "Bearer", isRegistered );
    }

    private User registerNewUser(UserDTO userDTO) {
        User newUser = User.builder()
                .kakaoId(userDTO.getSub())
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
        userRepository.save(newUser);

        long min = 3L;
        long max = 7L;

        Random random = new Random();
        Long number = random.nextLong(min, max);

        Optional<Quest> quest1 = questRepository.findById(1L);
        Optional<Quest> quest2 = questRepository.findById(10L);
        Optional<Quest> quest3 = questRepository.findById(number);

        UserQuest userQuest1 = UserQuest.builder()
                .user(newUser)
                .quest(quest1.get())
                .build();

        UserQuest userQuest2 = UserQuest.builder()
                .user(newUser)
                .quest(quest2.get())
                .build();

        UserQuest userQuest3 = UserQuest.builder()
                .user(newUser)
                .quest(quest3.get())
                .build();

        userQuestRepository.save(userQuest1);
        userQuestRepository.save(userQuest2);
        userQuestRepository.save(userQuest3);

        return newUser;
    }
}
