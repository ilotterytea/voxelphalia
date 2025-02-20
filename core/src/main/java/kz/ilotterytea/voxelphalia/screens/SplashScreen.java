package kz.ilotterytea.voxelphalia.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.utils.ScreenUtils;
import kz.ilotterytea.voxelphalia.VoxelphaliaGame;
import kz.ilotterytea.voxelphalia.assets.AssetUtils;

public class SplashScreen implements Screen {
    private VoxelphaliaGame game;

    @Override
    public void show() {
        game = VoxelphaliaGame.getInstance();
        AssetUtils.queue(game.getAssetManager());
    }

    @Override
    public void render(float delta) {
        ScreenUtils.clear(Color.WHITE);

        if (game.getAssetManager().update()) {
            game.getIdentifierRegistry().load();
            game.getVoxelRegistry().load();
            game.getRecipeRegistry().load();
            Gdx.app.log(SplashScreen.class.getSimpleName(), "Loaded all assets");
            game.setScreen(new LevelLoadingScreen());
        }
    }

    @Override
    public void resize(int width, int height) {

    }

    @Override
    public void pause() {
        hide();
    }

    @Override
    public void resume() {
        show();
    }

    @Override
    public void hide() {
        dispose();
    }

    @Override
    public void dispose() {

    }
}
