package depth.main_project.PayKids_Server.domain.user.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@AllArgsConstructor
@Getter
@Builder
public class UserDTO {
    private Long id;
    private String sub;
    private String username;
    private String uuid;
    private String nickname;
    private String email;
    private String profileImageURL;
    private Integer stageStatus;

    public UserDTO(String sub, String nickname, String email, String profileImageURL) {
        this.sub = sub;
        this.nickname = nickname;
        this.email = email;
        this.profileImageURL = profileImageURL;
    }
}

