package univ.example.my_own_refrigerator.dto;

import lombok.Getter;
import lombok.Setter;
import java.util.List;
import java.util.Map;

@Getter
@Setter
public class MyRecipeDto {
    private String name;
    private String shortDescription;
    private String imageUrl;
    private String ingredients; // 재료 ID와 수량/무게
    private String cookingSteps;
}