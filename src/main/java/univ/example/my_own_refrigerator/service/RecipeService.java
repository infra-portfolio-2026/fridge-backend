package univ.example.my_own_refrigerator.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import univ.example.my_own_refrigerator.entity.RecipeEntity;
import univ.example.my_own_refrigerator.entity.RecipeStepEntity;
import univ.example.my_own_refrigerator.entity.RefrigeratorEntity;
import univ.example.my_own_refrigerator.entity.UserEntity;
import univ.example.my_own_refrigerator.repository.RecipeRepository;
import univ.example.my_own_refrigerator.repository.RecipeStepRepository;
import univ.example.my_own_refrigerator.repository.RefrigeratorRepository;
import univ.example.my_own_refrigerator.repository.UserRepository;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RecipeService {

    private final RefrigeratorRepository refrigeratorRepository;
    private final UserRepository userRepository;
    private final WebClient.Builder webClientBuilder;
    private final RecipeStepRepository recipeStepRepository;
    private final ObjectMapper objectMapper;
    private final RecipeRepository recipeRepository;
    private final String PYTHON_API_URL = "http://localhost:5000";

    // -------------------------------
    // 모든 레시피 조회 (DB)
    // -------------------------------
    public Mono<String> getAllRecipes() {
        return Mono.fromCallable(() -> {
            List<RecipeEntity> recipes = recipeRepository.findAll();
            try {
                return objectMapper.writeValueAsString(recipes);
            } catch (JsonProcessingException e) {
                e.printStackTrace();
                return "{\"error\":\"모든 레시피를 가져오는 중 오류가 발생했습니다.\"}";
            }
        });
    }

    // -------------------------------
    // 레시피 상세 조회 (DB 직접 조회, 단계 + 영양성분)
    // -------------------------------
    public Mono<String> getRecipeDetailsFromDb(Long recipeId) {
        return Mono.fromCallable(() -> {
            RecipeEntity recipe = recipeRepository.findById(recipeId)
                    .orElseThrow(() -> new RuntimeException("해당 레시피를 찾을 수 없습니다."));

            // RecipeStepEntity 조회, id 기준 오름차순
            List<RecipeStepEntity> steps = recipeStepRepository.findByRecipeIdOrderByIdAsc(recipeId);

            Map<String, Object> response = new HashMap<>();
            response.put("recipeId", recipe.getId());
            response.put("recipeName", recipe.getName());
            response.put("ingredients", recipe.getIngredients()); // 필요시 DTO로 변환 가능
            response.put("steps", steps);

            // 영양성분 추가
            Map<String, String> nutrition = new HashMap<>();
            nutrition.put("energy", recipe.getEnergy());
            nutrition.put("carbohydrate", recipe.getCarbohydrate());
            nutrition.put("protein", recipe.getProtein());
            nutrition.put("fat", recipe.getFat());
            nutrition.put("sodium", recipe.getSodium());
            response.put("nutrition", nutrition);

            return objectMapper.writeValueAsString(response);
        });
    }

    // -------------------------------
    // 추천 레시피 조회 (사용자 냉장고 기반, Flask API)
    // -------------------------------
    public Mono<String> getRecommendedRecipesFromDb() {
        String kakaoId = SecurityContextHolder.getContext().getAuthentication().getName();

        return Mono.fromSupplier(() -> userRepository.findByKakaoId(kakaoId))
                .flatMap(userOpt -> {
                    if (userOpt.isEmpty()) {
                        return Mono.just("{\"error\":\"사용자를 찾을 수 없습니다.\"}");
                    }

                    UserEntity user = userOpt.get();
                    Optional<RefrigeratorEntity> refrigeratorOpt = refrigeratorRepository.findByUser(user);

                    if (refrigeratorOpt.isEmpty()) {
                        return Mono.just("{\"error\":\"사용자에게 냉장고가 없습니다.\"}");
                    }

                    RefrigeratorEntity refrigerator = refrigeratorOpt.get();
                    List<String> ingredients;

                    try {
                        List<Map<String, Object>> foodsList = objectMapper.readValue(
                                refrigerator.getFoodsJson(),
                                new TypeReference<List<Map<String, Object>>>() {}
                        );
                        ingredients = foodsList.stream()
                                .map(food -> (String) food.get("name"))
                                .collect(Collectors.toList());
                    } catch (JsonProcessingException e) {
                        return Mono.error(new RuntimeException("냉장고 데이터 파싱 오류", e));
                    }
                    if (ingredients.isEmpty()) {
                        return Mono.just("{\"error\":\"냉장고에 재료가 없습니다.\"}");
                    }

                    String ingredientsString = String.join(", ", ingredients);
                    List<Map<String, String>> requestBody = Collections.singletonList(
                            Collections.singletonMap("ingredients", ingredientsString)
                    );

                    WebClient webClient = webClientBuilder.baseUrl(PYTHON_API_URL).build();
                    return webClient.post()
                            .uri("/recommend")
                            .bodyValue(requestBody)
                            .retrieve()
                            .bodyToMono(String.class)
                            .onErrorResume(e -> {
                                System.err.println("Python API 호출 실패: " + e.getMessage());
                                return Mono.just("{\"error\":\"레시피 추천 서버에 문제가 발생했습니다.\"}");
                            });
                });
    }

    // -------------------------------
    // 레시피 상세 조회 (Flask API)
    // -------------------------------
    public Mono<String> getRecipeDetails(String recipeId) {
        WebClient webClient = webClientBuilder.baseUrl(PYTHON_API_URL).build();
        return webClient.get()
                .uri("/details/{recipeId}", recipeId)
                .retrieve()
                .bodyToMono(String.class)
                .onErrorResume(e -> {
                    System.err.println("Python API 호출 실패: " + e.getMessage());
                    return Mono.just("{\"error\":\"레시피 상세 정보 서버에 문제가 발생했습니다.\"}");
                });
    }
}
