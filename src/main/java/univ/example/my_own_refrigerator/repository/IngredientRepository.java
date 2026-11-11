package univ.example.my_own_refrigerator.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import univ.example.my_own_refrigerator.entity.IngredientEntity;
import java.util.List;
import java.util.Optional;

public interface IngredientRepository extends JpaRepository<IngredientEntity, Long> {

    // ✅ source가 'system'이거나 authorId가 현재 유저의 ID인 재료를 모두 찾습니다.
    List<IngredientEntity> findBySourceOrAuthorId(IngredientEntity.Source source, Long authorId);

    // ✅ 이름에 특정 문자열이 포함되고, 위와 동일한 조건으로 필터링하여 찾습니다.
    List<IngredientEntity> findByNameContainingAndSourceOrAuthorId(String name, IngredientEntity.Source source, Long authorId);
}