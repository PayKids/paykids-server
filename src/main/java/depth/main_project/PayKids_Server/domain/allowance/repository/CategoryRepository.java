package depth.main_project.PayKids_Server.domain.allowance.repository;

import depth.main_project.PayKids_Server.domain.allowance.entity.AllowanceType;
import depth.main_project.PayKids_Server.domain.allowance.entity.Category;
import depth.main_project.PayKids_Server.domain.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {
    List<Category> findAllByUserAndAllowanceType(User user, AllowanceType allowanceType);
    Optional<Category> findByUserAndAllowanceTypeAndTitle(User user, AllowanceType allowanceType, String title);
    List<Category> findAllByUser(User user);
}
