package univ.example.my_own_refrigerator.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import univ.example.my_own_refrigerator.dto.CommunityRecipeDto;
import univ.example.my_own_refrigerator.dto.CommunityRecipeDetailsDto;
import univ.example.my_own_refrigerator.service.CommunityService;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/community")
@Tag(name = "커뮤니티", description = "공유 레시피 목록 조회 API")
public class CommunityController {

    private final CommunityService communityService;

    @GetMapping("/recipes")
    @Operation(summary = "커뮤니티 레시피 목록 조회", description = "모든 유저가 공유한 레시피 목록을 조회합니다.")
    public ResponseEntity<List<CommunityRecipeDto>> getCommunityRecipes() {
        List<CommunityRecipeDto> recipes = communityService.getCommunityRecipes();
        return ResponseEntity.ok(recipes);
    }

    @GetMapping("/recipes/{id}")
    @Operation(summary = "커뮤니티 레시피 상세 조회", description = "공유된 특정 레시피의 상세 정보를 조회합니다.")
    public ResponseEntity<CommunityRecipeDetailsDto> getRecipeDetails(@PathVariable Long id) {
        CommunityRecipeDetailsDto details = communityService.getRecipeDetails(id);
        return ResponseEntity.ok(details);
    }

    /**
     * ✅ 레시피 '좋아요' 엔드포인트 추가
     * @param id 좋아요를 누를 레시피의 ID
     * @return 200 OK
     */
    @PostMapping("/recipes/{id}/like")
    @Operation(summary = "레시피 '좋아요' 추가", description = "특정 레시피의 좋아요 수를 1 증가시킵니다. (누구나 가능)")
    public ResponseEntity<Void> likeRecipe(@PathVariable Long id) {
        communityService.incrementLikeCount(id);
        return ResponseEntity.ok().build();
    }
}