package depth.main_project.PayKids_Server.domain.allowance.service;

import depth.main_project.PayKids_Server.domain.allowance.dto.AllowanceChartAmountDTO;
import depth.main_project.PayKids_Server.domain.allowance.dto.AllowanceChartCategoryDTO;
import depth.main_project.PayKids_Server.domain.allowance.dto.AllowanceChartDTO;
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

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Transactional
@Service
public class IncomeService {
    private final AllowanceChartRepository allowanceChartRepository;
    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;
    private final TokenService tokenService;
    private final IncomeCategoryService incomeCategoryService;

    //월별 수입 총 금액 조회
    public int getMonthlyIncomeAmount(int year, int month, String token){
        String userId = tokenService.getUserUuidFromToken(token);
        int totalAmount = 0;

        if (tokenService.expiredToken(token) == false){
            throw new MapperException(ErrorCode.TOKEN_EXPIRED_ERROR);
        }

        if (userId == null) {
            throw new MapperException(ErrorCode.TOKEN_ERROR);
        }

        User user = userRepository.findByUuid(userId)
                .orElseThrow(() -> new MapperException(ErrorCode.USER_NOT_FOUND));

        List<AllowanceChart> allowanceChartList = allowanceChartRepository.findAllByUserAndAllowanceType(user, AllowanceType.INCOME);

        if (allowanceChartList.isEmpty()) {
            throw new MapperException(ErrorCode.ALLOWANCE_NOT_FOUND);
        }

        for (AllowanceChart allowanceChart : allowanceChartList) {
            if (allowanceChart.getDate().getYear() == year && allowanceChart.getDate().getMonthValue() == month){
                totalAmount += allowanceChart.getAmount();
            }
        }

        return totalAmount;
    }

    //지정한 월에 일별로 수입 금액만 데이터 제공
    public List<AllowanceChartAmountDTO> getAllDailyIncomeAmount(int year, int month, String token){
        String userId = tokenService.getUserUuidFromToken(token);
        List<Integer> amountList = new ArrayList<>();
        List<AllowanceChartAmountDTO> allowanceChartAmountDTOList = new ArrayList<>();
        YearMonth yearMonth = YearMonth.of(year, month);
        int daysOfMonth = yearMonth.lengthOfMonth();

        for (int i = 0; i < daysOfMonth; i++) {
            amountList.add(0);
        }

        if (tokenService.expiredToken(token) == false){
            throw new MapperException(ErrorCode.TOKEN_EXPIRED_ERROR);
        }

        if (userId == null) {
            throw new MapperException(ErrorCode.TOKEN_ERROR);
        }

        User user = userRepository.findByUuid(userId)
                .orElseThrow(() -> new MapperException(ErrorCode.USER_NOT_FOUND));

        List<AllowanceChart> allowanceChartList = allowanceChartRepository.findAllByUserAndAllowanceType(user, AllowanceType.INCOME);

        if (allowanceChartList.isEmpty()) {
            return allowanceChartAmountDTOList;
        }

        for (AllowanceChart allowanceChart : allowanceChartList) {
            if (allowanceChart.getDate().getYear() == year && allowanceChart.getDate().getMonthValue() == month){
                amountList.set(allowanceChart.getDate().getDayOfMonth() - 1, amountList.get(allowanceChart.getDate().getDayOfMonth() - 1) + allowanceChart.getAmount());
            }
        }

        for (int i = 0; i < amountList.size(); i++) {
            AllowanceChartAmountDTO allowanceChartAmountDTO = AllowanceChartAmountDTO.builder()
                    .allowanceType(AllowanceType.INCOME)
                    .amount(amountList.get(i))
                    .date(LocalDate.of(year, month, i + 1))
                    .build();

            allowanceChartAmountDTOList.add(allowanceChartAmountDTO);
        }

        return allowanceChartAmountDTOList;
    }

