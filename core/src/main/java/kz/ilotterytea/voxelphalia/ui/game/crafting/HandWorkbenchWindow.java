package kz.ilotterytea.voxelphalia.ui.game.crafting;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import kz.ilotterytea.voxelphalia.entities.PlayerEntity;
import kz.ilotterytea.voxelphalia.recipes.RecipeWorkbenchLevel;

public class HandWorkbenchWindow extends CraftingWindow {
    public HandWorkbenchWindow(Skin skin, PlayerEntity playerEntity) {
        super(skin, playerEntity, RecipeWorkbenchLevel.HANDS);
    }

    @Override
    public void act(float delta) {
        super.act(delta);

        if (Gdx.input.isKeyJustPressed(Input.Keys.C)) {
            setVisible(!isVisible());
        }
    }
}
