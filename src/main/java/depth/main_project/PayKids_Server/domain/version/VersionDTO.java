package depth.main_project.PayKids_Server.domain.version;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
@Getter
@Setter
@Builder
public class VersionDTO {
    private String versionCode;
    private String versionName;
}