    //월, 카테고리별 수입 확인
    public List<AllowanceChartCategoryDTO> getMonthlyCategoriesIncome(int year, int month, String token){
        String userId = tokenService.getUserUuidFromToken(token);
        double totalAmount = 0;
        List<AllowanceChartCategoryDTO> allowanceChartCategoryDTOList = new ArrayList<>();

        if (tokenService.expiredToken(token) == false){
            throw new MapperException(ErrorCode.TOKEN_EXPIRED_ERROR);
        }

        if (userId == null) {
            throw new MapperException(ErrorCode.TOKEN_ERROR);
        }

        List<CategoryDTO> categoryDTOList = incomeCategoryService.getIncomeAllCategory(token);
        List<Integer> amountList = new ArrayList<>();

        for (int i = 0; i < categoryDTOList.size(); i++) {
            amountList.add(0);
        }

        User user = userRepository.findByUuid(userId)
                .orElseThrow(() -> new MapperException(ErrorCode.USER_NOT_FOUND));

        List<AllowanceChart> allowanceChartList = allowanceChartRepository.findAllByUserAndAllowanceType(user, AllowanceType.INCOME);

        if (allowanceChartList.isEmpty()) {
            return allowanceChartCategoryDTOList;
        }

        for (AllowanceChart allowanceChart : allowanceChartList) {
            if (allowanceChart.getDate().getYear() == year && allowanceChart.getDate().getMonthValue() == month){
                for (int i = 0; i < categoryDTOList.size(); i++) {
                    if (categoryDTOList.get(i).getTitle().equals(allowanceChart.getCategory().getTitle())){
                        amountList.set(i, amountList.get(i) + allowanceChart.getAmount());
                    }
                }

                totalAmount += allowanceChart.getAmount();
            }
        }

        for (int i = 0; i < amountList.size(); i++) {
            if (amountList.get(i) == 0){
                continue;
            }

            double percentage = (amountList.get(i) / totalAmount) * 100;
            String formatted = String.format("%.2f%%", percentage);

            AllowanceChartCategoryDTO allowanceChartCategoryDTO = AllowanceChartCategoryDTO.builder()
                    .category(categoryDTOList.get(i).getTitle())
                    .allowanceType(AllowanceType.INCOME)
                    .amount(amountList.get(i))
                    .percent(formatted)
                    .build();

            allowanceChartCategoryDTOList.add(allowanceChartCategoryDTO);
        }

        return allowanceChartCategoryDTOList;
    }

    //날짜 별 수입 내역 제공
    public List<AllowanceChartDTO> getDailyIncome(int year, int month, int day, String token){
        String userId = tokenService.getUserUuidFromToken(token);
        List<AllowanceChartDTO> allowanceChartDTOList = new ArrayList<>();

        if (tokenService.expiredToken(token) == false){
            throw new MapperException(ErrorCode.TOKEN_EXPIRED_ERROR);
        }

        if (userId == null) {
            throw new MapperException(ErrorCode.TOKEN_ERROR);
        }

        User user = userRepository.findByUuid(userId)
                .orElseThrow(() -> new MapperException(ErrorCode.USER_NOT_FOUND));

        List<AllowanceChart> allowanceChartList = allowanceChartRepository.findAllByUserAndAllowanceType(user, AllowanceType.INCOME);

        if (allowanceChartList.isEmpty()) {
            return allowanceChartDTOList;
        }

        for (AllowanceChart allowanceChart : allowanceChartList) {
            if (allowanceChart.getDate().getYear() == year && allowanceChart.getDate().getMonthValue() == month && allowanceChart.getDate().getDayOfMonth() == day){
                AllowanceChartDTO allowanceChartDTO = AllowanceChartDTO.builder()
                        .id(allowanceChart.getId())
                        .allowanceType(AllowanceType.INCOME)
                        .amount(allowanceChart.getAmount())
                        .memo(allowanceChart.getMemo())
                        .category(allowanceChart.getCategory().getTitle())
                        .date(allowanceChart.getDate())
                        .build();

                allowanceChartDTOList.add(allowanceChartDTO);
            }
        }

        return allowanceChartDTOList;
    }

    //월, 특정 카테고리 수입 내역 조회
    public List<AllowanceChartDTO> getMonthlyCategoryIncome(int year, int month, String token, String category){
        String userId = tokenService.getUserUuidFromToken(token);
        List<AllowanceChartDTO> allowanceChartDTOList = new ArrayList<>();
        category = category.replaceAll("\\s", "");

        if (tokenService.expiredToken(token) == false){
            throw new MapperException(ErrorCode.TOKEN_EXPIRED_ERROR);
        }

        if (userId == null) {
            throw new MapperException(ErrorCode.TOKEN_ERROR);
        }

        User user = userRepository.findByUuid(userId)
                .orElseThrow(() -> new MapperException(ErrorCode.USER_NOT_FOUND));

        List<AllowanceChart> allowanceChartList = allowanceChartRepository.findAllByUserAndAllowanceType(user, AllowanceType.INCOME);

        if (allowanceChartList.isEmpty()) {
            return allowanceChartDTOList;
        }

        for (AllowanceChart allowanceChart : allowanceChartList) {
            if (allowanceChart.getDate().getYear() == year && allowanceChart.getDate().getMonthValue() == month && allowanceChart.getCategory().getTitle().equals(category)){
                AllowanceChartDTO allowanceChartDTO = AllowanceChartDTO.builder()
                        .id(allowanceChart.getId())
                        .allowanceType(AllowanceType.INCOME)
                        .amount(allowanceChart.getAmount())
                        .memo(allowanceChart.getMemo())
                        .category(allowanceChart.getCategory().getTitle())
                        .date(allowanceChart.getDate())
                        .build();

                allowanceChartDTOList.add(allowanceChartDTO);
            }
        }

        return allowanceChartDTOList;
    }

