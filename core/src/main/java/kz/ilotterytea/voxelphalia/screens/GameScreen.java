package kz.ilotterytea.voxelphalia.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.graphics.g3d.Environment;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.environment.DirectionalLight;
import com.badlogic.gdx.graphics.g3d.shaders.DefaultShader;
import com.badlogic.gdx.graphics.g3d.utils.DefaultShaderProvider;
import com.badlogic.gdx.graphics.g3d.utils.FirstPersonCameraController;
import com.badlogic.gdx.utils.ScreenUtils;
import kz.ilotterytea.voxelphalia.level.Level;
import kz.ilotterytea.voxelphalia.level.RenderableLevel;

public class GameScreen implements Screen {
    private PerspectiveCamera camera;
    private FirstPersonCameraController controller;

    private ModelBatch modelBatch;
    private Environment environment;

    private Level level;
    private RenderableLevel renderableLevel;

    @Override
    public void show() {
        camera = new PerspectiveCamera(60, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        camera.near = 0.1f;
        camera.far = 100f;
        camera.update();

        controller = new FirstPersonCameraController(camera);

        DefaultShader.Config config = new DefaultShader.Config();
        config.defaultCullFace = GL20.GL_FRONT;
        DefaultShaderProvider modelBatchProvider = new DefaultShaderProvider(config);
        modelBatch = new ModelBatch(modelBatchProvider);

        environment = new Environment();
        environment.set(new ColorAttribute(ColorAttribute.AmbientLight, 0.4f, 0.4f, 0.4f, 1.f));
        environment.set(new ColorAttribute(ColorAttribute.Fog, 0.8f, 0.8f, 0.8f, 0.8f));
        environment.add(new DirectionalLight().set(1, 1, 1, 0, -1, 0));

        level = new Level(4, 2, 4);
        renderableLevel = new RenderableLevel(level);

        // test level
        for (int x = 0; x < level.getWidthInVoxels(); x++) {
            for (int z = 0; z < level.getWidthInVoxels(); z++) {
                for (int y = 0; y < 10; y++) {
                    byte voxel = 2;

                    // place grass on the top
                    if (y == 9) {
                        voxel = 1;
                    }

                    level.placeVoxel(voxel, x, y, z);
                }
            }
        }

        camera.position.set(
            level.getWidthInVoxels() / 2.0f,
            35f,
            level.getDepthInVoxels() / 2.0f
        );
        camera.update();

        Gdx.input.setInputProcessor(controller);
    }

    @Override
    public void render(float delta) {
        ScreenUtils.clear(Color.SKY, true);

        controller.update(delta);

        renderableLevel.tick(delta);

        modelBatch.begin(camera);
        renderableLevel.render(modelBatch, environment);
        modelBatch.end();
    }

    @Override
    public void resize(int width, int height) {
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
        renderableLevel.dispose();
        modelBatch.dispose();
    }
}
