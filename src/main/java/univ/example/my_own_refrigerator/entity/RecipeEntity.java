package univ.example.my_own_refrigerator.entity;

import jakarta.persistence.*;
import lombok.*;
import java.util.List;

@Entity
@Table(name = "recipes")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RecipeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 200)
    private String name; // RCP_NM

    @Column(length = 50)
    private String category; // 카테고리, 나중에 CSV RCP_PAT2 사용 가능

    @Column(length = 2000)
    private String way; // RCP_WAY2

    @Column(length = 255)
    private String hashTag; // HASH_TAG

    @Column(length = 255)
    private String mainImage; // ATT_FILE_NO_MAIN

    @Column(length = 255)
    private String subImage; // ATT_FILE_NO_MK

    @Column(length = 255)
    private String tip; // RCP_NA_TIP

    @Column(length = 50)
    private String energy; // INFO_ENG

    @Column(length = 50)
    private String carbohydrate; // INFO_CAR

    @Column(length = 50)
    private String protein; // INFO_PRO

    @Column(length = 50)
    private String fat; // INFO_FAT

    @Column(length = 50)
    private String sodium; // INFO_NA

    @OneToMany(mappedBy = "recipe", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<RecipeIngredientEntity> ingredients;

    @OneToMany(mappedBy = "recipe", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<RecipeStepEntity> steps;
}