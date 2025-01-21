package depth.main_project.PayKids_Server.domain.allowance.service;

import depth.main_project.PayKids_Server.domain.allowance.dto.CategoryDTO;
import depth.main_project.PayKids_Server.domain.allowance.entity.AllowanceChart;
import depth.main_project.PayKids_Server.domain.allowance.entity.AllowanceType;
import depth.main_project.PayKids_Server.domain.allowance.entity.Category;
import depth.main_project.PayKids_Server.domain.allowance.repository.AllowanceChartRepository;
import depth.main_project.PayKids_Server.domain.allowance.repository.CategoryRepository;
import depth.main_project.PayKids_Server.domain.auth.TokenService;
import depth.main_project.PayKids_Server.domain.user.entity.User;
import depth.main_project.PayKids_Server.domain.user.repository.UserRepository;
import depth.main_project.PayKids_Server.global.exception.ErrorCode;
import depth.main_project.PayKids_Server.global.exception.MapperException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Service
public class IncomeCategoryService {
    private final CategoryRepository categoryRepository;
    private final AllowanceChartRepository allowanceChartRepository;
    private final UserRepository userRepository;
    private final TokenService tokenService;

    //수입 카테고리 전부 조회
    public List<CategoryDTO> getIncomeAllCategory(String token) {
        String userId = tokenService.getUserUuidFromToken(token);
        List<CategoryDTO> categoryDTOList = new ArrayList<>();

        if (tokenService.expiredToken(token) == false){
            throw new MapperException(ErrorCode.TOKEN_EXPIRED_ERROR);
        }

        if (userId == null) {
            throw new MapperException(ErrorCode.TOKEN_ERROR);
        }

        User user = userRepository.findByUuid(userId)
                .orElseThrow(() -> new MapperException(ErrorCode.USER_NOT_FOUND));

        List<Category> categoryList = categoryRepository.findAllByUserAndAllowanceTypeOrderByIdDesc(user, AllowanceType.INCOME);

        if (categoryList == null || categoryList.size() == 0) {
            throw new MapperException(ErrorCode.CATEGORY_NOT_FOUND);
        }

        for (Category category : categoryList) {
            CategoryDTO categoryDTO = CategoryDTO.builder()
                    .id(category.getId())
                    .title(category.getTitle())
                    .allowanceType(category.getAllowanceType())
                    .build();

            categoryDTOList.add(categoryDTO);
        }

        return categoryDTOList;
    }

    //이미 존재하는 카테고리인지 확인
    public Boolean isExistIncomeCategory(String token, String categoryName) {
        String userId = tokenService.getUserUuidFromToken(token);
        categoryName = categoryName.replaceAll("\\s", "");

        if (tokenService.expiredToken(token) == false){
            throw new MapperException(ErrorCode.TOKEN_EXPIRED_ERROR);
        }

        if (userId == null) {
            throw new MapperException(ErrorCode.TOKEN_ERROR);
        }

        User user = userRepository.findByUuid(userId)
                .orElseThrow(() -> new MapperException(ErrorCode.USER_NOT_FOUND));

        List<Category> categoryList = categoryRepository.findAllByUserAndAllowanceTypeOrderByIdDesc(user, AllowanceType.INCOME);

        if (categoryList == null || categoryList.size() == 0) {
            return false;
        }

        for (Category category : categoryList) {
            if (categoryName.equals(category.getTitle().replaceAll("\\s", ""))) {
                return true;
            }
        }

        return false;
    }

    //글자수 확인 로직, 5글자 초과인 경우 제한
    public Boolean isLongCategory(String categoryName) {
        categoryName = categoryName.replaceAll("\\s", "");

        if (categoryName.length() > 5){
            return true;
        }

        return false;
    }

    //수입 카테고리 저장 로직
    @Transactional
    public Boolean saveIncomeCategory(String token, String name) {
        String userId = tokenService.getUserUuidFromToken(token);
        name = name.replaceAll("\\s", "");

        if (tokenService.expiredToken(token) == false){
            throw new MapperException(ErrorCode.TOKEN_EXPIRED_ERROR);
        }

        if (userId == null) {
            throw new MapperException(ErrorCode.TOKEN_ERROR);
        }

        User user = userRepository.findByUuid(userId)
                .orElseThrow(() -> new MapperException(ErrorCode.USER_NOT_FOUND));

        Category category = Category.builder()
                .user(user)
                .title(name)
                .allowanceType(AllowanceType.INCOME)
                .build();

        categoryRepository.save(category);

        return true;
    }

    //소비 카테고리 삭제, 기타인 경우 삭제하지 않음.
    @Transactional
    public Boolean deleteIncomeCategory(String token, String name) {
        name = name.replaceAll("\\s", "");

        if (name.equals("기타")){
            throw new MapperException(ErrorCode.CATEGORY_ERROR);
        }

        String userId = tokenService.getUserUuidFromToken(token);

        if (tokenService.expiredToken(token) == false){
            throw new MapperException(ErrorCode.TOKEN_EXPIRED_ERROR);
        }

        if (userId == null) {
            throw new MapperException(ErrorCode.TOKEN_ERROR);
        }

        User user = userRepository.findByUuid(userId)
                .orElseThrow(() -> new MapperException(ErrorCode.USER_NOT_FOUND));

        Optional<Category> category = categoryRepository.findByUserAndAllowanceTypeAndTitle(user, AllowanceType.INCOME, name);
        Optional<Category> changeCategory = categoryRepository.findByUserAndAllowanceTypeAndTitle(user, AllowanceType.INCOME, "기타");

        if (category.isPresent() && changeCategory.isPresent()) {
            List<AllowanceChart> allowanceChartList = allowanceChartRepository.findAllByUserAndAllowanceTypeAndCategory(user, AllowanceType.INCOME, category.get());

            if (allowanceChartList == null || allowanceChartList.size() == 0) {
                categoryRepository.deleteById(category.get().getId());
                return true;
            }

            for (AllowanceChart allowanceChart : allowanceChartList) {
                allowanceChart.setCategory(changeCategory.get());
            }
            allowanceChartRepository.saveAll(allowanceChartList);

            categoryRepository.deleteById(category.get().getId());

            return true;
        } else {
            throw new MapperException(ErrorCode.CATEGORY_NOT_FOUND);
        }
    }
}
