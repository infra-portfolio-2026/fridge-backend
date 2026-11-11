package univ.example.my_own_refrigerator.entity;

import com.fasterxml.jackson.annotation.JsonIgnore; // ⭐ 이 import를 추가해야 합니다.
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "recipe_steps")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RecipeStepEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "recipe_id", nullable = false)
    @JsonIgnore // ⭐ 순환 참조를 끊기 위해 이 어노테이션을 추가합니다.
    private RecipeEntity recipe;

    @Column(length = 2000)
    private String description; // MANUAL01~20

    @Column(length = 255)
    private String image; // MANUAL_IMG01~20
}