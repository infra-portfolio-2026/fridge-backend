package univ.example.my_own_refrigerator.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class NewIngredientDto {
    private String name;
    private Integer expDate;
    private String type;
}