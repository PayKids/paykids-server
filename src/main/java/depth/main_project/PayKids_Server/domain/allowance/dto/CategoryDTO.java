package depth.main_project.PayKids_Server.domain.allowance.dto;

import depth.main_project.PayKids_Server.domain.allowance.entity.AllowanceType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@AllArgsConstructor
@Getter
@Builder
public class CategoryDTO {
    private Long id;
    private String title;
    private AllowanceType allowanceType;
}
