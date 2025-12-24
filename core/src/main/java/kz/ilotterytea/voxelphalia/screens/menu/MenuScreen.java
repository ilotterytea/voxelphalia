package kz.ilotterytea.voxelphalia.screens.menu;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g3d.Environment;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.environment.DirectionalLight;
import com.badlogic.gdx.graphics.g3d.shaders.DefaultShader;
import com.badlogic.gdx.graphics.g3d.utils.DefaultShaderProvider;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import kz.ilotterytea.voxelphalia.VoxelphaliaGame;
import kz.ilotterytea.voxelphalia.environment.SkyClouds;
import kz.ilotterytea.voxelphalia.l10n.LineId;
import kz.ilotterytea.voxelphalia.l10n.LocalizationManager;
import kz.ilotterytea.voxelphalia.ui.TintedActor;
import kz.ilotterytea.voxelphalia.ui.TitleWindow;
import kz.ilotterytea.voxelphalia.ui.menu.MainMenuTitleTable;

public class MenuScreen implements Screen {
    private VoxelphaliaGame game;
    private Stage stage;
    private ScreenViewport viewport;

    private PerspectiveCamera camera;
    private SkyClouds clouds;
    private ModelBatch modelBatch;
    private Environment environment;

    @Override
    public void show() {
        game = VoxelphaliaGame.getInstance();
        viewport = new ScreenViewport();
        stage = new Stage(viewport);
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
        brandTable.align(Align.topLeft);
        brandTable.pad(16f);
        table.add(brandTable).grow().row();

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

        // --- MENU BUTTONS ---
        Table menuTable = new Table();
        menuTable.align(Align.bottomLeft);
        menuTable.pad(16f);
        table.add(menuTable).grow();

        float buttonWidth = 400f, gapY = 8f;

        TextButton singleplayerButton = new TextButton(localizationManager.getLine(LineId.MENU_SINGLEPLAYER), skin);
        singleplayerButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                super.clicked(event, x, y);
                game.setScreen(new LevelSelectionScreen());
            }
        });
        menuTable.add(singleplayerButton).width(buttonWidth).padBottom(gapY).row();

        TextButton multiplayerButton = new TextButton(localizationManager.getLine(LineId.MENU_MULTIPLAYER), skin);
        multiplayerButton.setDisabled(true);
        menuTable.add(multiplayerButton).width(buttonWidth).padBottom(gapY * 3).row();

        TextButton settingsButton = new TextButton(localizationManager.getLine(LineId.MENU_SETTINGS), skin);
        settingsButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                super.clicked(event, x, y);
            }
        });
        menuTable.add(settingsButton).width(buttonWidth).row();

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
        viewport.update(width, height, true);

        camera.viewportWidth = width;
        camera.viewportHeight = height;
        camera.update();
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
        clouds.dispose();
        modelBatch.dispose();
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
