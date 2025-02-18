package kz.ilotterytea.voxelphalia.utils.registries;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.JsonReader;
import com.badlogic.gdx.utils.JsonValue;
import kz.ilotterytea.voxelphalia.recipes.Recipe;
import kz.ilotterytea.voxelphalia.recipes.RecipeWorkbenchLevel;

public class RecipeRegistry extends Registry<Recipe> {
    @Override
    protected void load() {
        FileHandle recipesHandle = Gdx.files.internal("data/recipes");

        for (FileHandle recipeFile : recipesHandle.list()) {
            byte resultId = Byte.parseByte(recipeFile.nameWithoutExtension());

            JsonValue json = new JsonReader().parse(recipeFile.readString());

            JsonValue ingredients = json.get("ingredientIds");
            byte[][] ingredientData = new byte[ingredients.size][];

            for (int i = 0; i < ingredients.size; i++) {
                JsonValue ingredient = ingredients.get(i);
                byte id = Byte.parseByte(ingredient.name());
                byte amount = ingredient.asByte();

                ingredientData[i] = new byte[]{id, amount};
            }

            byte resultAmount = json.getByte("resultAmount");

            float craftingTime = 0f;

            if (json.has("craftingTime")) {
                craftingTime = json.getFloat("craftingTime");
            }

            addEntry(new Recipe(ingredientData, resultAmount, resultId, craftingTime, RecipeWorkbenchLevel.values()[json.getInt("level")]));
        }
    }

    @Override
    public Recipe getEntryById(byte id) {
        for (Recipe data : entries) {
            if (data.resultId() == id) return data;
        }

        return null;
    }
}
