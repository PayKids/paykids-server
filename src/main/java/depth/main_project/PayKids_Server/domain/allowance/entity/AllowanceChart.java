package depth.main_project.PayKids_Server.domain.allowance.entity;

import depth.main_project.PayKids_Server.domain.user.entity.User;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@NoArgsConstructor
@Entity
@Getter
@Setter
@Table(name = "AllowanceChart")
public class AllowanceChart {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private LocalDate date;
    private int amount;
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
    public AllowanceChart(LocalDate date, AllowanceType allowanceType, int amount, String memo, User user, Category category) {
        this.date = date;
        this.allowanceType = allowanceType;
        this.amount = amount;
        this.memo = memo;
        this.user = user;
        this.category = category;
    }
}
