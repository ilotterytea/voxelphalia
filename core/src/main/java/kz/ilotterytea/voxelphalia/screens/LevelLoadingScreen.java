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
import kz.ilotterytea.voxelphalia.VoxelphaliaGame;
import kz.ilotterytea.voxelphalia.entities.mobs.MobType;
import kz.ilotterytea.voxelphalia.l10n.LineId;
import kz.ilotterytea.voxelphalia.level.Chunk;
import kz.ilotterytea.voxelphalia.level.Level;
import kz.ilotterytea.voxelphalia.level.generators.IslandTerrainGenerator;
import kz.ilotterytea.voxelphalia.level.generators.TerrainGenerator;
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
    private TerrainGenerator generator;

    private Thread generationThread;

    public LevelLoadingScreen(Level level) {
        this.level = level;
    }

    @Override
    public void show() {
        game = VoxelphaliaGame.getInstance();
        stage = new Stage(new ScreenViewport());
        Skin skin = VoxelphaliaGame.getInstance()
            .getAssetManager()
            .get("textures/gui/gui.skin");

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

        Label titleLabel = new Label(game.getLocalizationManager().getLine(LineId.LOADING_TITLE), skin);
        table.add(titleLabel).padBottom(15f).row();

        stepLabel = new Label("", skin);
        table.add(stepLabel).padBottom(30f).row();

        stepBar = new ProgressBar(0f, 6f, 1f, false, skin);
        table.add(stepBar).width(300f);

        generator = new IslandTerrainGenerator(level.getSeed());

        runGenerationThread();
    }

    @Override
    public void render(float delta) {
        ScreenUtils.clear(Color.DARK_GRAY);

        LineId id = switch (step) {
            case 0 -> LineId.LOADING_TERRAIN_GENERATING;
            case 1 -> LineId.LOADING_TERRAIN_BUILDING;
            case 2 -> LineId.LOADING_MINERAL;
            case 3 -> LineId.LOADING_TREES;
            case 4 -> LineId.LOADING_LIFE;
            case 5 -> LineId.LOADING_SIMULATION;
            default -> LineId.LOADING_FINISHED;
        };

        stepLabel.setText(game.getLocalizationManager().getLine(id));
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
            while (step < 7) {
                switch (step) {
                    // generating terrain
                    case 0 -> generator.generateLandscape(level);
                    // mineralizing
                    case 2 -> generator.generateMinerals(level);
                    // planting trees
                    case 3 -> generator.generateTrees(level);
                    // spawning mobs
                    case 4 -> {
                        MobType[] mobs = {
                            MobType.PIG,
                            MobType.FISH,
                            MobType.PENGUIN
                        };

                        for (MobType mob : mobs) {
                            generator.generateMobs(level, mob);
                        }
                    }
                    // ticking
                    case 5 -> level.tick(Gdx.graphics.getDeltaTime());
                    // removing locks
                    case 6 -> {
                        for (Chunk chunk : level.getChunks()) {
                            chunk.setModified(false);
                            chunk.setLock(false);
                        }
                        level.setApplyPhysics(true);
                    }
                    default -> {
                    }
                }

                step++;
            }
        });

        generationThread.start();
    }
}
