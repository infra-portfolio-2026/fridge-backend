package univ.example.my_own_refrigerator.dto;

import lombok.Builder;
import lombok.Data;
import java.util.List;

@Data
@Builder
public class MyPageResponseDto {
    private String nickname;
    private List<MyRecipeListItemDto> myRecipes; // ⏪ MyRecipeListItemDto 리스트로 변경
}