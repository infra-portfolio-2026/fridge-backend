package univ.example.my_own_refrigerator.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import univ.example.my_own_refrigerator.entity.MyRecipeEntity;
import java.util.List;

public interface MyRecipeRepository extends JpaRepository<MyRecipeEntity, Long> {

    List<MyRecipeEntity> findByAuthorId(Long authorId);

    // ⏪ isPublic이 true인 레시피만 조회하는 메서드 추가
    List<MyRecipeEntity> findByIsPublicTrue();
}