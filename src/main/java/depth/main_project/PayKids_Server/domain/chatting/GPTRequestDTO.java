package depth.main_project.PayKids_Server.domain.chatting;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class GPTRequestDTO {
    private String model;
    private List<MessageDTO> messages;

    public GPTRequestDTO(String model, String prompt) {
        this.model = model;
        this.messages =  new ArrayList<>();
        String modifiedPrompt = "너는 금융 전문가야. 모든 질문에 답변하기 전에 아래 규칙을 따라야 해.\n 1. 먼저, 질문이 금융과 관련된 것인지 판단해.\n 2. 금융과 관련이 있으면 답변을 생성해.\n 3. 금융과 관련이 없으면 무조건 아래 문장을 출력해:\n이 질문은 금융과 관련이 없어요!\n 반드시 위 규칙을 따라야 해. 규칙을 어기면 안 돼.\n 다음 질문에 대해 초등학교 저학년이 이해하기 쉽게 답변해주세요:" + prompt;
        this.messages.add(new MessageDTO("user", modifiedPrompt));
    }
}
