package univ.example.my_own_refrigerator.dto;

import lombok.Data;

@Data
public class MyRecipeListItemDto {
    private Long id;
    private String name;
    private String shortDescription;
}