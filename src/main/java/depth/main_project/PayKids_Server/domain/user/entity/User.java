package depth.main_project.PayKids_Server.domain.user.entity;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Entity
@Getter
@Setter
@Table(name = "User")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String username;

    @Column(nullable = true)
    private String nickname;
    private String email;

    //서버에 저장되어 있는 유저 프로필 이미지 주소
    private String profileImageURL;

    //진행해야 하는 스테이지 상태를 의미한다.
    private Integer stageStatus = 1;

    @Builder
    public User(String username, String nickname, String email, String profileImageURL) {
        this.username = username;
        this.nickname = nickname;
        this.email = email;
        this.profileImageURL = profileImageURL;
        this.stageStatus = 0;
    }
}
