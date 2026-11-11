//package univ.example.my_own_refrigerator.config;
//
//import com.opencsv.CSVReader;
//import com.opencsv.exceptions.CsvValidationException;
//import org.springframework.boot.CommandLineRunner;
//import org.springframework.core.io.ClassPathResource;
//import org.springframework.stereotype.Component;
//import org.springframework.transaction.annotation.Transactional;
//import univ.example.my_own_refrigerator.entity.RecipeEntity;
//import univ.example.my_own_refrigerator.entity.RecipeIngredientEntity;
//import univ.example.my_own_refrigerator.entity.RecipeStepEntity;
//import univ.example.my_own_refrigerator.repository.RecipeRepository;
//
//import java.io.InputStreamReader;
//import java.io.IOException;
//import java.io.Reader;
//import java.nio.charset.StandardCharsets;
//import java.util.ArrayList;
//import java.util.List;
//
//@Component
//public class DataLoader implements CommandLineRunner {
//
//    private final RecipeRepository recipeRepository;
//
//    public DataLoader(RecipeRepository recipeRepository) {
//        this.recipeRepository = recipeRepository;
//    }
//
//    @Override
//    @Transactional
//    public void run(String... args) throws Exception {
//        System.out.println("데이터 로딩을 시작합니다.");
//        loadRecipesFromCsv("recipes_dataset_revise_final.csv");
//        System.out.println("데이터 로딩이 완료되었습니다.");
//    }
//
//    public void loadRecipesFromCsv(String filePath) {
//        try {
//            ClassPathResource resource = new ClassPathResource(filePath);
//            try (Reader reader = new InputStreamReader(resource.getInputStream(), StandardCharsets.UTF_8);
//                 CSVReader csvReader = new CSVReader(reader)) {
//
//                csvReader.readNext(); // 헤더 스킵
//
//                String[] data;
//                while ((data = csvReader.readNext()) != null) {
//                    RecipeEntity recipe = RecipeEntity.builder()
//                            .name(data[1])
//                            .category(data[3])
//                            .way(data[2])
//                            .hashTag(data[10])
//                            .mainImage(data[11])
//                            .subImage(data[12])
//                            .tip(data[data.length - 1])
//                            .energy(data[5])
//                            .carbohydrate(data[6])
//                            .protein(data[7])
//                            .fat(data[8])
//                            .sodium(data[9])
//                            .build();
//
//                    List<RecipeIngredientEntity> ingredients = new ArrayList<>();
//                    String ingredientInfo = data[13].replaceAll("\"", "");
//                    String[] ingredientPairs = ingredientInfo.split(", ");
//                    for (String pair : ingredientPairs) {
//                        if (pair.contains("|")) {
//                            String[] parts = pair.split("\\|");
//                            // 여기에서 배열의 길이가 2 이상인지 확인
//                            if (parts.length >= 2) {
//                                RecipeIngredientEntity ingredient = RecipeIngredientEntity.builder()
//                                        .name(parts[0].trim())
//                                        .amount(parts[1].trim())
//                                        .recipe(recipe)
//                                        .build();
//                                ingredients.add(ingredient);
//                            }
//                        }
//                    }
//                    recipe.setIngredients(ingredients);
//
//                    List<RecipeStepEntity> steps = new ArrayList<>();
//                    for (int i = 1; i <= 20; i++) {
//                        int descIndex = 13 + i;
//                        int imgIndex = 33 + i;
//
//                        if (descIndex < data.length && !data[descIndex].isEmpty()) {
//                            String description = data[descIndex].replaceAll("\"", "");
//                            String image = (imgIndex < data.length) ? data[imgIndex].replaceAll("\"", "") : null;
//
//                            RecipeStepEntity step = RecipeStepEntity.builder()
//                                    .description(description)
//                                    .image(image)
//                                    .recipe(recipe)
//                                    .build();
//                            steps.add(step);
//                        } else {
//                            break;
//                        }
//                    }
//                    recipe.setSteps(steps);
//
//                    recipeRepository.save(recipe);
//                }
//            }
//        } catch (IOException | CsvValidationException e) {
//            e.printStackTrace();
//        }
//    }
//}