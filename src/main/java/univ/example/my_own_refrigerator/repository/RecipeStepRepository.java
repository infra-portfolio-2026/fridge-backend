package univ.example.my_own_refrigerator.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import univ.example.my_own_refrigerator.entity.RecipeStepEntity;
import java.util.List;

public interface RecipeStepRepository extends JpaRepository<RecipeStepEntity, Long> {
    // recipeId 기준으로 step 순서대로 조회
    List<RecipeStepEntity> findByRecipeIdOrderByIdAsc(Long recipeId);
}
