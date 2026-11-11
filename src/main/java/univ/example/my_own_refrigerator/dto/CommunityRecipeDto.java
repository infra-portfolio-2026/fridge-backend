package univ.example.my_own_refrigerator.dto;

import lombok.Builder;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@Builder
public class CommunityRecipeDto {
    private Long id;
    private String recipeName;
    private String shortDescription;
    private String userNickname;
    private LocalDateTime postedDate;
    private int likeCount;
}