package depth.main_project.PayKids_Server.domain.version;

import jakarta.persistence.*;
import lombok.*;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Setter
@Entity
@Table(name = "Version")
public class Version {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;
    private String versionCode;
    private String versionName;

    @Builder
    public Version(String versionCode, String versionName) {
        this.versionCode = versionCode;
        this.versionName = versionName;
    }
}
