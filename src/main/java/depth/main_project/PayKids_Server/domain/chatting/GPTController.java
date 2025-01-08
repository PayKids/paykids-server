package depth.main_project.PayKids_Server.domain.chatting;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/gpt")
public class GPTController {
    private final GPTService gptService;

    @Operation(summary = "채팅", description = "주어진 입력에 대한 대답을 합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "성공적으로 대답함"),
            @ApiResponse(responseCode = "400", description = "잘못된 입력 값"),
            @ApiResponse(responseCode = "500", description = "서버 오류 발생")
    })
    @GetMapping("/chat")
    public ResponseEntity<Map<String, String>> chat(@RequestParam String prompt, @RequestHeader("Authorization") String token) {

        return ResponseEntity.ok(gptService.getChatting(prompt, token));
    }

    @Operation(summary = "남은 채팅 횟수 조회", description = "남은 채팅 횟수를 조회합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "성공적으로 대답함"),
            @ApiResponse(responseCode = "400", description = "잘못된 입력 값"),
            @ApiResponse(responseCode = "500", description = "서버 오류 발생")
    })
    @GetMapping("/number")
    public ResponseEntity<Integer> getGPTNumber(@RequestHeader("Authorization") String token) {
        return ResponseEntity.ok(gptService.getGPTNumber(token));
    }
}
