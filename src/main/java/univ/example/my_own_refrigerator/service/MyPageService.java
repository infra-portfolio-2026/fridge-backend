package univ.example.my_own_refrigerator.service;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import univ.example.my_own_refrigerator.dto.MyPageResponseDto;
import univ.example.my_own_refrigerator.dto.MyRecipeListItemDto; // ⏪ 새로운 DTO import
import univ.example.my_own_refrigerator.entity.UserEntity;
import univ.example.my_own_refrigerator.entity.MyRecipeEntity;
import univ.example.my_own_refrigerator.repository.UserRepository;
import univ.example.my_own_refrigerator.repository.MyRecipeRepository;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MyPageService {

    private final UserRepository userRepository;
    private final MyRecipeRepository myRecipeRepository;

    public MyPageResponseDto getMyPageData() {
        String kakaoId = SecurityContextHolder.getContext().getAuthentication().getName();

        UserEntity user = userRepository.findByKakaoId(kakaoId)
                .orElseThrow(() -> new RuntimeException("User not found with kakaoId: " + kakaoId));

        List<MyRecipeEntity> myRecipes = myRecipeRepository.findByAuthorId(user.getId());

        // ⏪ 레시피 엔티티에서 필요한 필드만 추출하여 DTO 리스트로 변환
        List<MyRecipeListItemDto> myRecipeDtos = myRecipes.stream()
                .map(recipe -> {
                    MyRecipeListItemDto dto = new MyRecipeListItemDto();
                    dto.setId(recipe.getId());
                    dto.setName(recipe.getName());
                    dto.setShortDescription(recipe.getShortDescription());
                    return dto;
                })
                .collect(Collectors.toList());

        return MyPageResponseDto.builder()
                .nickname(user.getNickname())
                .myRecipes(myRecipeDtos) // ⏪ 변환된 DTO 리스트를 사용
                .build();
    }
}