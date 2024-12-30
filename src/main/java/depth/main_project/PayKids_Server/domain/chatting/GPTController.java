package depth.main_project.PayKids_Server.domain.chatting;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/gpt")
public class GPTController {

    @Value("${gpt.model}")
    private String model;

    @Value("${gpt.api.url}")
    private String apiURL;

    @Autowired
    private RestTemplate template;

    @Operation(summary = "채팅", description = "주어진 입력에 대한 대답을 합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "성공적으로 대답함"),
            @ApiResponse(responseCode = "400", description = "잘못된 입력 값"),
            @ApiResponse(responseCode = "500", description = "서버 오류 발생")
    })
    @GetMapping("/chat")
    public ResponseEntity<Map<String, String>> chat(@RequestParam(name = "prompt")String prompt) {
        GPTRequestDTO requestDTO = new GPTRequestDTO(model, prompt);
        GPTResponseDTO responseDTO = template.postForObject(apiURL, requestDTO, GPTResponseDTO.class);
        String content = responseDTO.getChoices().get(0).getMessage().getContent();

        Map<String, String> jsonResponse = new HashMap<>();
        jsonResponse.put("response", content);

        return ResponseEntity.ok(jsonResponse);
    }
}
