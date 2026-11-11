package univ.example.my_own_refrigerator.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import univ.example.my_own_refrigerator.entity.RefrigeratorEntity;
import univ.example.my_own_refrigerator.entity.UserEntity;
import java.util.Optional;

public interface RefrigeratorRepository extends JpaRepository<RefrigeratorEntity, Long> {
    Optional<RefrigeratorEntity> findByUser(UserEntity user);
}