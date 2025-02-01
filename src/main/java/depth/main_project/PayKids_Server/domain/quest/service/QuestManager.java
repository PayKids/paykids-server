package depth.main_project.PayKids_Server.domain.quest.service;

import depth.main_project.PayKids_Server.domain.quest.dto.UserQuestUpdateEvent;
import depth.main_project.PayKids_Server.domain.quest.entity.Quest;
import depth.main_project.PayKids_Server.domain.quest.entity.UserQuest;
import depth.main_project.PayKids_Server.domain.quest.repository.UserQuestRepository;
import depth.main_project.PayKids_Server.domain.user.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

import java.util.Optional;

@RequiredArgsConstructor
@Component
public class QuestManager {
    private final UserQuestRepository userQuestRepository;

    @TransactionalEventListener(phase = TransactionPhase.BEFORE_COMMIT)
    public void updateQuest(UserQuestUpdateEvent event) {
        Quest quest = event.getQuest();
        User user = event.getUser();

        Optional<UserQuest> userQuest = userQuestRepository.findByUserAndQuest(user, quest);

        if (userQuest.isPresent()) {
            if (userQuest.get().getCount() < quest.getRealCount()){
                int newCount = userQuest.get().getCount() + 1;
                userQuest.get().setCount(newCount);
                userQuestRepository.save(userQuest.get());
            } else {
                userQuest.get().setIsComplete(true);
                userQuestRepository.save(userQuest.get());
            }
        }
    }
}
