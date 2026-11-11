package univ.example.my_own_refrigerator.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import univ.example.my_own_refrigerator.entity.RecipeEntity;

public interface RecipeRepository extends JpaRepository<RecipeEntity, Long> {
}