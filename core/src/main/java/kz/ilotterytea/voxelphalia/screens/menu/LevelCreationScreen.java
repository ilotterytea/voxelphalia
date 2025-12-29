package kz.ilotterytea.voxelphalia.screens.menu;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import kz.ilotterytea.voxelphalia.VoxelphaliaGame;
import kz.ilotterytea.voxelphalia.l10n.LineId;
import kz.ilotterytea.voxelphalia.l10n.LocalizationManager;
import kz.ilotterytea.voxelphalia.level.Level;
import kz.ilotterytea.voxelphalia.screens.LevelLoadingScreen;
import kz.ilotterytea.voxelphalia.ui.menu.TiledVoxelTexture;
import kz.ilotterytea.voxelphalia.ui.sound.SoundingSelectBox;
import kz.ilotterytea.voxelphalia.ui.sound.SoundingTextButton;

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

        float sectionWidth = 512f, sectionPad = 16f, elementPad = 4f;

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
        centerTable.add(worldNameTable).width(sectionWidth).padBottom(sectionPad).row();

        Label worldName = new Label(localizationManager.getLine(LineId.MENU_LEVEL_CREATE_NAME), skin);
        worldName.setAlignment(Align.left);
        worldNameTable.add(worldName).padBottom(elementPad).growX().row();

        TextField worldField = new TextField("", skin);
        worldField.setMessageText("My World");
        worldNameTable.add(worldField).growX().row();

        // --- Seed name ---
        Table seedNameTable = new Table();
        seedNameTable.align(Align.topLeft);
        centerTable.add(seedNameTable).width(sectionWidth).padBottom(sectionPad).row();

        Label seedName = new Label(localizationManager.getLine(LineId.MENU_LEVEL_CREATE_SEED), skin);
        seedName.setAlignment(Align.left);
        Label seedSubtitle = new Label(localizationManager.getLine(LineId.MENU_LEVEL_CREATE_SEED_DESCRIPTION), skin, "level-creation-subtitle");
        seedName.setAlignment(Align.left);
        seedNameTable.add(seedName).growX().padBottom(elementPad).row();

        TextField seedField = new TextField("", skin);
        seedNameTable.add(seedField).growX().padBottom(elementPad).row();

        seedNameTable.add(seedSubtitle).growX().row();

        // Level type
        Table levelTypeTable = new Table();
        levelTypeTable.align(Align.topLeft);
        centerTable.add(levelTypeTable).width(sectionWidth).padBottom(sectionPad).row();

        Label levelTypeName = new Label(localizationManager.getLine(LineId.MENU_LEVEL_CREATE_TYPE), skin);
        levelTypeName.setAlignment(Align.left);
        levelTypeTable.add(levelTypeName).padBottom(elementPad).growX().row();
        Label levelTypeSubtitle = new Label("", skin, "level-creation-subtitle");
        levelTypeSubtitle.setAlignment(Align.left);

        SoundingSelectBox<String> levelTypeSelectBox = new SoundingSelectBox<>(skin);
        Array<String> levelTypeArray = new Array<>();
        for (Level.LevelGeneratorType type : Level.LevelGeneratorType.values()) levelTypeArray.add(type.name());
        levelTypeSelectBox.setItems(levelTypeArray);
        levelTypeSelectBox.setSelected(levelTypeArray.get(0));
        levelTypeSubtitle.setText(localizationManager.getLine(LineId.parse("menu.level.type." + levelTypeArray.get(0) + ".description")));

        levelTypeSelectBox.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                String selectedString = levelTypeSelectBox.getSelected();
                levelTypeSubtitle.setText(
                    localizationManager.getLine(LineId.parse("menu.level.type." + selectedString + ".description"))
                );
            }
        });
        levelTypeTable.add(levelTypeSelectBox).padBottom(elementPad).growX().row();
        levelTypeTable.add(levelTypeSubtitle).growX().row();

        // -- BOTTOM --
        Table bottomTable = new Table();
        table.add(bottomTable).fillX().pad(16f).row();

        // -- Creation button --
        SoundingTextButton creationButton = new SoundingTextButton(localizationManager.getLine(LineId.MENU_LEVEL_CREATE), skin);
        creationButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                super.clicked(event, x, y);
                String levelName = worldField.getText();
                if (levelName.isEmpty()) levelName = worldField.getMessageText();

                int seed = (int) (System.currentTimeMillis() / 1000);
                if (!seedField.getText().isBlank()) {
                    seed = seedField.getText().hashCode();
                }

                Level level = new Level(
                    levelName,
                    Level.LevelGeneratorType.valueOf(levelTypeSelectBox.getSelected()),
                    Level.LevelGameMode.SURVIVAL,
                    seed
                );

                game.setScreen(new LevelLoadingScreen(level));
            }
        });
        bottomTable.add(creationButton).width(sectionWidth).padBottom(16f).row();

        // -- Back button --
        SoundingTextButton backButton = new SoundingTextButton(localizationManager.getLine(LineId.MENU_BACK), skin);
        backButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                super.clicked(event, x, y);
                game.setScreen(new LevelSelectionScreen());
            }
        });
        bottomTable.add(backButton).width(sectionWidth);

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
