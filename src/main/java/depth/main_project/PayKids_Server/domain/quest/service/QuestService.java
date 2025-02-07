package depth.main_project.PayKids_Server.domain.quest.service;

import depth.main_project.PayKids_Server.domain.achievement.dto.UserAchievementUpdateEvent;
import depth.main_project.PayKids_Server.domain.auth.TokenService;
import depth.main_project.PayKids_Server.domain.quest.dto.UserQuestDTO;
import depth.main_project.PayKids_Server.domain.quest.dto.UserQuestUpdateEvent;
import depth.main_project.PayKids_Server.domain.quest.entity.Quest;
import depth.main_project.PayKids_Server.domain.quest.entity.UserQuest;
import depth.main_project.PayKids_Server.domain.quest.repository.QuestRepository;
import depth.main_project.PayKids_Server.domain.quest.repository.UserQuestRepository;
import depth.main_project.PayKids_Server.domain.user.entity.User;
import depth.main_project.PayKids_Server.domain.user.repository.UserRepository;
import depth.main_project.PayKids_Server.global.exception.ErrorCode;
import depth.main_project.PayKids_Server.global.exception.MapperException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Service
public class QuestService {
    private final UserQuestRepository userQuestRepository;
    private final QuestRepository questRepository;
    private final UserRepository userRepository;
    private final TokenService tokenService;

    @Autowired
    private ApplicationEventPublisher eventPublisher;

    public List<UserQuestDTO> getAllQuest(String token) {
        if (tokenService.expiredToken(token) == false){
            throw new MapperException(ErrorCode.TOKEN_EXPIRED_ERROR);
        }

        String userId = tokenService.getUserUuidFromToken(token);

        if (userId == null) {
            throw new MapperException(ErrorCode.TOKEN_ERROR);
        }

        User user = userRepository.findByUuid(userId)
                .orElseThrow(() -> new MapperException(ErrorCode.USER_NOT_FOUND));

        List<UserQuest> userQuestList = userQuestRepository.findAllByUser(user);
        List<UserQuestDTO> userQuestDTOList = new ArrayList<>();

        for (UserQuest userQuest : userQuestList) {
            UserQuestDTO userQuestDTO = UserQuestDTO.builder()
                    .count(userQuest.getCount())
                    .isComplete(userQuest.getIsComplete())
                    .maxCount(userQuestRepository.findQuestCountByQuestId(userQuest.getId()))
                    .name(userQuestRepository.findQuestNameByQuestId(userQuest.getId()))
                    .build();

            userQuestDTOList.add(userQuestDTO);
        }

        return userQuestDTOList;
    }

    public void questManage(User user, Long questId) {
        Optional<Quest> quest = questRepository.findById(questId);
        Optional<UserQuest> userQuest = userQuestRepository.findByUserAndQuest(user, quest.get());

        if(userQuest.isPresent()) {
            eventPublisher.publishEvent(new UserQuestUpdateEvent(user, quest.get()));
        }
    }
}
