package univ.example.my_own_refrigerator.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import univ.example.my_own_refrigerator.dto.MyRecipeDto;
import univ.example.my_own_refrigerator.dto.MyRecipeDetailsDto;
import univ.example.my_own_refrigerator.entity.MyRecipeEntity;
import univ.example.my_own_refrigerator.service.MyRecipeService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/my-recipes")
@Tag(name = "나만의 레시피", description = "사용자 레시피 등록 및 관리 API")
public class MyRecipeController {

    private final MyRecipeService myRecipeService;

    @PostMapping
    @Operation(summary = "나만의 레시피 등록", description = "사용자가 직접 새로운 레시피를 등록합니다.")
    public ResponseEntity<MyRecipeEntity> createMyRecipe(@RequestBody MyRecipeDto recipeDto) {
        MyRecipeEntity newRecipe = myRecipeService.createMyRecipe(recipeDto);
        return ResponseEntity.ok(newRecipe);
    }

    @GetMapping("/{id}")
    @Operation(summary = "나만의 레시피 상세 조회", description = "특정 레시피의 모든 정보를 조회합니다.")
    public ResponseEntity<MyRecipeDetailsDto> getRecipeDetails(@PathVariable Long id) {
        MyRecipeDetailsDto recipe = myRecipeService.getRecipeDetails(id);
        return ResponseEntity.ok(recipe);
    }

    @PutMapping("/{id}")
    @Operation(summary = "나만의 레시피 수정", description = "기존에 등록된 레시피를 수정합니다.")
    public ResponseEntity<MyRecipeDetailsDto> updateMyRecipe(@PathVariable Long id, @RequestBody MyRecipeDto recipeDto) {
        MyRecipeDetailsDto updatedRecipe = myRecipeService.updateMyRecipe(id, recipeDto);
        return ResponseEntity.ok(updatedRecipe);
    }

    // ⏪ 레시피 공유 API 추가
    @PostMapping("/{id}/share")
    @Operation(summary = "나만의 레시피 커뮤니티 공유", description = "특정 레시피를 커뮤니티에 공개 상태로 변경합니다.")
    public ResponseEntity<Void> shareRecipe(@PathVariable Long id) {
        myRecipeService.shareRecipe(id);
        return ResponseEntity.ok().build();
    }
}