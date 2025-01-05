package depth.main_project.PayKids_Server.domain.chatting;

import depth.main_project.PayKids_Server.domain.auth.TokenService;
import depth.main_project.PayKids_Server.domain.user.entity.User;
import depth.main_project.PayKids_Server.domain.user.repository.UserRepository;
import depth.main_project.PayKids_Server.global.exception.ErrorCode;
import depth.main_project.PayKids_Server.global.exception.MapperException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Transactional
@RequiredArgsConstructor
public class GPTService {
    private final UserRepository userRepository;
    private final TokenService tokenService;

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

    @Scheduled(cron = "0 0 8 * * ?", zone = "Asia/Seoul")
    public void resetGPTNumber() {
        try {
            List<User> users = userRepository.findAll();

            for (User user : users) {
                user.setGptNumber(0);
                userRepository.save(user);
            }
        } catch (Exception e) {
            throw new MapperException(ErrorCode.GPT_RESET_ERROR);
        }
    }
}
