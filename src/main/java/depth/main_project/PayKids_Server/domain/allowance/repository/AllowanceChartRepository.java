package depth.main_project.PayKids_Server.domain.allowance.repository;

import depth.main_project.PayKids_Server.domain.allowance.entity.AllowanceChart;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AllowanceChartRepository extends JpaRepository<AllowanceChart, Long> {
}
