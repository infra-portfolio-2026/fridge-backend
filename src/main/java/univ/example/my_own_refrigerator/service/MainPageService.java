package univ.example.my_own_refrigerator.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import univ.example.my_own_refrigerator.entity.IngredientEntity;
import univ.example.my_own_refrigerator.entity.RefrigeratorEntity;
import univ.example.my_own_refrigerator.entity.UserEntity;
import univ.example.my_own_refrigerator.repository.IngredientRepository;
import univ.example.my_own_refrigerator.repository.RefrigeratorRepository;
import univ.example.my_own_refrigerator.repository.UserRepository;

import java.io.IOException;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MainPageService {

    private final UserRepository userRepository;
    private final RefrigeratorRepository refrigeratorRepository;
    private final IngredientRepository ingredientRepository;
    private final ObjectMapper objectMapper;

    // 카카오 액세스 토큰으로 유저 정보를 조회하는 AuthService는 더 이상 필요하지 않습니다.
    // private final AuthService authService;

    public Map<String, Object> getMainPageData(String kakaoId) throws IOException { // 파라미터를 kakaoId로 변경
        // JsonNode userInfo = authService.getKakaoUserInfo(kakaoAccessToken);
        // String kakaoId = userInfo.get("id").asText();
        // String nickname = userInfo.get("properties").get("nickname").asText();

        UserEntity user = userRepository.findByKakaoId(kakaoId)
                .orElseThrow(() -> new RuntimeException("User not found with kakaoId: " + kakaoId));

        // 냉장고가 존재하지 않아도 오류를 던지지 않고 Optional을 반환
        return refrigeratorRepository.findByUser(user)
                .map(refrigerator -> {
                    // 냉장고가 존재하는 경우, 기존 로직 실행
                    try {
                        return createMainPageData(user, refrigerator);
                    } catch (IOException e) {
                        throw new RuntimeException("Error processing refrigerator data", e);
                    }
                })
                .orElseGet(() -> {
                    // 냉장고가 존재하지 않는 경우, 빈 데이터 반환
                    Map<String, Object> result = new HashMap<>();
                    result.put("nickname", user.getNickname()); // user 객체에서 닉네임 가져오기
                    result.put("foodTypePercentages", new HashMap<>());
                    result.put("expiringFoods", new ArrayList<>());
                    result.put("allFoods", new ArrayList<>());
                    return result;
                });
    }

    private Map<String, Object> createMainPageData(UserEntity user, RefrigeratorEntity refrigerator) throws IOException {
        Map<String, Object> result = new HashMap<>();
        result.put("nickname", user.getNickname());

        // 냉장고 안의 모든 음식 데이터를 JSON 문자열로 파싱
        List<Map<String, Object>> foodsList;
        if (refrigerator.getFoodsJson() != null && !refrigerator.getFoodsJson().isEmpty()) {
            foodsList = objectMapper.readValue(refrigerator.getFoodsJson(), new TypeReference<List<Map<String, Object>>>() {});
        } else {
            foodsList = new ArrayList<>();
        }

        // 재료 정보를 미리 조회하여 Map에 저장
        List<Long> ingredientIds = foodsList.stream()
                .map(food -> ((Number) food.get("id")).longValue())
                .collect(Collectors.toList());
        List<IngredientEntity> ingredients = ingredientRepository.findAllById(ingredientIds);
        Map<Long, IngredientEntity> ingredientMap = ingredients.stream()
                .collect(Collectors.toMap(IngredientEntity::getId, ingredient -> ingredient));

        int total = foodsList.size();

        // 1. 식품 유형 비율 계산
        Map<String, Long> typeCounts = new HashMap<>();
        for (Map<String, Object> food : foodsList) {
            Long ingredientId = ((Number) food.get("id")).longValue();
            IngredientEntity ingredient = ingredientMap.get(ingredientId);
            if (ingredient != null) {
                String type = ingredient.getType();
                typeCounts.put(type, typeCounts.getOrDefault(type, 0L) + 1);
            }
        }
        Map<String, Integer> foodTypePercentages = typeCounts.entrySet().stream()
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        entry -> (int) Math.round((entry.getValue() / (double) total) * 100)
                ));
        result.put("foodTypePercentages", foodTypePercentages);

        // 2. 유통기한 임박 식품 목록 구성
        List<Map<String, Object>> expiringFoods = new ArrayList<>();
        LocalDate today = LocalDate.now();

        for (Map<String, Object> food : foodsList) {
            Long ingredientId = ((Number) food.get("id")).longValue();
            IngredientEntity ingredient = ingredientMap.get(ingredientId);

            if (ingredient != null) {
                Integer expDateDays = ingredient.getExpDate(); // IngredientEntity의 expDate (일)
                LocalDate expDate = today.plusDays(expDateDays);
                long daysLeft = ChronoUnit.DAYS.between(today, expDate);

                if (daysLeft >= 0 && daysLeft <= 3) {
                    Map<String, Object> expMap = new HashMap<>();
                    expMap.put("name", ingredient.getName());
                    expMap.put("quantity", food.get("quantity"));
                    expMap.put("daysLeft", daysLeft);
                    expiringFoods.add(expMap);
                }
            }
        }
        result.put("expiringFoods", expiringFoods);

        // 3. 전체 식품 목록
        List<Map<String, Object>> allFoods = foodsList.stream()
                .map(food -> {
                    Long ingredientId = ((Number) food.get("id")).longValue();
                    IngredientEntity ingredient = ingredientMap.get(ingredientId);
                    if (ingredient != null) {
                        Map<String, Object> foodDetails = new HashMap<>();
                        foodDetails.put("id", ingredient.getId());
                        foodDetails.put("name", ingredient.getName());
                        foodDetails.put("quantity", food.get("quantity"));
                        foodDetails.put("expDate", LocalDate.now().plusDays(ingredient.getExpDate()).toString());
                        foodDetails.put("type", ingredient.getType());
                        return foodDetails;
                    }
                    return null;
                })
                .filter(java.util.Objects::nonNull)
                .collect(Collectors.toList());

        result.put("allFoods", allFoods);

        return result;
    }
}
