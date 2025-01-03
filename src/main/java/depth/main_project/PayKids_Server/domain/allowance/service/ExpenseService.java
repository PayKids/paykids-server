package depth.main_project.PayKids_Server.domain.allowance.service;

import depth.main_project.PayKids_Server.domain.allowance.dto.AllowanceChartCategoryDTO;
import depth.main_project.PayKids_Server.domain.allowance.dto.AllowanceChartDTO;
import depth.main_project.PayKids_Server.domain.allowance.dto.AllowanceChartAmountDTO;
import depth.main_project.PayKids_Server.domain.allowance.dto.CategoryDTO;
import depth.main_project.PayKids_Server.domain.allowance.entity.AllowanceChart;
import depth.main_project.PayKids_Server.domain.allowance.entity.AllowanceType;
import depth.main_project.PayKids_Server.domain.allowance.entity.Category;
import depth.main_project.PayKids_Server.domain.allowance.repository.AllowanceChartRepository;
import depth.main_project.PayKids_Server.domain.allowance.repository.CategoryRepository;
import depth.main_project.PayKids_Server.domain.user.entity.User;
import depth.main_project.PayKids_Server.domain.user.repository.UserRepository;
import depth.main_project.PayKids_Server.global.exception.ErrorCode;
import depth.main_project.PayKids_Server.global.exception.MapperException;
import depth.main_project.PayKids_Server.global.token.TokenService;
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
public class ExpenseService {
    private final AllowanceChartRepository allowanceChartRepository;
    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;
    private final TokenService tokenService;
    private final ExpenseCategoryService expenseCategoryService;

    //월별 소비 총 금액 조회
    public int getMonthlyExpenseAmount(int year, int month, String token){
        Long userId = tokenService.getUserIdFromToken(token);
        int totalAmount = 0;

        if (tokenService.expiredToken(token) == false){
            throw new MapperException(ErrorCode.TOKEN_EXPIRED_ERROR);
        }

        if (userId == null) {
            throw new MapperException(ErrorCode.TOKEN_ERROR);
        }

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new MapperException(ErrorCode.USER_NOT_FOUND));

        List<AllowanceChart> allowanceChartList = allowanceChartRepository.findAllByUserAndAllowanceType(user, AllowanceType.EXPENSE);

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

    //지정한 월에 일별로 지출 금액만 데이터 제공
    public List<AllowanceChartAmountDTO> getAllDailyExpenseAmount(int year, int month, String token){
        Long userId = tokenService.getUserIdFromToken(token);
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

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new MapperException(ErrorCode.USER_NOT_FOUND));

        List<AllowanceChart> allowanceChartList = allowanceChartRepository.findAllByUserAndAllowanceType(user, AllowanceType.EXPENSE);

        if (allowanceChartList.isEmpty()) {
            throw new MapperException(ErrorCode.ALLOWANCE_NOT_FOUND);
        }

        for (AllowanceChart allowanceChart : allowanceChartList) {
            if (allowanceChart.getDate().getYear() == year && allowanceChart.getDate().getMonthValue() == month){
                amountList.set(allowanceChart.getDate().getDayOfMonth() - 1, amountList.get(allowanceChart.getDate().getDayOfMonth() - 1) + allowanceChart.getAmount());
            }
        }

        for (int i = 0; i < amountList.size(); i++) {
            AllowanceChartAmountDTO allowanceChartAmountDTO = AllowanceChartAmountDTO.builder()
                    .allowanceType(AllowanceType.EXPENSE)
                    .amount(amountList.get(i))
                    .date(LocalDate.of(year, month, i + 1))
                    .build();

            allowanceChartAmountDTOList.add(allowanceChartAmountDTO);
        }

        if (allowanceChartAmountDTOList.isEmpty()) {
            throw new MapperException(ErrorCode.NO_ALLOWANCE_FOUND);
        }

