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
        String modifiedPrompt = "다음 질문에 대해 초등학교 저학년이 이해하기 쉽게 답변해주세요:\n" + prompt;
        this.messages.add(new MessageDTO("user", modifiedPrompt));
    }
}
