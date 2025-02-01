package depth.main_project.PayKids_Server.domain.quest.dto;

import depth.main_project.PayKids_Server.domain.quest.entity.Quest;
import depth.main_project.PayKids_Server.domain.user.entity.User;
import lombok.Getter;
import lombok.Setter;
import org.springframework.context.ApplicationEvent;

@Getter
@Setter
public class UserQuestUpdateEvent extends ApplicationEvent {
    private final User user;
    private final Quest quest;

    public UserQuestUpdateEvent(User user, Quest quest) {
        super(user);
        this.user = user;
        this.quest = quest;
    }

    public User getUser() {
        return user;
    }

    public Quest getQuest() {return quest;}
}
