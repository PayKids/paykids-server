package depth.main_project.PayKids_Server.domain.version;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@Transactional
@RequiredArgsConstructor
public class VersionService {
    private final VersionRepository versionRepository;

    public VersionDTO getNecessaryVersion() {
        Version version = versionRepository.findOne();

        try {
            return VersionDTO.builder()
                    .versionCode(version.getVersionCode())
                    .versionName(version.getVersionName())
                    .build();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void modifyNecessaryVersion(String versionName, String versionCode) {
        Version version = versionRepository.findOne();

        if (versionCode != null) version.setVersionCode(versionCode);
        if (versionName != null) version.setVersionName(versionName);

        try {
            versionRepository.save(version);
        } catch (Exception e) {
            throw new RuntimeException();
        }
    }
}
