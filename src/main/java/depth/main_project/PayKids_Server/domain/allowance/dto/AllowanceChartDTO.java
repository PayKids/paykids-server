package depth.main_project.PayKids_Server.domain.allowance.dto;

import depth.main_project.PayKids_Server.domain.allowance.entity.AllowanceType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@AllArgsConstructor
@Getter
@Setter
@Builder
public class AllowanceChartDTO {
    private Long id;
    private LocalDate date;
    private AllowanceType allowanceType;
    private String category;
    private int amount;
    private String memo;
}
