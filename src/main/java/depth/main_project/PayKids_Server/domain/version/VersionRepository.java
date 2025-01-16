package depth.main_project.PayKids_Server.domain.version;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface VersionRepository extends JpaRepository<Version, Long> {
    @Query("select v from Version v")
    Version findOne();
}
