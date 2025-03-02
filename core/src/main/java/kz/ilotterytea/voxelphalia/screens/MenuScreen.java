package kz.ilotterytea.voxelphalia.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Color;
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
import kz.ilotterytea.voxelphalia.ui.game.SettingsWindow;
import kz.ilotterytea.voxelphalia.utils.OSUtils;
import kz.ilotterytea.voxelphalia.utils.tuples.Pair;

import java.util.Comparator;

public class MenuScreen implements Screen {
    private VoxelphaliaGame game;
    private Stage stage;

    @Override
    public void show() {
        game = VoxelphaliaGame.getInstance();
        stage = new Stage(new ScreenViewport());
        Skin skin = game.getAssetManager().get("textures/gui/gui.skin");

        LocalizationManager localizationManager = game.getLocalizationManager();

        // -- MAIN TABLE --
        Table table = new Table();
        table.setFillParent(true);
        table.align(Align.center);
        table.pad(8f);
        stage.addActor(table);

        // -- BACKGROUND IMAGE FOR WINDOWS --
        Image backgroundImage = new Image(skin.getDrawable("pause-background"));
        backgroundImage.setFillParent(true);
        backgroundImage.setVisible(false);
        stage.addActor(backgroundImage);

        // -- BRAND TABLE --
        Table brandTable = new Table();
        brandTable.align(Align.center);
        table.add(brandTable).grow().row();

        // Brand
        Image brandImage = new Image();
        brandTable.add(brandImage);

        // -- WORLD SELECTION --
        Table worldSelectionWrapper = new Table();
        worldSelectionWrapper.align(Align.center);
        table.add(worldSelectionWrapper).grow().row();

        Table worldSelectionTable = new Table(skin);
        worldSelectionTable.setBackground("button-disabled");
        worldSelectionTable.pad(8f);
        ScrollPane worldSelectionPane = new ScrollPane(worldSelectionTable, skin);
        worldSelectionPane.setScrollingDisabled(true, false);
        worldSelectionPane.setFadeScrollBars(false);
        worldSelectionWrapper.add(worldSelectionPane).size(500f, 300f);

        // Loading levels
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

        levels.sort(Comparator.comparingInt(o -> Integer.parseInt(o.first.getName())));

        int levelCount = 0;

        for (Pair<Level, FileHandle> level : levels) {
            try {
                int num = Integer.parseInt(level.first.getName());
                levelCount = Math.max(levelCount, num);
            } catch (Exception ignored) {
                continue;
            }

            // Level (table)
            Table levelTable = new Table(skin);
            levelTable.setBackground("button-down");
            levelTable.pad(8f);
            worldSelectionTable.add(levelTable).growX().padBottom(16f).row();

            // Level name
            Label levelName = new Label(localizationManager.getLine(LineId.MENU_LEVEL_NAME, level.first.getName()), skin);
            levelName.setAlignment(Align.left);
            levelTable.add(levelName).grow().padBottom(8f).row();

            // Level description
            Table levelDescTable = new Table();
            levelDescTable.align(Align.left);
            levelTable.add(levelDescTable).grow().padBottom(8f).row();

            // Level chunk size
            Label levelChunkSizeLabel = new Label(localizationManager.getLine(LineId.MENU_LEVEL_SIZE,
                level.first.getWidth(),
                level.first.getHeight(),
                level.first.getDepth()
            ), skin);
            levelChunkSizeLabel.addListener(new TextTooltip(localizationManager.getLine(LineId.MENU_LEVEL_SIZE,
                level.first.getWidthInVoxels(),
                level.first.getHeightInVoxels(),
                level.first.getDepthInVoxels()
            ), skin));
            levelChunkSizeLabel.setAlignment(Align.left);
            levelDescTable.add(levelChunkSizeLabel).padRight(8f);

            // Level file size
            Label levelFileSizeLabel = new Label(String.format("%10.2fMB",
                determineLevelSize(level.second) / 1024f / 1024f
            ), skin);
            levelFileSizeLabel.setAlignment(Align.left);
            levelDescTable.add(levelFileSizeLabel);

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
                    game.setScreen(new LevelLoadingScreen(level.second.name()));
                }
            });
            levelButtonTable.add(playButton).width(100f);
        }

        // --- Create a new level button ---
        TextButton createNewLevelButton = new TextButton(localizationManager.getLine(LineId.MENU_LEVEL_CREATE), skin);
        levelCount++;
        int finalLevelCount = levelCount;
        createNewLevelButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                super.clicked(event, x, y);
                game.setScreen(new LevelLoadingScreen(String.format("%d", finalLevelCount)));
            }
        });
        worldSelectionTable.add(createNewLevelButton).growX().height(120f).padBottom(16f).row();

        // --- CONTROL BUTTONS ---
        Table controlButtonTable = new Table();
        table.add(controlButtonTable).growX().row();

        // -- LEFT SIDE OF CONTROL BUTTONS --
        Table leftControlButtonTable = new Table();
        leftControlButtonTable.align(Align.left);
        controlButtonTable.add(leftControlButtonTable).grow();

        // Quit button
        TextButton quitButton = new TextButton(localizationManager.getLine(LineId.MENU_QUIT), skin);
        quitButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                super.clicked(event, x, y);
                Gdx.app.exit();
            }
        });
        leftControlButtonTable.add(quitButton);

        // -- RIGHT SIDE OF CONTROL BUTTONS --
        Table rightControlButtonTable = new Table();
        rightControlButtonTable.align(Align.right);
        controlButtonTable.add(rightControlButtonTable).grow();

        // Settings button
        Table settingsTable = new Table();
        settingsTable.setFillParent(true);
        stage.addActor(settingsTable);

        SettingsWindow settingsWindow = new SettingsWindow(skin);
        settingsWindow.setVisible(false);
        settingsTable.add(settingsWindow).size(400f, 450f);
        settingsWindow.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                super.clicked(event, x, y);
                if (!settingsWindow.isVisible()) {
                    backgroundImage.setVisible(false);
                }
            }
        });

        TextButton settingsButton = new TextButton(localizationManager.getLine(LineId.MENU_SETTINGS), skin);
        settingsButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                super.clicked(event, x, y);
                settingsWindow.setVisible(true);
                backgroundImage.setVisible(true);
            }
        });
        rightControlButtonTable.add(settingsButton);

        Gdx.input.setInputProcessor(stage);
    }

    @Override
    public void render(float delta) {
        ScreenUtils.clear(Color.BLACK);

        // do not touch it, it fixed selectbox
        stage.act(Math.min(Gdx.graphics.getDeltaTime(), 1 / 30f));
        stage.draw();
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
        Gdx.input.setInputProcessor(null);
        stage.dispose();
    }

    private long determineLevelSize(FileHandle folder) {
        long size = 0;

        for (FileHandle file : folder.list()) {
            if (file.isDirectory()) {
                size += determineLevelSize(file);
                continue;
            }

            size += file.length();
        }

        return size;
    }
}
