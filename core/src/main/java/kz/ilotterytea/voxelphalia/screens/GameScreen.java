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
import kz.ilotterytea.voxelphalia.input.PlayerInputProcessor;
import kz.ilotterytea.voxelphalia.input.SpecialInputProcessor;
import kz.ilotterytea.voxelphalia.level.Level;
import kz.ilotterytea.voxelphalia.level.RenderableLevel;
import kz.ilotterytea.voxelphalia.level.TerrainGenerator;
import kz.ilotterytea.voxelphalia.level.VoxelType;
import kz.ilotterytea.voxelphalia.ui.DebugInfoTable;
import kz.ilotterytea.voxelphalia.ui.inventory.InventoryHotbarTable;

import java.util.Random;

public class GameScreen implements Screen {
    private VoxelphaliaGame game;
    private Stage stage;

    private PerspectiveCamera camera;

    private ModelBatch modelBatch;
    private Environment environment;

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

        renderableLevel = new RenderableLevel(camera, level);

        PlayerEntity playerEntity = new PlayerEntity(camera);
        float playerX = level.getWidthInVoxels() / 2.0f,
            playerZ = level.getDepthInVoxels() / 2.0f;
        playerEntity.setPosition(playerX,
            level.getHighestY(playerX, playerZ) + 1f,
            playerZ
        );
        level.addEntity(playerEntity);

        stage = new Stage(new ScreenViewport());
        Skin skin = game.getAssetManager().get("textures/gui/gui.skin");
        stage.addActor(new DebugInfoTable(skin, camera, renderableLevel));

        stage.addActor(new InventoryHotbarTable(skin, playerEntity.getInventory()));

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

        Gdx.input.setInputProcessor(new InputMultiplexer(
            new SpecialInputProcessor(camera),
            new PlayerInputProcessor(playerEntity, level, camera)
        ));
    }

    @Override
    public void render(float delta) {
        ScreenUtils.clear(Color.SKY, true);

        renderableLevel.tick(delta);
        renderableLevel.tick(delta, level, camera);

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
    }
}
