package univ.example.my_own_refrigerator.dto;

import lombok.Builder;
import lombok.Data;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
public class CommunityRecipeDetailsDto {
    private Long id;
    private String name;
    private String shortDescription;
    private String userNickname;
    private LocalDateTime postedDate;
    private int likeCount;
    private String cookingSteps;
    private String ingredients; // 재료 이름과 수량
}