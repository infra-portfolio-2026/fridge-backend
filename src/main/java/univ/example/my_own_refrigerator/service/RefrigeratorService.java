package univ.example.my_own_refrigerator.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import univ.example.my_own_refrigerator.dto.IngredientDto;
import univ.example.my_own_refrigerator.entity.IngredientEntity;
import univ.example.my_own_refrigerator.entity.RefrigeratorEntity;
import univ.example.my_own_refrigerator.entity.UserEntity;
import univ.example.my_own_refrigerator.repository.IngredientRepository;
import univ.example.my_own_refrigerator.repository.RefrigeratorRepository;
import univ.example.my_own_refrigerator.repository.UserRepository;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class RefrigeratorService {

    private final RefrigeratorRepository refrigeratorRepository;
    private final UserRepository userRepository;
    private final IngredientRepository ingredientRepository;
    private final ObjectMapper objectMapper = new ObjectMapper();
    // private final AuthService authService; // AuthService 의존성 제거

    /**
     * 유저의 냉장고에 재료를 추가하거나 개수를 업데이트합니다.
     *
     * @param kakaoId        냉장고 소유자의 카카오 ID (JWT에서 추출됨)
     * @param ingredientDto  추가할 재료의 DTO (재료 ID와 개수 포함)
     * @return 재료가 업데이트된 냉장고 엔티티
     * @throws JsonProcessingException JSON 처리 오류 발생 시
     * @throws IOException 카카오 사용자 정보 조회 오류 시
     */
    @Transactional
    public RefrigeratorEntity addOrUpdateIngredient(String kakaoId, IngredientDto ingredientDto) throws IOException {
        // 1. 카카오 ID로 유저 엔티티를 찾습니다.
        UserEntity user = userRepository.findByKakaoId(kakaoId)
                .orElseThrow(() -> new IllegalArgumentException("User not found with kakaoId: " + kakaoId));

        // 2. 냉장고 엔티티를 찾거나 새로 생성합니다.
        RefrigeratorEntity refrigerator = Optional.ofNullable(user.getRefrigerator())
                .orElseGet(() -> {
                    RefrigeratorEntity newRefrigerator = RefrigeratorEntity.builder().user(user).foodsJson("[]").build();
                    user.setRefrigerator(newRefrigerator);
                    return newRefrigerator;
                });

        // 3. 재료 ID로 IngredientEntity를 조회합니다.
        IngredientEntity ingredient = ingredientRepository.findById(ingredientDto.getId())
                .orElseThrow(() -> new IllegalArgumentException("Ingredient not found with id: " + ingredientDto.getId()));

        // 4. 냉장고의 JSON 데이터를 파싱하여 리스트로 변환합니다.
        List<Map<String, Object>> foodsList = new ArrayList<>();
        if (refrigerator.getFoodsJson() != null && !refrigerator.getFoodsJson().isEmpty()) {
            foodsList = objectMapper.readValue(refrigerator.getFoodsJson(), new TypeReference<List<Map<String, Object>>>() {});
        }

        // 5. 기존 재료 목록에서 동일한 ID의 재료가 있는지 확인하고, 있으면 개수를 업데이트합니다.
        boolean ingredientExists = false;
        for (Map<String, Object> food : foodsList) {
            if (food.get("id").equals(ingredientDto.getId().intValue())) {
                Integer existingQuantity = (Integer) food.getOrDefault("quantity", 0);
                food.put("quantity", existingQuantity + ingredientDto.getQuantity());
                ingredientExists = true;
                break;
            }
        }

        // 6. 재료가 없으면 새로 추가합니다.
        if (!ingredientExists) {
            foodsList.add(Map.of(
                    "id", ingredient.getId(),
                    "name", ingredient.getName(),
                    "quantity", ingredientDto.getQuantity(),
                    "expDate", ingredient.getExpDate(),
                    "type", ingredient.getType()
            ));
        }

        // 7. 업데이트된 리스트를 다시 JSON 문자열로 변환하여 저장합니다.
        refrigerator.setFoodsJson(objectMapper.writeValueAsString(foodsList));

        return refrigeratorRepository.save(refrigerator);
    }
}
