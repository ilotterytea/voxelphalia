package kz.ilotterytea.voxelphalia.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.github.czyzby.noise4j.map.Grid;
import com.github.czyzby.noise4j.map.generator.util.Generators;
import kz.ilotterytea.voxelphalia.VoxelphaliaGame;
import kz.ilotterytea.voxelphalia.entities.mobs.MobType;
import kz.ilotterytea.voxelphalia.level.Level;
import kz.ilotterytea.voxelphalia.level.TerrainGenerator;
import kz.ilotterytea.voxelphalia.voxels.Voxel;

import java.util.concurrent.atomic.AtomicReference;

public class LevelLoadingScreen implements Screen {
    private VoxelphaliaGame game;

    private Texture backgroundTexture;
    private TextureRegion backgroundRegion;

    private Stage stage;
    private Label stepLabel;
    private ProgressBar stepBar;

    private int step;

    private Level level;

    private Thread generationThread;

    @Override
    public void show() {
        game = VoxelphaliaGame.getInstance();
        stage = new Stage(new ScreenViewport());
        Skin skin = VoxelphaliaGame.getInstance()
            .getAssetManager()
            .get("textures/gui/gui.skin");

        // generating texture from terrain.png
        Voxel voxel;

        do {
            voxel = game.getVoxelRegistry().getEntryById((byte) MathUtils.random(1, game.getVoxelRegistry().getEntries().size() - 1));
        } while (voxel.getId() == 8);

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
        Image backgroundImage = new Image(backgroundRegion);
        backgroundImage.setFillParent(true);
        stage.addActor(backgroundImage);

        // tinting the background
        Image tintImage = new Image(skin.getDrawable("black-transparent"));
        tintImage.setFillParent(true);
        stage.addActor(tintImage);

        Image gradientImage = new Image(skin.getDrawable("loading-screen-background"));
        gradientImage.setFillParent(true);
        stage.addActor(gradientImage);

        // status
        Table table = new Table();
        table.setFillParent(true);
        stage.addActor(table);

        Label titleLabel = new Label("Loading level", skin);
        table.add(titleLabel).padBottom(15f).row();

        stepLabel = new Label("", skin);
        table.add(stepLabel).padBottom(30f).row();

        stepBar = new ProgressBar(0f, 6f, 1f, false, skin);
        table.add(stepBar).width(300f);

        level = new Level(30, 4, 30, Generators.rollSeed());
        runGenerationThread();
    }

    @Override
    public void render(float delta) {
        ScreenUtils.clear(Color.DARK_GRAY);

        String status = switch (step) {
            case 0 -> "Generating terrain";
            case 1 -> "Building terrain";
            case 2 -> "Mineralizing";
            case 3 -> "Placing trees";
            case 4 -> "Making it more alive";
            case 5 -> "Simulating a little bit :)";
            default -> "Finished!";
        };

        stepLabel.setText(status);
        stepBar.setValue(step);

        stage.act(delta);
        stage.draw();

        if (!generationThread.isAlive()) {
            VoxelphaliaGame.getInstance().setScreen(new GameScreen(level));
        }
    }

    @Override
    public void resize(int width, int height) {
        stage.getViewport().update(width, height, true);
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
        stage.dispose();
        backgroundTexture.dispose();

        generationThread.interrupt();
        generationThread = null;
    }

    private void runGenerationThread() {
        if (generationThread != null) return;

        AtomicReference<Grid> grid = new AtomicReference<>();

        generationThread = new Thread(() -> {
            while (step < 6) {
                switch (step) {
                    // generating terrain
                    case 0 ->
                        grid.set(TerrainGenerator.generateGrid(level.getWidthInVoxels(), level.getDepthInVoxels(), level.getSeed()));
                    // building terrain
                    case 1 -> TerrainGenerator.applyGrid(level, grid.get());
                    // placing minerals
                    case 2 -> {
                        Voxel[] minerals = {
                            game.getVoxelRegistry().getEntryById((byte) 12),
                            game.getVoxelRegistry().getEntryById((byte) 13),
                            game.getVoxelRegistry().getEntryById((byte) 14),
                            game.getVoxelRegistry().getEntryById((byte) 15),
                            game.getVoxelRegistry().getEntryById((byte) 16),
                        };

                        int[] mineralSettings = {
                            3, 23, 30,
                            2, 20, 20,
                            2, 20, 10,
                            1, 10, 5,
                            1, 5, 5
                        };

                        for (int i = 0; i < minerals.length; i++) {
                            TerrainGenerator.generateMinerals(
                                level, minerals[i],
                                mineralSettings[i], mineralSettings[i + 1], mineralSettings[i + 2],
                                (int) (level.getSeed() + Math.pow(2, i))
                            );
                        }
                    }
                    // placing trees
                    case 3 -> TerrainGenerator.generateTrees(level, 50, level.getSeed() + 19);
                    // spawning mobs
                    case 4 -> {
                        MobType[] mobs = {
                            MobType.PIG,
                            MobType.FISH,
                            MobType.PENGUIN
                        };

                        for (int i = 0; i < mobs.length; i++) {
                            TerrainGenerator.generateMobs(level, mobs[i], level.getSeed() + 20 + i);
                        }
                    }
                    // ticking
                    case 5 -> level.tick(Gdx.graphics.getDeltaTime());
                    default -> {
                    }
                }

                step++;
            }
        });

        generationThread.start();
    }
}
