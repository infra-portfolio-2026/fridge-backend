package univ.example.my_own_refrigerator.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import univ.example.my_own_refrigerator.dto.IngredientDto;
import univ.example.my_own_refrigerator.entity.RefrigeratorEntity;
import univ.example.my_own_refrigerator.service.RefrigeratorService;
import univ.example.my_own_refrigerator.config.auth.PrincipalDetails;

import java.io.IOException;

@Tag(name = "냉장고 API", description = "유저의 냉장고 관리 API")
@RestController
@RequestMapping("/api/refrigerator")
@RequiredArgsConstructor
public class RefrigeratorController {

    private final RefrigeratorService refrigeratorService;

    @Operation(summary = "냉장고에 재료 추가/업데이트", description = "로그인된 유저의 냉장고에 재료를 등록하거나 이미 있는 재료의 개수를 업데이트합니다.")
    @PostMapping("/add-ingredient")
    public ResponseEntity<RefrigeratorEntity> addIngredient(
            @AuthenticationPrincipal PrincipalDetails principalDetails,
            @RequestBody IngredientDto ingredientDto) {

        // PrincipalDetails 객체에서 유저의 고유한 카카오 ID 추출
        String kakaoId = principalDetails.getUser().getKakaoId();

        try {
            RefrigeratorEntity updatedRefrigerator = refrigeratorService.addOrUpdateIngredient(kakaoId, ingredientDto);
            return ResponseEntity.ok(updatedRefrigerator);
        } catch (IOException e) { // IOException을 catch하도록 수정
            return ResponseEntity.internalServerError().build();
        }
    }
}
