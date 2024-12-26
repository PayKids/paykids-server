package depth.main_project.PayKids_Server.domain.allowance.dto;

import depth.main_project.PayKids_Server.domain.allowance.entity.AllowanceType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@AllArgsConstructor
@Getter
@Builder
public class AllowanceChartDTO {
    private Long id;
    private LocalDateTime date;
    private AllowanceType allowanceType;
    private String memo;
}
