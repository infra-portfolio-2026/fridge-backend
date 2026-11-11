package univ.example.my_own_refrigerator.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;
import univ.example.my_own_refrigerator.service.RecipeService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/recipes")
@Tag(name = "레시피 추천", description = "냉장고 재료 기반 레시피 추천 API")
public class RecipeController {

    private final RecipeService recipeService;

    @GetMapping("/recommend")
    @Operation(summary = "냉장고 재료로 레시피 추천받기", description = "데이터베이스에 저장된 사용자의 냉장고 재료를 기반으로 레시피를 추천합니다. JWT 토큰이 필요합니다.")
    public Mono<String> getRecommendedRecipes() {
        return recipeService.getRecommendedRecipesFromDb();
    }

    @GetMapping("/details/{recipeId}")
    @Operation(summary = "레시피 상세 정보 조회", description = "추천된 레시피의 고유 ID로 상세 정보를 조회합니다.")
    public Mono<String> getRecipeDetails(@PathVariable String recipeId) {
        return recipeService.getRecipeDetails(recipeId);
    }

    @GetMapping("/all")
    @Operation(summary = "모든 레시피 목록 조회", description = "데이터베이스에 저장된 모든 레시피 목록을 반환합니다.")
    public Mono<String> getAllRecipes() {
        return recipeService.getAllRecipes();
    }

    @GetMapping("/details-db/{recipeId}")
    @Operation(summary = "DB에서 직접 레시피 상세 조회", description = "Python 서버를 거치지 않고 DB에서 직접 조리 단계를 조회합니다.")
    public Mono<String> getRecipeDetailsFromDb(@PathVariable Long recipeId) {
        return recipeService.getRecipeDetailsFromDb(recipeId);
    }
}