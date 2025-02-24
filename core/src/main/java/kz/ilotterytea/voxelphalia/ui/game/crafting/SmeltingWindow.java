package kz.ilotterytea.voxelphalia.ui.game.crafting;

import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import kz.ilotterytea.voxelphalia.VoxelphaliaGame;
import kz.ilotterytea.voxelphalia.entities.PlayerEntity;
import kz.ilotterytea.voxelphalia.inventory.Inventory;
import kz.ilotterytea.voxelphalia.l10n.LineId;
import kz.ilotterytea.voxelphalia.l10n.LocalizationManager;
import kz.ilotterytea.voxelphalia.recipes.Recipe;
import kz.ilotterytea.voxelphalia.recipes.RecipeWorkbenchLevel;
import kz.ilotterytea.voxelphalia.ui.IconButton;
import kz.ilotterytea.voxelphalia.utils.Identifier;
import kz.ilotterytea.voxelphalia.utils.registries.RecipeRegistry;
import kz.ilotterytea.voxelphalia.voxels.specialvoxels.FurnaceVoxel;

import java.util.Map;

public class SmeltingWindow extends CraftingWindow {
    private FurnaceVoxel furnace;

    public SmeltingWindow(Skin skin, PlayerEntity playerEntity) {
        super(skin, playerEntity, RecipeWorkbenchLevel.FURNACE);

        craftButton.clearListeners();
        craftButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                super.clicked(event, x, y);
                if (craftButton.isDisabled()) return;

                Inventory inventory = playerEntity.getInventory();

                for (Map.Entry<Identifier, Byte> entry : selectedRecipe.ingredients().entrySet()) {
                    inventory.remove(entry.getKey(), entry.getValue());
                }

                furnace.setVoxelToSmelt(selectedRecipe.resultId());
                showRecipe(selectedRecipe);
            }
        });
    }

    @Override
    public void act(float delta) {
        super.act(delta);

        LocalizationManager localizationManager = VoxelphaliaGame.getInstance().getLocalizationManager();

        if (isVisible()) {
            if (furnace.getSmeltId() != null && selectedRecipe.resultId().equals(furnace.getSmeltId()) && furnace.isSmelting()) {
                craftButton.setText(localizationManager.getLine(LineId.CRAFTING_REMAINING, (int) ((furnace.getMaxSmeltTime() - furnace.getSmeltTime()) * 10f) / 10f));
            } else {
                craftButton.setText(localizationManager.getLine(LineId.SMELTING_SMELT));
            }

            if (furnace.isFinished()) {
                Inventory inventory = playerEntity.getInventory();

                Recipe recipe = VoxelphaliaGame.getInstance()
                    .getRecipeRegistry()
                    .getEntries()
                    .stream()
                    .filter((x) -> x.resultId() == furnace.getSmeltId())
                    .findFirst()
                    .get();

                inventory.add(recipe.resultId(), recipe.resultAmount());
                furnace.setVoxelToSmelt(null);
                showRecipe(recipe);
            }
        }
    }

    @Override
    protected void showRecipe(Recipe recipe) {
        super.showRecipe(recipe);

        boolean furnaceSmelting = furnace == null || !furnace.isSmelting();
        boolean craftable = !craftButton.isDisabled() && furnaceSmelting;

        for (IconButton recipeButton : recipeButtons) {
            recipeButton.setDisabled(!furnaceSmelting);
        }

        craftButton.setDisabled(!craftable);
    }

    @Override
    public void setVisible(boolean visible) {
        super.setVisible(visible);

        RecipeRegistry recipeRegistry = VoxelphaliaGame.getInstance().getRecipeRegistry();

        if (furnace != null && furnace.isSmelting()) {
            showRecipe(recipeRegistry.getEntry(furnace.getId()));
        } else {
            showRecipe(recipeRegistry.getEntries().stream().filter((x) -> x.level() == level).findFirst().orElse(null));
        }

        if (!visible) {
            furnace = null;
        }
    }

    public void setFurnace(FurnaceVoxel furnace) {
        this.furnace = furnace;
    }
}
