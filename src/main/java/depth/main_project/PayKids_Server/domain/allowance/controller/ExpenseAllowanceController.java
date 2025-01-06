package depth.main_project.PayKids_Server.domain.allowance.controller;

import depth.main_project.PayKids_Server.domain.allowance.dto.AllowanceChartAmountDTO;
import depth.main_project.PayKids_Server.domain.allowance.dto.AllowanceChartCategoryDTO;
import depth.main_project.PayKids_Server.domain.allowance.dto.AllowanceChartDTO;
import depth.main_project.PayKids_Server.domain.allowance.service.ExpenseService;
import depth.main_project.PayKids_Server.global.dto.ApiResult;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/expense/allowance")
@RequiredArgsConstructor
public class ExpenseAllowanceController {
    private final ExpenseService expenseService;

    @Operation(summary = "월 소비 전체 금액 조회", description = "주어진 달의 전체 소비 금액을 반환합니다. 소비 내역이 없으면 error return 합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "성공적으로 확인됨"),
            @ApiResponse(responseCode = "400", description = "잘못된 입력 값"),
            @ApiResponse(responseCode = "500", description = "서버 오류 발생")
    })
    @GetMapping("/month-total-amount")
    public ApiResult<Integer> getMonthTotalExpenseAmount(@RequestParam Integer year, @RequestParam Integer month, @RequestHeader("Authorization") String token) {
        return ApiResult.ok(expenseService.getMonthlyExpenseAmount(year, month, token));
    }

    @Operation(summary = "월, 일별 소비 금액 조회", description = "주어진 달의 일별 소비 금액을 반환합니다. 소비 내역이 없으면 error return 합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "성공적으로 확인됨"),
            @ApiResponse(responseCode = "400", description = "잘못된 입력 값"),
            @ApiResponse(responseCode = "500", description = "서버 오류 발생")
    })
    @GetMapping("/month-daily-amount")
    public ApiResult<List<AllowanceChartAmountDTO>> getMonthDailyExpenseAmount(@RequestParam Integer year, @RequestParam Integer month, @RequestHeader("Authorization") String token) {
        return ApiResult.ok(expenseService.getAllDailyExpenseAmount(year, month, token));
    }

    @Operation(summary = "월, 모든 카테고리 소비 조회", description = "주어진 달의 모든 카테고리 소비를 반환합니다. 소비 내역이 없으면 error return 합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "성공적으로 확인됨"),
            @ApiResponse(responseCode = "400", description = "잘못된 입력 값"),
            @ApiResponse(responseCode = "500", description = "서버 오류 발생")
    })
    @GetMapping("/month-all-category")
    public ApiResult<List<AllowanceChartCategoryDTO>> getMonthAllCategoryExpense(@RequestParam Integer year, @RequestParam Integer month, @RequestHeader("Authorization") String token) {
        return ApiResult.ok(expenseService.getMonthlyCategoriesExpense(year, month, token));
    }

    @Operation(summary = "월, 카테고리 중 최다 소비 조회", description = "주어진 달의 카테고리중 최다 소비를 반환합니다. 소비 내역이 없으면 error return 합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "성공적으로 확인됨"),
            @ApiResponse(responseCode = "400", description = "잘못된 입력 값"),
            @ApiResponse(responseCode = "500", description = "서버 오류 발생")
    })
    @GetMapping("/month-most-category")
    public ApiResult<AllowanceChartCategoryDTO> getMonthMostCategoryExpense(@RequestParam Integer year, @RequestParam Integer month, @RequestHeader("Authorization") String token) {
        return ApiResult.ok(expenseService.getMostCategoryExpense(year, month, token));
    }

    @Operation(summary = "일 소비 조회", description = "주어진 일의 소비 내역을 반환합니다. 소비 내역이 없으면 error return 합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "성공적으로 확인됨"),
            @ApiResponse(responseCode = "400", description = "잘못된 입력 값"),
            @ApiResponse(responseCode = "500", description = "서버 오류 발생")
    })
    @GetMapping("/day")
    public ApiResult<List<AllowanceChartDTO>> getDailyExpenseAmount(@RequestParam LocalDate localDate, @RequestHeader("Authorization") String token) {
        return ApiResult.ok(expenseService.getDailyExpense(localDate.getYear(), localDate.getMonthValue(), localDate.getDayOfMonth(), token));
    }

    @Operation(summary = "월, 특정 카테고리 소비 조회", description = "주어진 달의 특정 카테고리 소비 내역을 반환합니다. 소비 내역이 없으면 error return 합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "성공적으로 확인됨"),
            @ApiResponse(responseCode = "400", description = "잘못된 입력 값"),
            @ApiResponse(responseCode = "500", description = "서버 오류 발생")
    })
    @GetMapping("/month-category")
    public ApiResult<List<AllowanceChartDTO>> getMonthCategoryExpense(@RequestParam Integer year, @RequestParam Integer month, @RequestHeader("Authorization") String token, @RequestParam String category){
        return ApiResult.ok(expenseService.getMonthlyCategoryExpense(year, month, token, category));
    }

    @Operation(summary = "소비 내역 저장", description = "소비 내역을 저장합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "성공적으로 확인됨"),
            @ApiResponse(responseCode = "400", description = "잘못된 입력 값"),
            @ApiResponse(responseCode = "500", description = "서버 오류 발생")
    })
    @PostMapping("/save")
    public ApiResult<Boolean> saveExpense(@RequestBody AllowanceChartDTO allowanceChartDTO, @RequestHeader("Authorization") String token) {
        return ApiResult.ok(expenseService.saveExpense(
                allowanceChartDTO.getDate().getYear(),
                allowanceChartDTO.getDate().getMonthValue(),
                allowanceChartDTO.getDate().getDayOfMonth(),
                allowanceChartDTO.getAmount(),
                allowanceChartDTO.getCategory(),
                allowanceChartDTO.getMemo(),
                token));
    }

    @Operation(summary = "소비 내역 삭제", description = "소비 내역을 삭제합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "성공적으로 확인됨"),
            @ApiResponse(responseCode = "400", description = "잘못된 입력 값"),
            @ApiResponse(responseCode = "500", description = "서버 오류 발생")
    })
    @DeleteMapping("/delete")
    public ApiResult<Boolean> deleteExpense(@RequestParam Long id, @RequestHeader("Authorization") String token) {
        return ApiResult.ok(expenseService.deleteExpense(id, token));
    }

    @Operation(summary = "소비 내역 수정", description = "소비 내역을 수정합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "성공적으로 확인됨"),
            @ApiResponse(responseCode = "400", description = "잘못된 입력 값"),
            @ApiResponse(responseCode = "500", description = "서버 오류 발생")
    })
    @PostMapping("/replace")
    public ApiResult<Boolean> replaceExpense(@RequestBody AllowanceChartDTO allowanceChartDTO, @RequestHeader("Authorization") String token){
        return ApiResult.ok(expenseService.modifyExpense(
                allowanceChartDTO.getId(),
                allowanceChartDTO.getDate().getYear(),
                allowanceChartDTO.getDate().getMonthValue(),
                allowanceChartDTO.getDate().getDayOfMonth(),
                allowanceChartDTO.getAmount(),
                allowanceChartDTO.getCategory(),
                allowanceChartDTO.getMemo(),
                token
        ));
    }
}
