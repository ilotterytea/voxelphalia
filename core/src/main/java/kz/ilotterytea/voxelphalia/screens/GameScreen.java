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
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.github.czyzby.noise4j.map.generator.util.Generators;
import kz.ilotterytea.voxelphalia.VoxelphaliaGame;
import kz.ilotterytea.voxelphalia.entities.*;
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
    private Array<RenderableEntity> renderableEntities;
    private PlayerEntity playerEntity;

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

        renderableEntities = new Array<>();

        playerEntity = new PlayerEntity(camera);
        float playerX = level.getWidthInVoxels() / 2.0f,
            playerZ = level.getDepthInVoxels() / 2.0f;
        playerEntity.setPosition(playerX,
            level.getHighestY(playerX, playerZ) + 1f,
            playerZ
        );

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

            TreeEntity entity = new TreeEntity(new Vector3(x, y + 2, z));
            renderableEntities.add(entity);
        }

        random = new Random(seed + 2);

        // rock generation
        for (int i = 0; i < random.nextInt(200, 800); i++) {
            int x = random.nextInt(0, level.getWidthInVoxels());
            int z = random.nextInt(0, level.getDepthInVoxels());
            int y = (int) level.getHighestY(x, z);

            VoxelType voxelBelow = VoxelType.getById(level.getVoxel(x, y - 1, z));

            if (voxelBelow != VoxelType.ROCK) {
                continue;
            }

            RockEntity entity = new RockEntity(new Vector3(x, level.getHighestY(x, z) + 1, z));
            renderableEntities.add(entity);
        }

        random = new Random(seed + 5);

        // ore generation
        for (int i = 0; i < random.nextInt(20, 100); i++) {
            int x = random.nextInt(0, level.getWidthInVoxels());
            int z = random.nextInt(0, level.getDepthInVoxels());
            int y = (int) level.getHighestY(x, z);

            VoxelType voxelBelow = VoxelType.getById(level.getVoxel(x, y - 1, z));

            if (voxelBelow != VoxelType.ROCK) {
                continue;
            }

            int typeRandom = random.nextInt() % 100;
            int type;

            // coal
            if (70 <= typeRandom) {
                type = 0;
            }
            // iron
            else if (30 <= typeRandom) {
                type = 1;
            }
            // gold
            else if (10 <= typeRandom) {
                type = 2;
            }
            // ruby
            else if (0 < typeRandom) {
                type = 3;
            }
            // gems
            else {
                type = 4;
            }

            OreEntity entity = new OreEntity(type, new Vector3(x, level.getHighestY(x, z) + 1, z));
            renderableEntities.add(entity);
        }

        Gdx.input.setCursorCatched(true);
        Gdx.input.setInputProcessor(new InputMultiplexer(
            new SpecialInputProcessor(camera),
            new PlayerInputProcessor(playerEntity, camera)
        ));
    }

    @Override
    public void render(float delta) {
        ScreenUtils.clear(Color.SKY, true);

        renderableLevel.tick(delta);
        playerEntity.tick(delta, level);

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
}
