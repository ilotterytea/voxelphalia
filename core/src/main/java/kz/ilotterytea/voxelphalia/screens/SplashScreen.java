package kz.ilotterytea.voxelphalia.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import kz.ilotterytea.voxelphalia.VoxelphaliaGame;
import kz.ilotterytea.voxelphalia.assets.AssetUtils;

public class SplashScreen implements Screen {
    private VoxelphaliaGame game;
    private Stage stage;

    private Texture devTexture;

    private TextureAtlas penguinAtlas;
    private Image penguinImage;
    private float penguinTime;
    private int penguinIndex;

    @Override
    public void show() {
        game = VoxelphaliaGame.getInstance();
        AssetUtils.queue(game.getAssetManager());

        stage = new Stage(new ScreenViewport());

        Table table = new Table();
        table.setFillParent(true);
        table.pad(16f);
        stage.addActor(table);

        // Logo
        Table devTable = new Table();
        table.add(devTable).grow().row();

        devTexture = new Texture(Gdx.files.internal("textures/gui/ilotterytea.png"));

        Image devImage = new Image(devTexture);
        devTable.add(devImage);

        // Penguin
        penguinAtlas = new TextureAtlas(Gdx.files.internal("textures/entities/mobs/penguin.atlas"));
        penguinImage = new Image(penguinAtlas.findRegion("side_walk", 1));

        Table penguinTable = new Table();
        penguinTable.align(Align.right);
        penguinTable.add(penguinImage).size(32f, 32f);
        table.add(penguinTable).growX();
    }

    @Override
    public void render(float delta) {
        ScreenUtils.clear(Color.BLACK);

        penguinTime += delta;

        if (penguinTime >= 0.25f) {
            penguinIndex++;
            penguinTime = 0;
            if (penguinIndex > 2) {
                penguinIndex = 1;
            }

            penguinImage.setDrawable(new TextureRegionDrawable(penguinAtlas.findRegion("side_walk", penguinIndex)));
        }

        stage.act(delta);
        stage.draw();

        if (game.getAssetManager().update()) {
            game.getIdentifierRegistry().load();
            game.getItemRegistry().load();
            game.getVoxelRegistry().load();
            game.getRecipeRegistry().load();
            game.getSoundRegistry().load();
            Gdx.app.log(SplashScreen.class.getSimpleName(), "Loaded all assets");
            game.setScreen(new LevelLoadingScreen("test"));
        }
    }

    @Override
    public void resize(int width, int height) {
        stage.getViewport().update(width, height, true);
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
        stage.dispose();
        penguinAtlas.dispose();
        devTexture.dispose();
    }
}
