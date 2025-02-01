package depth.main_project.PayKids_Server.domain.chatting;

import depth.main_project.PayKids_Server.domain.achievement.dto.UserAchievementUpdateEvent;
import depth.main_project.PayKids_Server.domain.allowance.repository.CategoryRepository;
import depth.main_project.PayKids_Server.domain.auth.TokenService;
import depth.main_project.PayKids_Server.domain.quest.entity.Quest;
import depth.main_project.PayKids_Server.domain.quest.entity.UserQuest;
import depth.main_project.PayKids_Server.domain.quest.repository.QuestRepository;
import depth.main_project.PayKids_Server.domain.quest.repository.UserQuestRepository;
import depth.main_project.PayKids_Server.domain.quest.service.QuestService;
import depth.main_project.PayKids_Server.domain.quiz.entity.Submission;
import depth.main_project.PayKids_Server.domain.quiz.repository.SubmissionRepository;
import depth.main_project.PayKids_Server.domain.user.entity.User;
import depth.main_project.PayKids_Server.domain.user.repository.UserRepository;
import depth.main_project.PayKids_Server.global.exception.ErrorCode;
import depth.main_project.PayKids_Server.global.exception.MapperException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
public class GPTService {
    private final UserRepository userRepository;
    private final UserQuestRepository userQuestRepository;
    private final CategoryRepository categoryRepository;
    private final SubmissionRepository submissionRepository;
    private final QuestRepository questRepository;
    private final TokenService tokenService;
    private final QuestService questService;
    private final ApplicationEventPublisher eventPublisher;

    @Value("ft:gpt-4o-mini-2024-07-18:personal::AmIGcMts")
    private String model;

    @Value("${gpt.api.url}")
    private String apiURL;

    @Autowired
    private RestTemplate template;

    public Map<String, String> getChatting(String prompt, String token){
        String uuId = tokenService.getUserUuidFromToken(token);

        if (uuId == null) {
            throw new MapperException(ErrorCode.TOKEN_ERROR);
        }

        User user = userRepository.findByUuid(uuId)
                .orElseThrow(() -> new MapperException(ErrorCode.USER_NOT_FOUND));

        if (user.getGptNumber() >= 10){
            throw new MapperException(ErrorCode.GPT_ERROR);
        } else {
            user.setGptNumber(user.getGptNumber() + 1);
            userRepository.save(user);
        }

        GPTRequestDTO requestDTO = new GPTRequestDTO(model, prompt);
        GPTResponseDTO responseDTO = template.postForObject(apiURL, requestDTO, GPTResponseDTO.class);
        String content = responseDTO.getChoices().get(0).getMessage().getContent();

        Map<String, String> jsonResponse = new HashMap<>();
        jsonResponse.put("response", content);

        questService.questManage(user, 4L);

        return jsonResponse;
    }

    public int getGPTNumber(String token){
        String uuId = tokenService.getUserUuidFromToken(token);

        if (uuId == null) {
            throw new MapperException(ErrorCode.TOKEN_ERROR);
        }

        User user = userRepository.findByUuid(uuId)
                .orElseThrow(() -> new MapperException(ErrorCode.USER_NOT_FOUND));

        return (10 - user.getGptNumber());
    }

    @Scheduled(cron = "0 0 0 * * ?", zone = "Asia/Seoul")
    public void resetGPTNumber() {
        try {
            List<User> users = userRepository.findAll();

            LocalDate previousDate = LocalDate.now().minusDays(1);
            LocalDate threeDaysDate = LocalDate.now().minusDays(4);

            for (User user : users) {
                user.setGptNumber(0);
                userRepository.save(user);

                if (user.getLastStreak() == null || !user.getLastStreak().isEqual(previousDate)){
                    user.setStreak(0);
                    userRepository.save(user);
                }

                if (user.getLastStreak() != null && user.getLastStreak().isEqual(threeDaysDate)){
                    eventPublisher.publishEvent(new UserAchievementUpdateEvent(user, 9L));
                }
            }

            userQuestRepository.deleteAll();

            for (User user : users) {
                if (user.getStreak() == null || user.getStreak() == 0){
                    Optional<Quest> quest = questRepository.findById(1L);

                    UserQuest userQuest = UserQuest.builder()
                            .user(user)
                            .quest(quest.get())
                            .build();

                    userQuestRepository.save(userQuest);
                } else {
                    Optional<Quest> quest = questRepository.findById(2L);

                    UserQuest userQuest = UserQuest.builder()
                            .user(user)
                            .quest(quest.get())
                            .build();

                    userQuestRepository.save(userQuest);
                }

                if (categoryRepository.findAllByUser(user).size() > 2){
                    Optional<Quest> quest = questRepository.findById(10L);

                    UserQuest userQuest = UserQuest.builder()
                            .user(user)
                            .quest(quest.get())
                            .build();

                    userQuestRepository.save(userQuest);
                }

                List<Submission> submissionList = submissionRepository.findAllByUserAndIsAnswerTrueTrue(user);

                if (!submissionList.isEmpty()){
                    Optional<Quest> quest = questRepository.findById(9L);

                    UserQuest userQuest = UserQuest.builder()
                            .user(user)
                            .quest(quest.get())
                            .build();

                    userQuestRepository.save(userQuest);
                }

                List<UserQuest> userQuests = userQuestRepository.findAllByUser(user);
                int questCount = 0;

                for (UserQuest userQuest : userQuests){
                    questCount++;
                }

                long min = 3L;
                long max = 7L;

                Random random = new Random();
                List<Long> randomNumbers = random.longs(min, max + 1)
                        .distinct()   // 중복 제거
                        .limit(2)     // 2개만 추출
                        .boxed()      // long → Long 변환
                        .collect(Collectors.toList());

                for (int i = 0; i < 3 - questCount; i++){
                    Optional<Quest> quest = questRepository.findById(randomNumbers.get(i));

                    UserQuest userQuest = UserQuest.builder()
                            .user(user)
                            .quest(quest.get())
                            .build();

                    userQuestRepository.save(userQuest);
                }
            }
        } catch (Exception e) {
            throw new MapperException(ErrorCode.GPT_RESET_ERROR);
        }
    }
}
