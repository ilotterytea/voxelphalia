package kz.ilotterytea.voxelphalia.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.graphics.g3d.Environment;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.decals.CameraGroupStrategy;
import com.badlogic.gdx.graphics.g3d.decals.DecalBatch;
import com.badlogic.gdx.graphics.g3d.environment.DirectionalLight;
import com.badlogic.gdx.graphics.g3d.shaders.DefaultShader;
import com.badlogic.gdx.graphics.g3d.utils.DefaultShaderProvider;
import com.badlogic.gdx.graphics.g3d.utils.FirstPersonCameraController;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.github.czyzby.noise4j.map.Grid;
import com.github.czyzby.noise4j.map.generator.noise.NoiseGenerator;
import kz.ilotterytea.voxelphalia.VoxelphaliaGame;
import kz.ilotterytea.voxelphalia.entities.RenderableEntity;
import kz.ilotterytea.voxelphalia.entities.TreeEntity;
import kz.ilotterytea.voxelphalia.input.SpecialInputProcessor;
import kz.ilotterytea.voxelphalia.level.Level;
import kz.ilotterytea.voxelphalia.level.RenderableLevel;
import kz.ilotterytea.voxelphalia.ui.DebugInfoTable;

import java.util.Random;

public class GameScreen implements Screen {
    private VoxelphaliaGame game;
    private Stage stage;

    private PerspectiveCamera camera;
    private FirstPersonCameraController controller;

    private ModelBatch modelBatch;
    private Environment environment;

    private Level level;
    private RenderableLevel renderableLevel;

    private DecalBatch decalBatch;
    private Array<RenderableEntity> renderableEntities;

    @Override
    public void show() {
        game = VoxelphaliaGame.getInstance();

        camera = new PerspectiveCamera(60, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        camera.near = 0.1f;
        camera.far = 100f;
        camera.update();

        controller = new FirstPersonCameraController(camera);
        controller.setVelocity(100f);

        DefaultShader.Config config = new DefaultShader.Config();
        config.defaultCullFace = GL20.GL_FRONT;
        DefaultShaderProvider modelBatchProvider = new DefaultShaderProvider(config);
        modelBatch = new ModelBatch(modelBatchProvider);

        environment = new Environment();
        environment.set(new ColorAttribute(ColorAttribute.AmbientLight, 0.4f, 0.4f, 0.4f, 1.f));
        environment.set(new ColorAttribute(ColorAttribute.Fog, 0.8f, 0.8f, 0.8f, 0.8f));
        environment.add(new DirectionalLight().set(1, 1, 1, 0, -1, 0));

        int seed = (int) (System.currentTimeMillis() / 1000);

        level = new Level(40, 4, 40);
        renderableLevel = new RenderableLevel(camera, level);

        // terrain generation
        NoiseGenerator noiseGenerator = new NoiseGenerator();
        Grid grid = new Grid(level.getWidthInVoxels(), level.getDepthInVoxels());
        noiseStage(grid, noiseGenerator, 32, 0.6f, seed);
        noiseStage(grid, noiseGenerator, 16, 0.2f, seed);
        noiseStage(grid, noiseGenerator, 8, 0.1f, seed);
        noiseStage(grid, noiseGenerator, 4, 0.1f, seed);
        noiseStage(grid, noiseGenerator, 1, 0.05f, seed);

        for (int x = 0; x < grid.getWidth(); x++) {
            for (int z = 0; z < grid.getHeight(); z++) {
                int maxHeight = (int) (level.getHeightInVoxels() * grid.get(x, z));

                for (int y = 0; y < maxHeight; y++) {
                    byte voxel = 2;

                    // place grass on top
                    if (y == maxHeight - 1) {
                        voxel = 1;
                    }

                    level.placeVoxel(voxel, x, y, z);
                }
            }
        }

        camera.position.set(
            level.getWidthInVoxels() / 2.0f,
            0,
            level.getDepthInVoxels() / 2.0f
        );
        camera.position.y = level.getHighestY(camera.position.x, camera.position.y) + 5f;
        camera.update();

        Gdx.input.setInputProcessor(new InputMultiplexer(new SpecialInputProcessor(), controller));

        stage = new Stage(new ScreenViewport());
        Skin skin = game.getAssetManager().get("textures/gui/gui.skin");
        stage.addActor(new DebugInfoTable(skin, renderableLevel));

        decalBatch = new DecalBatch(new CameraGroupStrategy(camera));
        renderableEntities = new Array<>();

        Random random = new Random(seed);

        // tree generation
        for (int i = 0; i < random.nextInt(800, 2000); i++) {
            int x = random.nextInt(0, level.getWidthInVoxels());
            int z = random.nextInt(0, level.getDepthInVoxels());

            TreeEntity entity = new TreeEntity(new Vector3(x, level.getHighestY(x, z) + 2, z));
            renderableEntities.add(entity);
        }
    }

    @Override
    public void render(float delta) {
        ScreenUtils.clear(Color.SKY, true);

        controller.update(delta);

        renderableLevel.tick(delta);

        modelBatch.begin(camera);
        renderableLevel.render(modelBatch, environment);
        modelBatch.end();

        for (RenderableEntity entity : renderableEntities) {
            entity.tick(delta, camera);
            entity.render(decalBatch);
        }
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

    private void noiseStage(final Grid grid, final NoiseGenerator noiseGenerator, final int radius,
                            final float modifier, int seed) {
        noiseGenerator.setRadius(radius);
        noiseGenerator.setModifier(modifier);
        noiseGenerator.setSeed(seed);
        noiseGenerator.generate(grid);
    }
}
