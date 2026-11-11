package univ.example.my_own_refrigerator.service;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import univ.example.my_own_refrigerator.dto.NewIngredientDto;
import univ.example.my_own_refrigerator.entity.IngredientEntity;
import univ.example.my_own_refrigerator.entity.UserEntity;
import univ.example.my_own_refrigerator.repository.IngredientRepository;
import univ.example.my_own_refrigerator.repository.UserRepository;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class IngredientService {

    private final IngredientRepository ingredientRepository;
    private final UserRepository userRepository;

    /**
     * ✅ 현재 사용자에게 허용된 모든 재료 목록을 조회합니다.
     * 시스템 재료 + 현재 사용자가 등록한 재료
     * @return 필터링된 IngredientEntity 리스트
     */
    public List<IngredientEntity> getAllIngredients() {
        // JWT 토큰에서 현재 사용자의 ID를 가져옵니다.
        String kakaoId = SecurityContextHolder.getContext().getAuthentication().getName();
        UserEntity user = userRepository.findByKakaoId(kakaoId)
                .orElseThrow(() -> new RuntimeException("User not found with kakaoId: " + kakaoId));

        return ingredientRepository.findBySourceOrAuthorId(IngredientEntity.Source.system, user.getId());
    }

    /**
     * ✅ 현재 사용자에게 허용된 재료 중 이름으로 검색합니다.
     * @param name 검색할 재료 이름
     * @return 필터링된 검색 결과 리스트
     */
    public List<IngredientEntity> searchIngredientsByName(String name) {
        String kakaoId = SecurityContextHolder.getContext().getAuthentication().getName();
        UserEntity user = userRepository.findByKakaoId(kakaoId)
                .orElseThrow(() -> new RuntimeException("User not found with kakaoId: " + kakaoId));

        return ingredientRepository.findByNameContainingAndSourceOrAuthorId(name, IngredientEntity.Source.system, user.getId());
    }

    /**
     * 새로운 재료를 등록합니다.
     * @param ingredientDto 등록할 재료 정보
     * @return 저장된 IngredientEntity
     */
    public IngredientEntity addIngredient(NewIngredientDto ingredientDto) {
        String kakaoId = SecurityContextHolder.getContext().getAuthentication().getName();
        UserEntity user = userRepository.findByKakaoId(kakaoId)
                .orElseThrow(() -> new RuntimeException("User not found with kakaoId: " + kakaoId));

        IngredientEntity newIngredient = IngredientEntity.builder()
                .name(ingredientDto.getName())
                .expDate(ingredientDto.getExpDate())
                .type(ingredientDto.getType())
                .source(IngredientEntity.Source.user)
                .authorId(user.getId())
                .build();

        return ingredientRepository.save(newIngredient);
    }
}