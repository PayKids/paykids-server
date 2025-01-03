package depth.main_project.PayKids_Server.domain.allowance.repository;

import depth.main_project.PayKids_Server.domain.allowance.entity.AllowanceChart;
import depth.main_project.PayKids_Server.domain.allowance.entity.AllowanceType;
import depth.main_project.PayKids_Server.domain.allowance.entity.Category;
import depth.main_project.PayKids_Server.domain.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AllowanceChartRepository extends JpaRepository<AllowanceChart, Long> {
    List<AllowanceChart> findAllByUserAndAllowanceTypeAndCategory(User user, AllowanceType allowanceType, Category category);
    List<AllowanceChart> findAllByUserAndAllowanceType(User user, AllowanceType allowanceType);
    Optional<AllowanceChart> findByIdAndUserAndAllowanceType(Long id, User user, AllowanceType allowanceType);
}
