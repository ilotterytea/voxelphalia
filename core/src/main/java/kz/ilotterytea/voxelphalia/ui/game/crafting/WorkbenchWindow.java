package kz.ilotterytea.voxelphalia.ui.game.crafting;

import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import kz.ilotterytea.voxelphalia.entities.PlayerEntity;
import kz.ilotterytea.voxelphalia.recipes.RecipeWorkbenchLevel;

public class WorkbenchWindow extends CraftingWindow {
    public WorkbenchWindow(Skin skin, PlayerEntity playerEntity) {
        super(skin, playerEntity, RecipeWorkbenchLevel.WORKBENCH);
    }
}
