package depth.main_project.PayKids_Server.domain.allowance.entity;

import depth.main_project.PayKids_Server.domain.user.entity.User;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;

@NoArgsConstructor
@Entity
@Getter
@Setter
@Table(name = "AllowanceChart")
public class AllowanceChart {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private LocalDateTime date;
    private AllowanceType allowanceType;

    @Column(nullable = true)
    private String memo;

    @ManyToOne (fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne (fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id")
    private Category category;

    @Builder
    public AllowanceChart(LocalDateTime date, AllowanceType allowanceType, String memo, User user, Category category) {
        this.date = date;
        this.allowanceType = allowanceType;
        this.memo = memo;
        this.user = user;
        this.category = category;
    }
}