        return allowanceChartAmountDTOList;
    }

    //월, 카테고리별 소비 확인
    public List<AllowanceChartCategoryDTO> getMonthlyCategoriesExpense(int year, int month, String token){
        Long userId = tokenService.getUserIdFromToken(token);
        double totalAmount = 0;
        List<AllowanceChartCategoryDTO> allowanceChartCategoryDTOList = new ArrayList<>();

        if (tokenService.expiredToken(token) == false){
            throw new MapperException(ErrorCode.TOKEN_EXPIRED_ERROR);
        }

        if (userId == null) {
            throw new MapperException(ErrorCode.TOKEN_ERROR);
        }

        List<CategoryDTO> categoryDTOList = expenseCategoryService.getExpenseAllCategory(token);
        List<Integer> amountList = new ArrayList<>();

        for (int i = 0; i < categoryDTOList.size(); i++) {
            amountList.add(0);
        }

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new MapperException(ErrorCode.USER_NOT_FOUND));

        List<AllowanceChart> allowanceChartList = allowanceChartRepository.findAllByUserAndAllowanceType(user, AllowanceType.EXPENSE);

        if (allowanceChartList.isEmpty()) {
            throw new MapperException(ErrorCode.ALLOWANCE_NOT_FOUND);
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
                    .allowanceType(AllowanceType.EXPENSE)
                    .amount(amountList.get(i))
                    .percent(formatted)
                    .build();

            allowanceChartCategoryDTOList.add(allowanceChartCategoryDTO);
        }

        if(allowanceChartCategoryDTOList.isEmpty()){
            throw new MapperException(ErrorCode.NO_ALLOWANCE_FOUND);
        }

        return allowanceChartCategoryDTOList;
    }

    //월, 카테고리 별 최다 소비 확인
    public AllowanceChartCategoryDTO getMostCategoryExpense(int year, int month, String token){
        List<AllowanceChartCategoryDTO> allowanceChartCategoryDTOList = getMonthlyCategoriesExpense(year, month ,token);
        int mostCategoryAmount = 0;
        String mostCategoryTitle = "";

        if (allowanceChartCategoryDTOList.isEmpty()) {
            throw new MapperException(ErrorCode.ALLOWANCE_NOT_FOUND);
        }

        for (AllowanceChartCategoryDTO allowanceChartCategoryDTO : allowanceChartCategoryDTOList) {
            if (allowanceChartCategoryDTO.getAmount() > mostCategoryAmount){
                mostCategoryAmount = allowanceChartCategoryDTO.getAmount();
                mostCategoryTitle = allowanceChartCategoryDTO.getCategory();
            }
        }

        return AllowanceChartCategoryDTO.builder()
                .category(mostCategoryTitle)
                .amount(mostCategoryAmount)
                .allowanceType(AllowanceType.EXPENSE)
                .percent(null)
                .build();
    }

    //날짜 별 소비 내역 제공
    public List<AllowanceChartDTO> getDailyExpense(int year, int month, int day, String token){
        Long userId = tokenService.getUserIdFromToken(token);
        List<AllowanceChartDTO> allowanceChartDTOList = new ArrayList<>();

        if (tokenService.expiredToken(token) == false){
            throw new MapperException(ErrorCode.TOKEN_EXPIRED_ERROR);
        }

        if (userId == null) {
            throw new MapperException(ErrorCode.TOKEN_ERROR);
        }

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new MapperException(ErrorCode.USER_NOT_FOUND));

        List<AllowanceChart> allowanceChartList = allowanceChartRepository.findAllByUserAndAllowanceType(user, AllowanceType.EXPENSE);

        if (allowanceChartList.isEmpty()) {
            throw new MapperException(ErrorCode.ALLOWANCE_NOT_FOUND);
        }

        for (AllowanceChart allowanceChart : allowanceChartList) {
            if (allowanceChart.getDate().getYear() == year && allowanceChart.getDate().getMonthValue() == month && allowanceChart.getDate().getDayOfMonth() == day){
                AllowanceChartDTO allowanceChartDTO = AllowanceChartDTO.builder()
                        .id(allowanceChart.getId())
                        .allowanceType(AllowanceType.EXPENSE)
                        .amount(allowanceChart.getAmount())
                        .memo(allowanceChart.getMemo())
                        .category(allowanceChart.getCategory().getTitle())
                        .date(allowanceChart.getDate())
                        .build();

                allowanceChartDTOList.add(allowanceChartDTO);
            }
        }

        if (allowanceChartDTOList.isEmpty()) {
            throw new MapperException(ErrorCode.NO_ALLOWANCE_FOUND);
        }

        return allowanceChartDTOList;
    }

    //월, 특정 카테고리 소비 내역 조회
    public List<AllowanceChartDTO> getMonthlyCategoryExpense(int year, int month, String token, String category){
        Long userId = tokenService.getUserIdFromToken(token);
        List<AllowanceChartDTO> allowanceChartDTOList = new ArrayList<>();
        category = category.replaceAll("\\s", "");

        if (tokenService.expiredToken(token) == false){
            throw new MapperException(ErrorCode.TOKEN_EXPIRED_ERROR);
        }

        if (userId == null) {
            throw new MapperException(ErrorCode.TOKEN_ERROR);
        }

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new MapperException(ErrorCode.USER_NOT_FOUND));

        List<AllowanceChart> allowanceChartList = allowanceChartRepository.findAllByUserAndAllowanceType(user, AllowanceType.EXPENSE);

        if (allowanceChartList.isEmpty()) {
            throw new MapperException(ErrorCode.ALLOWANCE_NOT_FOUND);
        }

        for (AllowanceChart allowanceChart : allowanceChartList) {
            if (allowanceChart.getDate().getYear() == year && allowanceChart.getDate().getMonthValue() == month && allowanceChart.getCategory().getTitle().equals(category)){
                AllowanceChartDTO allowanceChartDTO = AllowanceChartDTO.builder()
                        .id(allowanceChart.getId())
                        .allowanceType(AllowanceType.EXPENSE)
                        .amount(allowanceChart.getAmount())
                        .memo(allowanceChart.getMemo())
                        .category(allowanceChart.getCategory().getTitle())
                        .date(allowanceChart.getDate())
                        .build();

                allowanceChartDTOList.add(allowanceChartDTO);
            }
        }

        if (allowanceChartDTOList.isEmpty()) {
            throw new MapperException(ErrorCode.NO_ALLOWANCE_FOUND);
        }

        return allowanceChartDTOList;
    }

    //소비 내역 저장 메서드
    @Transactional
    public Boolean saveExpense(int year, int month, int day, int amount, String category, String memo, String token){
        Long userId = tokenService.getUserIdFromToken(token);

        if (tokenService.expiredToken(token) == false){
            throw new MapperException(ErrorCode.TOKEN_EXPIRED_ERROR);
        }

        if (userId == null) {
            throw new MapperException(ErrorCode.TOKEN_ERROR);
        }

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new MapperException(ErrorCode.USER_NOT_FOUND));

        Optional<Category> categoryEntity = categoryRepository.findByUserAndAllowanceTypeAndTitle(user, AllowanceType.EXPENSE, category);

        if (categoryEntity.isEmpty()) {
            throw new MapperException(ErrorCode.CATEGORY_NOT_FOUND);
        }

        AllowanceChart allowanceChart = AllowanceChart.builder()
                .date(LocalDate.of(year, month, day))
                .allowanceType(AllowanceType.EXPENSE)
                .category(categoryEntity.get())
                .amount(amount)
                .memo(memo)
                .user(user)
                .build();

        allowanceChartRepository.save(allowanceChart);

        return true;
    }

    //소비 내역 삭제
    @Transactional
    public Boolean deleteExpense (Long id, String token){
        Long userId = tokenService.getUserIdFromToken(token);

        if (tokenService.expiredToken(token) == false){
            throw new MapperException(ErrorCode.TOKEN_EXPIRED_ERROR);
        }

        if (userId == null) {
            throw new MapperException(ErrorCode.TOKEN_ERROR);
        }

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new MapperException(ErrorCode.USER_NOT_FOUND));

        Optional<AllowanceChart> allowanceChart = allowanceChartRepository.findByIdAndUserAndAllowanceType(id, user, AllowanceType.EXPENSE);

        if (allowanceChart.isEmpty()) {
            throw new MapperException(ErrorCode.NO_ALLOWANCE_FOUND);
        }

        allowanceChartRepository.delete(allowanceChart.get());

        return true;
    }

    //소비내역 수정
    @Transactional
    public Boolean modifyExpense (Long id, int year, int month, int day, int amount, String category, String memo, String token){
        Long userId = tokenService.getUserIdFromToken(token);

        if (tokenService.expiredToken(token) == false){
            throw new MapperException(ErrorCode.TOKEN_EXPIRED_ERROR);
        }

        if (userId == null) {
            throw new MapperException(ErrorCode.TOKEN_ERROR);
        }

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new MapperException(ErrorCode.USER_NOT_FOUND));

        Optional<AllowanceChart> allowanceChart = allowanceChartRepository.findByIdAndUserAndAllowanceType(id, user, AllowanceType.EXPENSE);

        if (allowanceChart.isEmpty()) {
            throw new MapperException(ErrorCode.NO_ALLOWANCE_FOUND);
        }

        Optional<Category> categoryOptional = categoryRepository.findByUserAndAllowanceTypeAndTitle(user, AllowanceType.EXPENSE, category);

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
