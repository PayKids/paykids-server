package depth.main_project.PayKids_Server.domain.quiz.repository;

import depth.main_project.PayKids_Server.domain.quiz.entity.StageName;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface StageNameRepository extends JpaRepository<StageName,Long> {
    StageName findOneByStageNumber(int stageNumber);
}
