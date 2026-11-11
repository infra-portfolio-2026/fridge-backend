package univ.example.my_own_refrigerator.dto;

import lombok.Data;

@Data
public class IngredientDto {
    private Long id; // 재료 ID 필드 추가
    private Integer quantity; // 개수 필드
}
