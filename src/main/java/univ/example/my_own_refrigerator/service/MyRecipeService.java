package univ.example.my_own_refrigerator.service;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import univ.example.my_own_refrigerator.dto.MyRecipeDto;
import univ.example.my_own_refrigerator.dto.MyRecipeDetailsDto;
import univ.example.my_own_refrigerator.entity.MyRecipeEntity;
import univ.example.my_own_refrigerator.entity.UserEntity;
import univ.example.my_own_refrigerator.repository.MyRecipeRepository;
import univ.example.my_own_refrigerator.repository.UserRepository;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class MyRecipeService {

    private final MyRecipeRepository myRecipeRepository;
    private final UserRepository userRepository;

    /**
     * 나만의 레시피 생성
     */
    public MyRecipeEntity createMyRecipe(MyRecipeDto recipeDto) {
        String kakaoId = SecurityContextHolder.getContext().getAuthentication().getName();
        UserEntity user = userRepository.findByKakaoId(kakaoId)
                .orElseThrow(() -> new RuntimeException("User not found with kakaoId: " + kakaoId));

        MyRecipeEntity newRecipe = MyRecipeEntity.builder()
                .name(recipeDto.getName())
                .shortDescription(recipeDto.getShortDescription())
                .imageUrl(recipeDto.getImageUrl())
                .ingredients(recipeDto.getIngredients()) // 문자열 그대로 저장
                .cookingSteps(recipeDto.getCookingSteps())
                .authorId(user.getId())
                .postedDate(LocalDateTime.now())
                .build();

        return myRecipeRepository.save(newRecipe);
    }

    /**
     * 레시피 상세 조회
     */
    public MyRecipeDetailsDto getRecipeDetails(Long recipeId) {
        String kakaoId = SecurityContextHolder.getContext().getAuthentication().getName();
        UserEntity user = userRepository.findByKakaoId(kakaoId)
                .orElseThrow(() -> new RuntimeException("User not found with kakaoId: " + kakaoId));

        MyRecipeEntity recipe = myRecipeRepository.findById(recipeId)
                .orElseThrow(() -> new RuntimeException("Recipe not found with id: " + recipeId));

        if (!recipe.getAuthorId().equals(user.getId())) {
            throw new RuntimeException("Unauthorized access to recipe: " + recipeId);
        }

        return convertToDetailsDto(recipe);
    }

    /**
     * 레시피 수정
     */
    public MyRecipeDetailsDto updateMyRecipe(Long recipeId, MyRecipeDto recipeDto) {
        String kakaoId = SecurityContextHolder.getContext().getAuthentication().getName();
        UserEntity user = userRepository.findByKakaoId(kakaoId)
                .orElseThrow(() -> new RuntimeException("User not found with kakaoId: " + kakaoId));

        MyRecipeEntity existingRecipe = myRecipeRepository.findById(recipeId)
                .orElseThrow(() -> new RuntimeException("Recipe not found with id: " + recipeId));

        if (!existingRecipe.getAuthorId().equals(user.getId())) {
            throw new RuntimeException("Unauthorized to update this recipe.");
        }

        existingRecipe.setName(recipeDto.getName());
        existingRecipe.setShortDescription(recipeDto.getShortDescription());
        existingRecipe.setImageUrl(recipeDto.getImageUrl());
        existingRecipe.setIngredients(recipeDto.getIngredients()); // 문자열 그대로 저장
        existingRecipe.setCookingSteps(recipeDto.getCookingSteps());

        MyRecipeEntity updatedRecipe = myRecipeRepository.save(existingRecipe);
        return convertToDetailsDto(updatedRecipe);
    }

    /**
     * 레시피 공유
     */
    public void shareRecipe(Long recipeId) {
        String kakaoId = SecurityContextHolder.getContext().getAuthentication().getName();
        UserEntity user = userRepository.findByKakaoId(kakaoId)
                .orElseThrow(() -> new RuntimeException("User not found with kakaoId: " + kakaoId));

        MyRecipeEntity recipe = myRecipeRepository.findById(recipeId)
                .orElseThrow(() -> new RuntimeException("Recipe not found with id: " + recipeId));

        if (!recipe.getAuthorId().equals(user.getId())) {
            throw new RuntimeException("Unauthorized to share this recipe.");
        }

        recipe.setPostedDate(LocalDateTime.now());
        recipe.setPublic(true);
        myRecipeRepository.save(recipe);
    }

    /**
     * MyRecipeEntity → MyRecipeDetailsDto 변환
     */
    private MyRecipeDetailsDto convertToDetailsDto(MyRecipeEntity recipe) {
        return MyRecipeDetailsDto.builder()
                .id(recipe.getId())
                .name(recipe.getName())
                .shortDescription(recipe.getShortDescription())
                .imageUrl(recipe.getImageUrl())
                .ingredients(recipe.getIngredients()) // 문자열 그대로 반환
                .cookingSteps(recipe.getCookingSteps())
                .authorId(recipe.getAuthorId())
                .build();
    }
}
