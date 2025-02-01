package depth.main_project.PayKids_Server.domain.user.entity;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.util.UUID;

@NoArgsConstructor
@Entity
@Getter
@Setter
@Table(name = "User")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String kakaoId;
    private String username;
    private int gptNumber = 0;

    @Column(nullable = false, unique = true, updatable = false)
    private String uuid = UUID.randomUUID().toString(); // UUID 자동 생성

    @Column(nullable = true)
    private String nickname;
    private String email;

    //서버에 저장되어 있는 유저 프로필 이미지 주소
    private String profileImageURL;

    //진행해야 하는 스테이지 상태를 의미한다.
    private Integer stageStatus = 1;

    //연속 학습 일수
    private Integer streak = 0;

    private LocalDate lastStreak;

    @Builder
    public User(String kakaoId,  String username, String nickname, String email, String profileImageURL) {
        this.kakaoId = kakaoId;
        this.username = username;
        this.nickname = nickname;
        this.email = email;
        this.profileImageURL = profileImageURL;
        this.stageStatus = 1;
    }
}
