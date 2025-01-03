package depth.main_project.PayKids_Server.domain.allowance.controller;

import depth.main_project.PayKids_Server.domain.allowance.dto.AllowanceChartAmountDTO;
import depth.main_project.PayKids_Server.domain.allowance.dto.AllowanceChartCategoryDTO;
import depth.main_project.PayKids_Server.domain.allowance.dto.AllowanceChartDTO;
import depth.main_project.PayKids_Server.domain.allowance.service.IncomeService;
import depth.main_project.PayKids_Server.global.dto.ApiResult;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/income/allowance")
@RequiredArgsConstructor
public class IncomeAllowanceController {
    private final IncomeService incomeService;

    @Operation(summary = "월 수입 전체 금액 조회", description = "주어진 달의 전체 수입 금액을 반환합니다. 수입 내역이 없으면 error return 합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "성공적으로 확인됨"),
            @ApiResponse(responseCode = "400", description = "잘못된 입력 값"),
            @ApiResponse(responseCode = "500", description = "서버 오류 발생")
    })
    @GetMapping("/month-total-amount")
    public ApiResult<Integer> getMonthTotalIncomeAmount(@RequestParam Integer year, @RequestParam Integer month, @RequestParam String token) {
        return ApiResult.ok(incomeService.getMonthlyIncomeAmount(year, month, token));
    }

    @Operation(summary = "월, 일별 수입 금액 조회", description = "주어진 달의 일별 수입 금액을 반환합니다. 수입 내역이 없으면 error return 합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "성공적으로 확인됨"),
            @ApiResponse(responseCode = "400", description = "잘못된 입력 값"),
            @ApiResponse(responseCode = "500", description = "서버 오류 발생")
    })
    @GetMapping("/month-daily-amount")
    public ApiResult<List<AllowanceChartAmountDTO>> getMonthDailyIncomeAmount(@RequestParam Integer year, @RequestParam Integer month, @RequestParam String token) {
        return ApiResult.ok(incomeService.getAllDailyIncomeAmount(year, month, token));
    }

    @Operation(summary = "월, 모든 카테고리 수입 조회", description = "주어진 달의 모든 카테고리 수입를 반환합니다. 수입 내역이 없으면 error return 합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "성공적으로 확인됨"),
            @ApiResponse(responseCode = "400", description = "잘못된 입력 값"),
            @ApiResponse(responseCode = "500", description = "서버 오류 발생")
    })
    @GetMapping("/month-all-category")
    public ApiResult<List<AllowanceChartCategoryDTO>> getMonthAllCategoryIncome(@RequestParam Integer year, @RequestParam Integer month, @RequestParam String token) {
        return ApiResult.ok(incomeService.getMonthlyCategoriesIncome(year, month, token));
    }

    @Operation(summary = "일 수입 조회", description = "주어진 일의 수입 내역을 반환합니다. 수입 내역이 없으면 error return 합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "성공적으로 확인됨"),
            @ApiResponse(responseCode = "400", description = "잘못된 입력 값"),
            @ApiResponse(responseCode = "500", description = "서버 오류 발생")
    })
    @GetMapping("/day")
    public ApiResult<List<AllowanceChartDTO>> getDailyIncomeAmount(@RequestParam LocalDate localDate, @RequestParam String token) {
        return ApiResult.ok(incomeService.getDailyIncome(localDate.getYear(), localDate.getMonthValue(), localDate.getDayOfMonth(), token));
    }

    @Operation(summary = "월, 특정 카테고리 수입 조회", description = "주어진 달의 특정 카테고리 수입 내역을 반환합니다. 수입 내역이 없으면 error return 합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "성공적으로 확인됨"),
            @ApiResponse(responseCode = "400", description = "잘못된 입력 값"),
            @ApiResponse(responseCode = "500", description = "서버 오류 발생")
    })
    @GetMapping("/month-category")
    public ApiResult<List<AllowanceChartDTO>> getMonthCategoryIncome(@RequestParam Integer year, @RequestParam Integer month, @RequestParam String token, @RequestParam String category){
        return ApiResult.ok(incomeService.getMonthlyCategoryIncome(year, month, token, category));
    }

    @Operation(summary = "수입 내역 저장", description = "수입 내역을 저장합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "성공적으로 확인됨"),
            @ApiResponse(responseCode = "400", description = "잘못된 입력 값"),
            @ApiResponse(responseCode = "500", description = "서버 오류 발생")
    })
    @PostMapping("/save")
    public ApiResult<Boolean> saveIncome(@RequestBody AllowanceChartDTO allowanceChartDTO, @RequestParam String token) {
        return ApiResult.ok(incomeService.saveIncome(
                allowanceChartDTO.getDate().getYear(),
                allowanceChartDTO.getDate().getMonthValue(),
                allowanceChartDTO.getDate().getDayOfMonth(),
                allowanceChartDTO.getAmount(),
                allowanceChartDTO.getCategory(),
                allowanceChartDTO.getMemo(),
                token));
    }

    @Operation(summary = "수입 내역 삭제", description = "수입 내역을 삭제합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "성공적으로 확인됨"),
            @ApiResponse(responseCode = "400", description = "잘못된 입력 값"),
            @ApiResponse(responseCode = "500", description = "서버 오류 발생")
    })
    @DeleteMapping("/delete")
    public ApiResult<Boolean> deleteIncome(@RequestParam Long id, @RequestParam String token) {
        return ApiResult.ok(incomeService.deleteIncome(id, token));
    }

    @Operation(summary = "수입 내역 수정", description = "수입 내역을 수정합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "성공적으로 확인됨"),
            @ApiResponse(responseCode = "400", description = "잘못된 입력 값"),
            @ApiResponse(responseCode = "500", description = "서버 오류 발생")
    })
    @PostMapping("/replace")
    public ApiResult<Boolean> replaceIncome(@RequestBody AllowanceChartDTO allowanceChartDTO, @RequestParam String token){
        return ApiResult.ok(incomeService.modifyIncome(
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
