package kz.ilotterytea.voxelphalia.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g3d.Environment;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.decals.CameraGroupStrategy;
import com.badlogic.gdx.graphics.g3d.decals.DecalBatch;
import com.badlogic.gdx.graphics.g3d.environment.DirectionalLight;
import com.badlogic.gdx.graphics.g3d.shaders.DefaultShader;
import com.badlogic.gdx.graphics.g3d.utils.DefaultShaderProvider;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Container;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.github.czyzby.noise4j.map.generator.util.Generators;
import kz.ilotterytea.voxelphalia.VoxelphaliaGame;
import kz.ilotterytea.voxelphalia.entities.PlayerEntity;
import kz.ilotterytea.voxelphalia.entities.SaplingEntity;
import kz.ilotterytea.voxelphalia.environment.SkyClouds;
import kz.ilotterytea.voxelphalia.input.PlayerInputProcessor;
import kz.ilotterytea.voxelphalia.input.SpecialInputProcessor;
import kz.ilotterytea.voxelphalia.level.Level;
import kz.ilotterytea.voxelphalia.level.RenderableLevel;
import kz.ilotterytea.voxelphalia.level.TerrainGenerator;
import kz.ilotterytea.voxelphalia.level.VoxelType;
import kz.ilotterytea.voxelphalia.ui.DebugInfoStack;
import kz.ilotterytea.voxelphalia.ui.game.HotbarTable;
import kz.ilotterytea.voxelphalia.ui.game.RespawnScreenStack;

import java.util.Random;

public class GameScreen implements Screen {
    private VoxelphaliaGame game;
    private Stage stage;

    private PerspectiveCamera camera;

    private ModelBatch modelBatch;
    private Environment environment;
    private SkyClouds clouds;

    private Level level;
    private RenderableLevel renderableLevel;

    private DecalBatch decalBatch;

    @Override
    public void show() {
        game = VoxelphaliaGame.getInstance();

        camera = new PerspectiveCamera(60, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        camera.near = 0.1f;
        camera.far = 40.0f * game.getPreferences().getInteger("render-distance", 1);
        camera.update();

        DefaultShader.Config config = new DefaultShader.Config();
        config.defaultCullFace = GL20.GL_FRONT;
        DefaultShaderProvider modelBatchProvider = new DefaultShaderProvider(config);
        modelBatch = new ModelBatch(modelBatchProvider);

        environment = new Environment();
        environment.set(new ColorAttribute(ColorAttribute.AmbientLight, 0.4f, 0.4f, 0.4f, 1.f));
        environment.set(new ColorAttribute(ColorAttribute.Fog, Color.SKY));
        environment.add(new DirectionalLight().set(0.7f, 0.7f, 0.7f, 0, -1, 0));

        int seed = Generators.rollSeed();

        level = new Level(40, 4, 40);
        TerrainGenerator.generateTerrain(level, seed);

        Vector3 playerSpawnPoint = new Vector3(
            MathUtils.random(20, level.getWidthInVoxels() - 40),
            0f,
            MathUtils.random(20, level.getDepthInVoxels() - 40)
        );

        playerSpawnPoint.y = level.getHighestY(playerSpawnPoint.x, playerSpawnPoint.z) + 1f;

        PlayerEntity playerEntity = new PlayerEntity(playerSpawnPoint, camera);
        level.addEntity(playerEntity);

        renderableLevel = new RenderableLevel(camera, playerEntity, level);

        stage = new Stage(new ScreenViewport());
        Skin skin = game.getAssetManager().get("textures/gui/gui.skin");
        stage.addActor(new DebugInfoStack(skin, camera, renderableLevel));

        stage.addActor(new HotbarTable(skin, playerEntity));
        stage.addActor(new RespawnScreenStack(skin, playerEntity));

        // Crosshair
        Container<Image> crosshairContainer = new Container<>(new Image(game.getAssetManager().get("textures/gui/crosshair.png", Texture.class)));
        crosshairContainer.setFillParent(true);
        crosshairContainer.align(Align.center);
        stage.addActor(crosshairContainer);

        decalBatch = new DecalBatch(new CameraGroupStrategy(camera));

        Random random = new Random(seed);

        // tree generation
        for (int i = 0; i < random.nextInt(800, 2000); i++) {
            int x = random.nextInt(0, level.getWidthInVoxels());
            int z = random.nextInt(0, level.getDepthInVoxels());
            int y = (int) level.getHighestY(x, z);

            VoxelType voxelBelow = VoxelType.getById(level.getVoxel(x, y - 1, z));

            if (voxelBelow != VoxelType.GRASS) {
                continue;
            }

            SaplingEntity entity = new SaplingEntity(new Vector3(x, y, z), 0);
            level.addEntity(entity);
        }

        this.clouds = new SkyClouds(
            new Vector3(level.getWidthInVoxels() / 2f, level.getHeightInVoxels() + 10f, level.getDepthInVoxels() / 2f),
            new Vector3(1000f, 0f, 1000f)
        );

        Gdx.input.setInputProcessor(new InputMultiplexer(
            new SpecialInputProcessor(playerEntity, camera),
            new PlayerInputProcessor(playerEntity, level, camera),
            stage
        ));
    }

    @Override
    public void render(float delta) {
        ScreenUtils.clear(Color.SKY, true);

        renderableLevel.tick(delta);
        renderableLevel.tick(delta, level, camera);

        clouds.tick(delta, camera);
        clouds.render(modelBatch, environment);

        modelBatch.begin(camera);
        renderableLevel.render(modelBatch, environment);
        modelBatch.end();

        renderableLevel.render(decalBatch);
        decalBatch.flush();

        stage.act(delta);
        stage.draw();
    }

    @Override
    public void resize(int width, int height) {
        camera.viewportWidth = width;
        camera.viewportHeight = height;
        camera.update();

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
        renderableLevel.dispose();
        decalBatch.dispose();
        modelBatch.dispose();
        clouds.dispose();
    }
}
