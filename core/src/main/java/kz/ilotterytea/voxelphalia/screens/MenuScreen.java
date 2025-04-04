package kz.ilotterytea.voxelphalia.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.g3d.Environment;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.environment.DirectionalLight;
import com.badlogic.gdx.graphics.g3d.shaders.DefaultShader;
import com.badlogic.gdx.graphics.g3d.utils.DefaultShaderProvider;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector3;
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
import kz.ilotterytea.voxelphalia.environment.SkyClouds;
import kz.ilotterytea.voxelphalia.l10n.LineId;
import kz.ilotterytea.voxelphalia.l10n.LocalizationManager;
import kz.ilotterytea.voxelphalia.level.Level;
import kz.ilotterytea.voxelphalia.level.LevelStorage;
import kz.ilotterytea.voxelphalia.ui.TintedActor;
import kz.ilotterytea.voxelphalia.ui.TitleWindow;
import kz.ilotterytea.voxelphalia.ui.game.SettingsWindow;
import kz.ilotterytea.voxelphalia.ui.menu.MainMenuTitleTable;
import kz.ilotterytea.voxelphalia.utils.OSUtils;
import kz.ilotterytea.voxelphalia.utils.tuples.Pair;
import kz.ilotterytea.voxelphalia.voxels.Voxel;

import java.util.Comparator;

public class MenuScreen implements Screen {
    private VoxelphaliaGame game;
    private Stage stage;

    private PerspectiveCamera camera;
    private SkyClouds clouds;
    private ModelBatch modelBatch;
    private Environment environment;

    private Texture backgroundTexture;
    private TextureRegion backgroundRegion;

