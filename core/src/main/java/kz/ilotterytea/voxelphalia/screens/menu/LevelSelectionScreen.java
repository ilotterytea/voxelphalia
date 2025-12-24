package kz.ilotterytea.voxelphalia.screens.menu;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import kz.ilotterytea.voxelphalia.VoxelphaliaConstants;
import kz.ilotterytea.voxelphalia.VoxelphaliaGame;
import kz.ilotterytea.voxelphalia.l10n.LineId;
import kz.ilotterytea.voxelphalia.l10n.LocalizationManager;
import kz.ilotterytea.voxelphalia.level.Level;
import kz.ilotterytea.voxelphalia.level.LevelStorage;
import kz.ilotterytea.voxelphalia.screens.LevelLoadingScreen;
import kz.ilotterytea.voxelphalia.ui.menu.TiledVoxelTexture;
import kz.ilotterytea.voxelphalia.utils.OSUtils;
import kz.ilotterytea.voxelphalia.utils.tuples.Pair;

import java.text.SimpleDateFormat;
import java.util.Comparator;
import java.util.Date;

public class LevelSelectionScreen implements Screen {
    private VoxelphaliaGame game;
    private Stage stage;

    private TiledVoxelTexture backgroundTexture;
    private Array<Texture> thumbnailTextures;

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

        Label titleLabel = new Label(localizationManager.getLine(LineId.MENU_SINGLEPLAYER), skin);
        table.add(titleLabel).row();

        // World selection
        Table worldSelectionWrapper = new Table();
        worldSelectionWrapper.align(Align.center);
        table.add(worldSelectionWrapper).grow().row();

        Table worldSelectionTable = new Table(skin);
        worldSelectionTable.pad(8f);
        ScrollPane worldSelectionPane = new ScrollPane(worldSelectionTable, skin);
        worldSelectionPane.setScrollingDisabled(false, true);
        worldSelectionWrapper.add(worldSelectionPane).height(384f);

        // --- Create a new level button ---
        TextButton createNewLevelButton = new TextButton(localizationManager.getLine(LineId.MENU_LEVEL_CREATE), skin);
        createNewLevelButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                super.clicked(event, x, y);
                game.setScreen(new LevelCreationScreen());
            }
        });
        worldSelectionTable.add(createNewLevelButton).size(384f, 384f);

        // Worlds
        FileHandle levelsFolder = Gdx.files.absolute(OSUtils.getUserDataDirectory(String.format("%s/%s/levels",
            VoxelphaliaConstants.Metadata.APP_DEV,
            VoxelphaliaConstants.Metadata.APP_ID
        )));

        Array<Pair<Level, FileHandle>> levels = new Array<>();

        for (FileHandle levelFolder : levelsFolder.list()) {
            if (!levelFolder.isDirectory()) continue;
            Level level = LevelStorage.loadLevel(levelFolder.name());
            if (level == null) continue;
            levels.add(new Pair<>(level, levelFolder));
        }

        levels.sort(Comparator.comparingLong(o -> o.first.getLastTimeOpened()));

        thumbnailTextures = new Array<>();

        for (int i = 0; i < levels.size; i++) {
            Pair<Level, FileHandle> level = levels.get(i);

            // Level (table)
            Table levelTable = new Table(skin);
            levelTable.setBackground("button-down");
            levelTable.pad(8f);
            worldSelectionTable.add(levelTable).padLeft(16f).growY().width(384f);

            // Level thumbnail
            Image thumbnailImage;

            String thumbnailPath = level.second.path() + "/thumbnail.png";
            FileHandle thumbnailHandle = Gdx.files.absolute(thumbnailPath);

            if (thumbnailHandle.exists()) {
                Texture thumbnail = new Texture(thumbnailHandle);
                thumbnailTextures.add(thumbnail);
                thumbnailImage = new Image(thumbnail);
            } else {
                thumbnailImage = new Image(game.getAssetManager().get("textures/gui/thumbnail.png", Texture.class));
            }

            levelTable.add(thumbnailImage).size(176f, 128f).grow().center().padBottom(8f).row();

            // Level description
            Table levelDescTable = new Table();
            levelDescTable.align(Align.left);
            levelTable.add(levelDescTable).grow().padBottom(8f).row();

            // Level name
            Label levelNameLabel = new Label(level.first.getName(), skin);
            levelDescTable.add(levelNameLabel).growX().row();

            // Level timestamp
            Label levelTimestampLabel = new Label(new SimpleDateFormat("MMM dd, yyyy HH:mm a").format(new Date(level.first.getLastTimeOpened())), skin, "gray");
            levelDescTable.add(levelTimestampLabel).growX().row();

            // Level generation type
            Label levelGenTypeLabel = new Label(localizationManager.getLine(LineId.parse("menu.level.type." + level.first.getGeneratorType().toString())), skin, "gray");
            levelDescTable.add(levelGenTypeLabel).growX().row();

            // Level game mode
            Label levelGameModeLabel = new Label(localizationManager.getLine(LineId.parse("menu.level.mode." + level.first.getGameMode().toString())), skin, "gray");
            levelDescTable.add(levelGameModeLabel).growX();

            // Buttons
            Table levelButtonTable = new Table();
            levelButtonTable.align(Align.right);
            levelTable.add(levelButtonTable).growX().row();

            // Deletion button
            TextButton deleteButton = new TextButton(localizationManager.getLine(LineId.MENU_LEVEL_DELETE), skin);
            deleteButton.addListener(new ClickListener() {
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    super.clicked(event, x, y);
                    if (level.second.deleteDirectory()) {
                        levelTable.remove();
                        levelTable.clear();
                    }
                }
            });
            levelButtonTable.add(deleteButton).width(100f).padRight(14f);

            // Play button
            TextButton playButton = new TextButton(localizationManager.getLine(LineId.MENU_LEVEL_PLAY), skin);
            playButton.addListener(new ClickListener() {
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    super.clicked(event, x, y);
                    game.setScreen(new LevelLoadingScreen(level.second.name(), Level.LevelGeneratorType.LIMITED, Level.LevelGameMode.SURVIVAL));
                }
            });
            levelButtonTable.add(playButton).width(100f);
        }

        // --- Close button
        TextButton closeButton = new TextButton(localizationManager.getLine(LineId.MENU_BACK), skin);
        closeButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                super.clicked(event, x, y);
                game.setScreen(new MenuScreen());
            }
        });
        table.add(closeButton);

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
        thumbnailTextures.forEach(Texture::dispose);
    }
}
