package depth.main_project.PayKids_Server.domain.quest.repository;

import depth.main_project.PayKids_Server.domain.quest.entity.Quest;
import depth.main_project.PayKids_Server.domain.quest.entity.UserQuest;
import depth.main_project.PayKids_Server.domain.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface UserQuestRepository extends JpaRepository<UserQuest, Long> {
    List<UserQuest> findAllByUser(User user);
    Optional<UserQuest> findByUserAndQuest(User user, Quest quest);

    @Query("SELECT q.name FROM UserQuest u JOIN u.quest q WHERE u.id = :questId")
    String findQuestNameByQuestId(Long questId);

    @Query("SELECT q.realCount FROM UserQuest u JOIN u.quest q WHERE u.id = :questId")
    Integer findQuestCountByQuestId(Long questId);
}
