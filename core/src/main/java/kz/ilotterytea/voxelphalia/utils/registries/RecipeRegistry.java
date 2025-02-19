package kz.ilotterytea.voxelphalia.utils.registries;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.JsonReader;
import com.badlogic.gdx.utils.JsonValue;
import kz.ilotterytea.voxelphalia.VoxelphaliaConstants;
import kz.ilotterytea.voxelphalia.VoxelphaliaGame;
import kz.ilotterytea.voxelphalia.recipes.Recipe;
import kz.ilotterytea.voxelphalia.recipes.RecipeWorkbenchLevel;
import kz.ilotterytea.voxelphalia.utils.Identifier;
import kz.ilotterytea.voxelphalia.voxels.Voxel;

import java.util.HashMap;

public class RecipeRegistry extends Registry<Recipe> {
    @Override
    protected void load() {
        FileHandle recipesHandle = Gdx.files.internal("data/recipes");
        VoxelRegistry voxelRegistry = VoxelphaliaGame.getInstance().getVoxelRegistry();

        for (FileHandle recipeFile : recipesHandle.list()) {
            Voxel voxel = voxelRegistry.getEntry(new Identifier(VoxelphaliaConstants.Metadata.APP_ID, recipeFile.nameWithoutExtension()));

            JsonValue json = new JsonReader().parse(recipeFile.readString());

            JsonValue ingredients = json.get("ingredientIds");
            HashMap<Identifier, Byte> ingredientData = new HashMap<>(ingredients.size);

            for (int i = 0; i < ingredients.size; i++) {
                JsonValue ingredient = ingredients.get(i);
                Voxel voxel1 = voxelRegistry.getEntry(Identifier.of(ingredient.name()));
                if (voxel1 == null) {
                    voxel = null;
                    break;
                }

                byte amount = ingredient.asByte();
                ingredientData.put(voxel1.getId(), amount);
            }

            if (voxel == null) continue;

            byte resultAmount = json.getByte("resultAmount");

            float craftingTime = 0f;

            if (json.has("craftingTime")) {
                craftingTime = json.getFloat("craftingTime");
            }

            addEntry(new Recipe(ingredientData, resultAmount, voxel.getId(), craftingTime, RecipeWorkbenchLevel.values()[json.getInt("level")]));
        }
    }
}
