package univ.example.my_own_refrigerator.dto;

import lombok.Data;

@Data
public class RecipeIngredientDto {
    private String name;
    private Integer quantity;
}