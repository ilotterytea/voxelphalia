package kz.ilotterytea.voxelphalia.utils.registries;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.JsonReader;
import com.badlogic.gdx.utils.JsonValue;
import kz.ilotterytea.voxelphalia.VoxelphaliaGame;
import kz.ilotterytea.voxelphalia.recipes.Recipe;
import kz.ilotterytea.voxelphalia.recipes.RecipeWorkbenchLevel;
import kz.ilotterytea.voxelphalia.utils.Identifier;

import java.util.HashMap;

public class RecipeRegistry extends Registry<Recipe> {
    @Override
    public void load() {
        IdentifierRegistry identifierRegistry = VoxelphaliaGame.getInstance().getIdentifierRegistry();

        FileHandle assetsFile = Gdx.files.internal("assets.txt");

        for (String path : assetsFile.readString().split("\n")) {
            if (!path.startsWith("data/recipes/")) continue;
            FileHandle recipeFile = Gdx.files.internal(path);

            Identifier id = identifierRegistry.getEntry(new Identifier(recipeFile.nameWithoutExtension()));

            JsonValue json = new JsonReader().parse(recipeFile.readString());

            JsonValue ingredients = json.get("ingredientIds");
            HashMap<Identifier, Byte> ingredientData = new HashMap<>(ingredients.size);

            for (int i = 0; i < ingredients.size; i++) {
                JsonValue ingredient = ingredients.get(i);
                Identifier id2 = identifierRegistry.getEntry(Identifier.of(ingredient.name()));
                if (id2 == null) {
                    id = null;
                    break;
                }

                byte amount = ingredient.asByte();
                ingredientData.put(id2, amount);
            }

            if (id == null) continue;

            byte resultAmount = json.getByte("resultAmount");

            float craftingTime = 0f;

            if (json.has("craftingTime")) {
                craftingTime = json.getFloat("craftingTime");
            }

            addEntry(new Recipe(ingredientData, resultAmount, id, craftingTime, RecipeWorkbenchLevel.values()[json.getInt("level")]));
        }
    }
}
