package depth.main_project.PayKids_Server.domain.quest.repository;

import depth.main_project.PayKids_Server.domain.quest.entity.Quest;
import org.springframework.data.jpa.repository.JpaRepository;

public interface QuestRepository extends JpaRepository<Quest, Long> {
}
