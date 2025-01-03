package depth.main_project.PayKids_Server.domain.allowance.entity;

import depth.main_project.PayKids_Server.domain.user.entity.User;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Entity
@Getter
@Table(name = "Category")
public class Category {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String title = "기타";
    private AllowanceType allowanceType;

    @ManyToOne (fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @Builder
    public Category(String title, AllowanceType allowanceType, User user) {
        this.title = title;
        this.allowanceType = allowanceType;
        this.user = user;
    }
}
