package depth.main_project.PayKids_Server.domain.user;

import depth.main_project.PayKids_Server.domain.achievement.entity.UserAchievement;
import depth.main_project.PayKids_Server.domain.achievement.repository.UserAchievementRepository;
import depth.main_project.PayKids_Server.domain.allowance.entity.AllowanceChart;
import depth.main_project.PayKids_Server.domain.allowance.entity.Category;
import depth.main_project.PayKids_Server.domain.allowance.repository.AllowanceChartRepository;
import depth.main_project.PayKids_Server.domain.allowance.repository.CategoryRepository;
import depth.main_project.PayKids_Server.domain.quest.entity.UserQuest;
import depth.main_project.PayKids_Server.domain.quest.repository.UserQuestRepository;
import depth.main_project.PayKids_Server.domain.quiz.entity.Submission;
import depth.main_project.PayKids_Server.domain.quiz.entity.UserStage;
import depth.main_project.PayKids_Server.domain.quiz.repository.SubmissionRepository;
import depth.main_project.PayKids_Server.domain.quiz.repository.UserStageRepository;
import depth.main_project.PayKids_Server.domain.user.dto.UserDTO;
import depth.main_project.PayKids_Server.domain.user.entity.User;
import depth.main_project.PayKids_Server.domain.user.repository.UserRepository;
import depth.main_project.PayKids_Server.global.config.S3Service;
import depth.main_project.PayKids_Server.global.exception.ErrorCode;
import depth.main_project.PayKids_Server.global.exception.MapperException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final AllowanceChartRepository allowanceChartRepository;
    private final SubmissionRepository submissionRepository;
    private final CategoryRepository categoryRepository;
    private final UserAchievementRepository achievementRepository;
    private final UserQuestRepository questRepository;
    private final UserStageRepository stageRepository;
    private final S3Service s3Service;

    public UserDTO getUserInfo(String uuid) {
        User user = userRepository.findByUuid(uuid)
                .orElseThrow(() -> new MapperException(ErrorCode.USER_NOT_FOUND));

        return new UserDTO(user.getId(), user.getKakaoId(), user.getUsername(), user.getUuid(), user.getNickname(), user.getEmail(), user.getProfileImageURL(), user.getStageStatus());
    }

    public String saveNickname(String uuid, String nickname) {
        if (nickname.codePointCount(0, nickname.length()) > 8) {
            throw new MapperException(ErrorCode.NICKNAME_TOO_LONG); // 닉네임 길이 초과
        }

        if (nickname == null || nickname.trim().isEmpty()) {
            throw new MapperException(ErrorCode.INVALID_NICKNAME); // 닉네임이 공백인 경우 예외 처리
        }

        User user = userRepository.findByUuid(uuid)
                .orElseThrow(() -> new MapperException(ErrorCode.USER_NOT_FOUND));

        if (userRepository.existsByNickname(nickname)) {
            throw new MapperException(ErrorCode.SAME_NICKNAME); // 다른 유저와의 닉네임 중복 확인
        }

        if (user.getNickname() != null) {
            throw new MapperException(ErrorCode.NICKNAME_ALREADY_EXISTS);
        }

        user.setNickname(nickname);
        userRepository.save(user);

        return "Nickname saved successfully";
    }

    public String changeNickname(String uuid, String newNickname) {
        if (newNickname.codePointCount(0, newNickname.length()) > 8) {
            throw new MapperException(ErrorCode.NICKNAME_TOO_LONG);
        }

        if (newNickname == null || newNickname.trim().isEmpty()) {
            throw new MapperException(ErrorCode.INVALID_NICKNAME);
        }

        User user = userRepository.findByUuid(uuid)
                .orElseThrow(() -> new MapperException(ErrorCode.USER_NOT_FOUND));

        if (userRepository.existsByNickname(newNickname)) {
            throw new MapperException(ErrorCode.SAME_NICKNAME);
        }

        user.setNickname(newNickname);
        userRepository.save(user);

        return "Nickname changed successfully";
    }

    public String changeProfileImage(String uuid, MultipartFile file) {
        User user = userRepository.findByUuid(uuid)
                .orElseThrow(() -> new MapperException(ErrorCode.USER_NOT_FOUND));

        // 이전 프로필 이미지 삭제
        String previousImageUrl = user.getProfileImageURL();
        deletePreviousProfileImage(previousImageUrl);

        // S3에 파일 업로드 및 URL 반환
        String email = user.getEmail();
        String newImageUrl = s3Service.uploadToProfileImageFolder(email, file);

        // 프로필 이미지 URL을 DB에 업데이트
        user.setProfileImageURL(newImageUrl);
        userRepository.save(user);

        return "Profile Image changed successfully";
    }

    private void deletePreviousProfileImage(String previousImageUrl) {
        if (previousImageUrl == null) {
            throw new MapperException(ErrorCode.PREVIOUS_IMAGE_NOT_FOUND);
        }

        if (previousImageUrl.contains(".s3.") && previousImageUrl.contains(".amazonaws.com")) {
            s3Service.deleteFileFromS3(previousImageUrl); // S3에서 삭제
        }
    }

    public String deleteUser(String uuid) {
        User user = userRepository.findByUuid(uuid)
                .orElseThrow(() -> new MapperException(ErrorCode.USER_NOT_FOUND));

        List<Category> categoryList = categoryRepository.findAllByUser(user);
        List<AllowanceChart> allowanceChartList = allowanceChartRepository.findAllByUser(user);
        List<Submission> submissionList = submissionRepository.findAllByUser(user);
        List<UserAchievement> userAchievementList = achievementRepository.findAllByUser(user);
        List<UserQuest> userQuestList = questRepository.findAllByUser(user);
        List<UserStage> userStageList = stageRepository.findAllByUser(user);

        allowanceChartRepository.deleteAll(allowanceChartList);
        submissionRepository.deleteAll(submissionList);
        achievementRepository.deleteAll(userAchievementList);
        questRepository.deleteAll(userQuestList);
        categoryRepository.deleteAll(categoryList);
        stageRepository.deleteAll(userStageList);

        userRepository.delete(user);

        return "User deleted successfully";
    }
}
