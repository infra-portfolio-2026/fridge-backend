package univ.example.my_own_refrigerator.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import univ.example.my_own_refrigerator.dto.CommunityRecipeDto;
import univ.example.my_own_refrigerator.dto.CommunityRecipeDetailsDto;
import univ.example.my_own_refrigerator.entity.MyRecipeEntity;
import univ.example.my_own_refrigerator.entity.UserEntity;
import univ.example.my_own_refrigerator.repository.MyRecipeRepository;
import univ.example.my_own_refrigerator.repository.UserRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CommunityService {

    private final MyRecipeRepository myRecipeRepository;
    private final UserRepository userRepository;

    /**
     * ✅ 공개된 레시피 목록 가져오기
     */
    public List<CommunityRecipeDto> getCommunityRecipes() {
        List<MyRecipeEntity> publicRecipes = myRecipeRepository.findByIsPublicTrue();

        List<Long> authorIds = publicRecipes.stream()
                .map(MyRecipeEntity::getAuthorId)
                .distinct()
                .collect(Collectors.toList());

        List<UserEntity> users = userRepository.findAllById(authorIds);
        var userNicknameMap = users.stream()
                .collect(Collectors.toMap(UserEntity::getId, UserEntity::getNickname));

        return publicRecipes.stream()
                .map(recipe -> CommunityRecipeDto.builder()
                        .id(recipe.getId())
                        .recipeName(recipe.getName())
                        .shortDescription(recipe.getShortDescription())
                        .userNickname(userNicknameMap.getOrDefault(recipe.getAuthorId(), "알 수 없음"))
                        .postedDate(recipe.getPostedDate())
                        .likeCount(recipe.getLikeCount())
                        .build())
                .collect(Collectors.toList());
    }

    /**
     * ✅ 특정 레시피 상세 조회
     */
    public CommunityRecipeDetailsDto getRecipeDetails(Long recipeId) {
        MyRecipeEntity recipe = myRecipeRepository.findById(recipeId)
                .orElseThrow(() -> new RuntimeException("Recipe not found with id: " + recipeId));

        if (!recipe.isPublic()) {
            throw new RuntimeException("This recipe is not shared with the community.");
        }

        UserEntity author = userRepository.findById(recipe.getAuthorId())
                .orElse(null);

        return CommunityRecipeDetailsDto.builder()
                .id(recipe.getId())
                .name(recipe.getName())
                .shortDescription(recipe.getShortDescription())
                .userNickname(author != null ? author.getNickname() : "알 수 없음")
                .postedDate(recipe.getPostedDate())
                .likeCount(recipe.getLikeCount())
                .cookingSteps(recipe.getCookingSteps())
                .ingredients(recipe.getIngredients()) // ✅ 문자열 그대로 반환
                .build();
    }

    /**
     * ✅ 특정 레시피의 '좋아요' 수를 1 증가시키는 메서드
     */
    @Transactional
    public void incrementLikeCount(Long recipeId) {
        MyRecipeEntity recipe = myRecipeRepository.findById(recipeId)
                .orElseThrow(() -> new RuntimeException("Recipe not found with id: " + recipeId));

        if (!recipe.isPublic()) {
            throw new RuntimeException("This recipe is not shared with the community.");
        }

        recipe.setLikeCount(recipe.getLikeCount() + 1);
        myRecipeRepository.save(recipe);
    }
}
