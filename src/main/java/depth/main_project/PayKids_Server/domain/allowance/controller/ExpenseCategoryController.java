package depth.main_project.PayKids_Server.domain.allowance.controller;

import depth.main_project.PayKids_Server.domain.allowance.dto.CategoryDTO;
import depth.main_project.PayKids_Server.domain.allowance.service.ExpenseCategoryService;
import depth.main_project.PayKids_Server.global.dto.ApiResult;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/expense/category")
@RequiredArgsConstructor
public class ExpenseCategoryController {
    private final ExpenseCategoryService expenseCategoryService;

    @Operation(summary = "소비 카테고리 전부 조회", description = "토큰으로 유저를 판별 한 후 보유한 카테고리를 반환합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "성공적으로 확인됨"),
            @ApiResponse(responseCode = "400", description = "잘못된 입력 값"),
            @ApiResponse(responseCode = "500", description = "서버 오류 발생")
    })
    @GetMapping("/category-list")
    public ApiResult<List<CategoryDTO>> getAllExpenseCategory(@RequestHeader("Authorization") String token) {
        return ApiResult.ok(expenseCategoryService.getExpenseAllCategory(token));
    }

    @Operation(summary = "소비 카테고리 저장", description = "카테고리 중복, 글자수 확인 후 저장합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "성공적으로 확인됨"),
            @ApiResponse(responseCode = "400", description = "잘못된 입력 값"),
            @ApiResponse(responseCode = "500", description = "서버 오류 발생")
    })
    @PostMapping("/save-category")
    public ApiResult<Boolean> saveExpenseCategory(@RequestParam String category, @RequestHeader("Authorization") String token) {
        if (expenseCategoryService.isLongCategory(category) == false){
            if (expenseCategoryService.isExistExpenseCategory(token, category) == false){
                return ApiResult.ok(expenseCategoryService.saveExpenseCategory(token, category));
            }
        }

        return ApiResult.ok("길이가 길거나, 중복입니다.", false);
    }

    @Operation(summary = "소비 카테고리 삭제", description = "카테고리 존재 확인 후 삭제합니다. 기타 카테고리는 삭제 되지 않습니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "성공적으로 확인됨"),
            @ApiResponse(responseCode = "400", description = "잘못된 입력 값"),
            @ApiResponse(responseCode = "500", description = "서버 오류 발생")
    })
    @DeleteMapping("/delete-category")
    public ApiResult<Boolean> deleteExpenseCategory(@RequestParam String category, @RequestHeader("Authorization") String token) {
        return ApiResult.ok(expenseCategoryService.deleteExpenseCategory(token, category));
    }
}