    @Override
    public void show() {
        game = VoxelphaliaGame.getInstance();
        stage = new Stage(new ScreenViewport());
        Skin skin = game.getAssetManager().get("textures/gui/gui.skin");

        LocalizationManager localizationManager = game.getLocalizationManager();

        camera = new PerspectiveCamera(60, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        camera.near = 0.1f;
        camera.far = 40.0f * game.getPreferences().getInteger("render-distance", 1);
        camera.position.set(0, 0, 0);
        camera.rotate(Vector3.Y, 90f);
        camera.update();

        this.clouds = new SkyClouds(
            new Vector3(-1000f, 90f, 0f),
            new Vector3(2000f, 0f, 2000f)
        );

        DefaultShader.Config config = new DefaultShader.Config();
        config.defaultCullFace = GL20.GL_FRONT;
        DefaultShaderProvider modelBatchProvider = new DefaultShaderProvider(config);
        modelBatch = new ModelBatch(modelBatchProvider);

        environment = new Environment();
        environment.set(new ColorAttribute(ColorAttribute.AmbientLight, 0.4f, 0.4f, 0.4f, 1.f));
        environment.set(new ColorAttribute(ColorAttribute.Fog, Color.SKY));
        environment.add(new DirectionalLight().set(0.7f, 0.7f, 0.7f, 0, -1, 0));

        // generating texture from terrain.png
        Voxel voxel = game.getVoxelRegistry().getEntries().get((byte) MathUtils.random(1, game.getVoxelRegistry().getEntries().size() - 1));

        TextureRegion region = voxel.getMaterial().getFrontTextureRegion(VoxelphaliaGame.getInstance()
            .getAssetManager().get("textures/terrain.png", Texture.class));

        if (!region.getTexture().getTextureData().isPrepared()) {
            region.getTexture().getTextureData().prepare();
        }

        Pixmap terrainPixmap = region.getTexture().getTextureData().consumePixmap();

        Pixmap pixmap = new Pixmap(16, 16, Pixmap.Format.RGBA8888);
        pixmap.drawPixmap(terrainPixmap, 0, 0, region.getRegionX(), region.getRegionY(), region.getRegionWidth(), region.getRegionHeight());

        backgroundTexture = new Texture(pixmap);
        terrainPixmap.dispose();
        pixmap.dispose();

        backgroundTexture.setWrap(Texture.TextureWrap.Repeat, Texture.TextureWrap.Repeat);
        backgroundRegion = new TextureRegion(backgroundTexture, 0, 0, backgroundTexture.getWidth() * 16, backgroundTexture.getHeight() * 16);

        // background
        Image bgTileImage = new Image(backgroundRegion);
        bgTileImage.setFillParent(true);
        //stage.addActor(bgTileImage);

        // tinting the background
        Image tintImage = new Image(skin.getDrawable("black-transparent"));
        tintImage.setFillParent(true);
        //stage.addActor(tintImage);

        Image gradientImage = new Image(skin.getDrawable("loading-screen-background"));
        gradientImage.setFillParent(true);
        //stage.addActor(gradientImage);

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
        //MainMenuTitleImage brandImage = new MainMenuTitleImage();
        //brandImage.setScale(2f);
        //brandImage.setOrigin(brandImage.getWidth() / 2f, brandImage.getHeight() / 2f);

        MainMenuTitleTable brandImage = new MainMenuTitleTable();
        final int[] clickedBrandTimes = {0};
        brandImage.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                super.clicked(event, x, y);
                clickedBrandTimes[0]++;
                if (clickedBrandTimes[0] > 4) {
                    showCreditsWindow(skin);
                    clickedBrandTimes[0] = 0;
                }
            }
        });

        brandTable.add(brandImage).fill();

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
        ScreenUtils.clear(Color.SKY, true);

        clouds.tick(delta, camera);
        clouds.render(modelBatch, environment);

        // do not touch it, it fixed selectbox
        stage.act(Math.min(Gdx.graphics.getDeltaTime(), 1 / 30f));
        stage.draw();
    }

    @Override
    public void resize(int width, int height) {
        stage.getViewport().update(width, height, true);

        camera.viewportWidth = width;
        camera.viewportHeight = height;
        camera.update();

        backgroundRegion.setRegionWidth(width / 6);
        backgroundRegion.setRegionHeight(height / 6);
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
        clouds.dispose();
        modelBatch.dispose();
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

    private void showCreditsWindow(Skin skin) {
        TextureAtlas atlas = game.getAssetManager().get("textures/gui/credits.atlas");

        Table bodyTable = new Table();
        bodyTable.setFillParent(true);

        // Developed by ilotterytea
        bodyTable.add(new Label("Developed by", skin)).left().expand();

        Image devImage = new Image(game.getAssetManager().get("textures/gui/ilotterytea.png", Texture.class));
        devImage.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                super.clicked(event, x, y);
                Gdx.net.openURI("https://ilotterytea.kz");
            }
        });
        bodyTable.add(devImage).size(devImage.getWidth() * 0.25f, devImage.getHeight() * 0.25f).padLeft(32f).right().row();

        // Made with libGDX
        bodyTable.add(new Label("Made with", skin)).left().expand();
        Image engineImage = new Image(atlas.findRegion("libgdx"));
        engineImage.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                super.clicked(event, x, y);
                Gdx.net.openURI("https://libgdx.com");
            }
        });
        bodyTable.add(engineImage).size(engineImage.getWidth() * 0.25f, engineImage.getHeight() * 0.25f).padLeft(32f).right().row();

        // Sounds from FreeSounds
        bodyTable.add(new Label("Sounds from", skin)).left().expand();
        Image soundImage = new Image(atlas.findRegion("freesound"));
        soundImage.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                super.clicked(event, x, y);
                Gdx.net.openURI("https://freesound.org");
            }
        });
        bodyTable.add(soundImage).size(soundImage.getWidth() * 0.5f, soundImage.getHeight() * 0.5f).padLeft(32f).right().row();

        TitleWindow window = new TitleWindow(skin, "Credits", bodyTable);
        TintedActor tintedWindow = new TintedActor(skin, window);

        window.onClose(tintedWindow::remove);

        stage.addActor(tintedWindow);
    }
}
