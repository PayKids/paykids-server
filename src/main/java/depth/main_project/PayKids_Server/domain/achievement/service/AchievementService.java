package depth.main_project.PayKids_Server.domain.achievement.service;

import depth.main_project.PayKids_Server.domain.achievement.dto.UserAchievementDTO;
import depth.main_project.PayKids_Server.domain.achievement.entity.Achievement;
import depth.main_project.PayKids_Server.domain.achievement.entity.UserAchievement;
import depth.main_project.PayKids_Server.domain.achievement.repository.AchievementRepository;
import depth.main_project.PayKids_Server.domain.achievement.repository.UserAchievementRepository;
import depth.main_project.PayKids_Server.domain.auth.TokenService;
import depth.main_project.PayKids_Server.domain.user.entity.User;
import depth.main_project.PayKids_Server.domain.user.repository.UserRepository;
import depth.main_project.PayKids_Server.global.exception.ErrorCode;
import depth.main_project.PayKids_Server.global.exception.MapperException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Service
public class AchievementService {
    private final AchievementRepository achievementRepository;
    private final UserAchievementRepository userAchievementRepository;
    private final UserRepository userRepository;
    private final TokenService tokenService;

    public List<UserAchievementDTO> getUserAchievements(String token) {
        List<UserAchievementDTO> userAchievements = new ArrayList<>();

        if (tokenService.expiredToken(token) == false){
            throw new MapperException(ErrorCode.TOKEN_EXPIRED_ERROR);
        }

        String userId = tokenService.getUserUuidFromToken(token);

        if (userId == null) {
            throw new MapperException(ErrorCode.TOKEN_ERROR);
        }

        User user = userRepository.findByUuid(userId)
                .orElseThrow(() -> new MapperException(ErrorCode.USER_NOT_FOUND));

        List<Achievement> achievements = achievementRepository.findAll();

        for (Achievement achievement : achievements) {
            Optional<UserAchievement> userAchievement = userAchievementRepository.findByAchievementAndUser(achievement, Optional.ofNullable(user));

            if (userAchievement.isPresent()) {
                UserAchievementDTO userAchievementDTO = UserAchievementDTO.builder()
                        .isCompleted(userAchievement.get().getIsCompleted())
                        .name(achievement.getName())
                        .description(achievement.getDescription())
                        .imageURL(achievement.getImageURL())
                        .build();

                userAchievements.add(userAchievementDTO);
            }

            UserAchievementDTO userAchievementDTO = UserAchievementDTO.builder()
                    .isCompleted(false)
                    .name(achievement.getName())
                    .description(achievement.getDescription())
                    .imageURL(achievement.getImageURL())
                    .build();

            userAchievements.add(userAchievementDTO);
        }

        return userAchievements;
    }
}
