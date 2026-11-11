package univ.example.my_own_refrigerator.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import univ.example.my_own_refrigerator.dto.NewIngredientDto; // ✅ NewIngredientDto 추가
import univ.example.my_own_refrigerator.entity.IngredientEntity;
import univ.example.my_own_refrigerator.service.IngredientService;

import java.util.List;

@RestController
@RequestMapping("/api/ingredients")
@RequiredArgsConstructor
@Tag(name = "재료 관리", description = "시스템 및 사용자 재료 등록/조회 API") // ✅ Swagger 태그 수정
public class IngredientController {

    private final IngredientService ingredientService;

    @GetMapping
    @Operation(summary = "모든 재료 목록 조회", description = "DB에 있는 모든 재료를 반환합니다.")
    public ResponseEntity<List<IngredientEntity>> getAllIngredients() {
        List<IngredientEntity> ingredients = ingredientService.getAllIngredients();
        return ResponseEntity.ok(ingredients);
    }

    @GetMapping("/search")
    @Operation(summary = "이름으로 재료 검색", description = "이름에 특정 문자열이 포함된 재료를 검색합니다.")
    public ResponseEntity<List<IngredientEntity>> searchIngredients(@RequestParam String name) {
        List<IngredientEntity> ingredients = ingredientService.searchIngredientsByName(name);
        return ResponseEntity.ok(ingredients);
    }

    /**
     * ✅ 새로운 재료를 등록하는 API
     * POST /api/ingredients/add
     * @param ingredientDto 등록할 재료 정보
     * @return 저장된 IngredientEntity
     */
    @PostMapping("/add")
    @Operation(summary = "새로운 재료 등록", description = "사용자가 직접 새로운 재료를 등록합니다. (JWT 토큰 필요)")
    public ResponseEntity<IngredientEntity> addIngredient(@RequestBody NewIngredientDto ingredientDto) {
        IngredientEntity savedIngredient = ingredientService.addIngredient(ingredientDto);
        return ResponseEntity.ok(savedIngredient);
    }
}