package kz.ilotterytea.voxelphalia.screens.menu;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import kz.ilotterytea.voxelphalia.VoxelphaliaGame;
import kz.ilotterytea.voxelphalia.l10n.LineId;
import kz.ilotterytea.voxelphalia.l10n.LocalizationManager;
import kz.ilotterytea.voxelphalia.level.Level;
import kz.ilotterytea.voxelphalia.screens.LevelLoadingScreen;
import kz.ilotterytea.voxelphalia.ui.menu.TiledVoxelTexture;

public class LevelCreationScreen implements Screen {
    private VoxelphaliaGame game;
    private Stage stage;

    private TiledVoxelTexture backgroundTexture;

    @Override
    public void show() {
        game = VoxelphaliaGame.getInstance();
        stage = new Stage(new ScreenViewport());
        Skin skin = game.getAssetManager().get("textures/gui/gui.skin");

        LocalizationManager localizationManager = game.getLocalizationManager();

        // background
        backgroundTexture = TiledVoxelTexture.random();
        Image bgTileImage = new Image(backgroundTexture.getRegion());
        bgTileImage.setFillParent(true);
        stage.addActor(bgTileImage);

        // tinting the background
        Image tintImage = new Image(skin.getDrawable("black-transparent"));
        tintImage.setFillParent(true);
        stage.addActor(tintImage);

        // -- MAIN TABLE --
        Table table = new Table();
        table.setFillParent(true);
        table.align(Align.center);
        table.pad(8f);
        stage.addActor(table);

        // -- TOP --
        Table topTable = new Table();
        Label titleLabel = new Label(localizationManager.getLine(LineId.MENU_LEVEL_CREATE_TITLE), skin);
        topTable.add(titleLabel);
        table.add(topTable).fillX().pad(16f).row();

        // -- CENTER --
        Table centerTable = new Table();
        centerTable.align(Align.top);
        table.add(centerTable).grow().pad(16f).row();

        // --- World name ---
        Table worldNameTable = new Table();
        worldNameTable.align(Align.topLeft);
        centerTable.add(worldNameTable).width(400f).row();

        Label worldName = new Label(localizationManager.getLine(LineId.MENU_LEVEL_CREATE_NAME), skin);
        worldName.setAlignment(Align.left);
        worldNameTable.add(worldName).padBottom(16f).growX().row();

        TextField worldField = new TextField("", skin);
        worldField.setMessageText("My World");
        worldNameTable.add(worldField).growX().padBottom(64f).row();

        // --- Seed name ---
        Table seedNameTable = new Table();
        seedNameTable.align(Align.topLeft);
        centerTable.add(seedNameTable).width(400f).row();

        Label seedName = new Label(localizationManager.getLine(LineId.MENU_LEVEL_CREATE_SEED), skin);
        seedName.setAlignment(Align.left);
        Label seedSubtitle = new Label(localizationManager.getLine(LineId.MENU_LEVEL_CREATE_SEED_DESCRIPTION), skin);
        seedName.setAlignment(Align.left);
        seedNameTable.add(seedName).padBottom(16f).growX().row();

        TextField seedField = new TextField("", skin);
        seedNameTable.add(seedField).growX().row();

        seedNameTable.add(seedSubtitle).padBottom(16f).growX().row();

        // -- BOTTOM --
        Table bottomTable = new Table();
        table.add(bottomTable).fillX().pad(16f).row();

        // -- Creation button --
        TextButton creationButton = new TextButton(localizationManager.getLine(LineId.MENU_LEVEL_CREATE), skin);
        creationButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                super.clicked(event, x, y);
                String levelName = worldField.getText();
                if (levelName.isEmpty()) levelName = worldField.getMessageText();
                game.setScreen(new LevelLoadingScreen(levelName, Level.LevelGeneratorType.LIMITED, Level.LevelGameMode.SURVIVAL));
            }
        });
        bottomTable.add(creationButton).width(400f).padBottom(16f).row();

        // -- Back button --
        TextButton backButton = new TextButton(localizationManager.getLine(LineId.MENU_BACK), skin);
        backButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                super.clicked(event, x, y);
                game.setScreen(new LevelSelectionScreen());
            }
        });
        bottomTable.add(backButton).width(400f);

        Gdx.input.setInputProcessor(stage);
    }

    @Override
    public void render(float delta) {
        ScreenUtils.clear(Color.SKY, true);

        stage.act(Math.min(Gdx.graphics.getDeltaTime(), 1 / 30f));
        stage.draw();
    }

    @Override
    public void resize(int width, int height) {
        stage.getViewport().update(width, height, true);
        backgroundTexture.getRegion().setRegionWidth(width / 6);
        backgroundTexture.getRegion().setRegionHeight(height / 6);
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
        Gdx.input.setInputProcessor(null);
        stage.dispose();
        backgroundTexture.dispose();
    }
}