    //수입 내역 저장 메서드
    @Transactional
    public Boolean saveIncome(int year, int month, int day, int amount, String category, String memo, String token){
        String userId = tokenService.getUserUuidFromToken(token);

        if (tokenService.expiredToken(token) == false){
            throw new MapperException(ErrorCode.TOKEN_EXPIRED_ERROR);
        }

        if (userId == null) {
            throw new MapperException(ErrorCode.TOKEN_ERROR);
        }

        User user = userRepository.findByUuid(userId)
                .orElseThrow(() -> new MapperException(ErrorCode.USER_NOT_FOUND));

        Optional<Category> categoryEntity = categoryRepository.findByUserAndAllowanceTypeAndTitle(user, AllowanceType.INCOME, category);

        if (categoryEntity.isEmpty()) {
            throw new MapperException(ErrorCode.CATEGORY_NOT_FOUND);
        }

        AllowanceChart allowanceChart = AllowanceChart.builder()
                .date(LocalDate.of(year, month, day))
                .allowanceType(AllowanceType.INCOME)
                .category(categoryEntity.get())
                .amount(amount)
                .memo(memo)
                .user(user)
                .build();

        allowanceChartRepository.save(allowanceChart);

        return true;
    }

    //수입 내역 삭제
    @Transactional
    public Boolean deleteIncome (Long id, String token){
        String userId = tokenService.getUserUuidFromToken(token);

        if (tokenService.expiredToken(token) == false){
            throw new MapperException(ErrorCode.TOKEN_EXPIRED_ERROR);
        }

        if (userId == null) {
            throw new MapperException(ErrorCode.TOKEN_ERROR);
        }

        User user = userRepository.findByUuid(userId)
                .orElseThrow(() -> new MapperException(ErrorCode.USER_NOT_FOUND));

        Optional<AllowanceChart> allowanceChart = allowanceChartRepository.findByIdAndUserAndAllowanceType(id, user, AllowanceType.INCOME);

        if (allowanceChart.isEmpty()) {
            throw new MapperException(ErrorCode.NO_ALLOWANCE_FOUND);
        }

        allowanceChartRepository.delete(allowanceChart.get());

        return true;
    }

    //수입내역 수정
    @Transactional
    public Boolean modifyIncome (Long id, int year, int month, int day, int amount, String category, String memo, String token){
        String userId = tokenService.getUserUuidFromToken(token);

        if (tokenService.expiredToken(token) == false){
            throw new MapperException(ErrorCode.TOKEN_EXPIRED_ERROR);
        }

        if (userId == null) {
            throw new MapperException(ErrorCode.TOKEN_ERROR);
        }

        User user = userRepository.findByUuid(userId)
                .orElseThrow(() -> new MapperException(ErrorCode.USER_NOT_FOUND));

        Optional<AllowanceChart> allowanceChart = allowanceChartRepository.findByIdAndUserAndAllowanceType(id, user, AllowanceType.INCOME);

        if (allowanceChart.isEmpty()) {
            throw new MapperException(ErrorCode.NO_ALLOWANCE_FOUND);
        }

        Optional<Category> categoryOptional = categoryRepository.findByUserAndAllowanceTypeAndTitle(user, AllowanceType.INCOME, category);

        if (categoryOptional.isEmpty()) {
            throw new MapperException(ErrorCode.CATEGORY_NOT_FOUND);
        }

        allowanceChart.get().setCategory(categoryOptional.get());
        allowanceChart.get().setMemo(memo);
        allowanceChart.get().setAmount(amount);
        allowanceChart.get().setDate(LocalDate.of(year, month, day));

        allowanceChartRepository.save(allowanceChart.get());

        return true;
    }
}
