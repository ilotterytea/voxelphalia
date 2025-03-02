package kz.ilotterytea.voxelphalia.ui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Stack;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.Align;
import kz.ilotterytea.voxelphalia.VoxelphaliaConstants;
import kz.ilotterytea.voxelphalia.VoxelphaliaGame;
import kz.ilotterytea.voxelphalia.level.RenderableLevel;

public class DebugInfoStack extends Stack {
    private final RenderableLevel level;
    private final Camera camera;
    private final Label gameLabel, cameraLabel, chunkLabel, entitiesLabel, memoryLabel, jvmLabel;

    public DebugInfoStack(Skin skin) {
        this(skin, null, null);
    }

    public DebugInfoStack(Skin skin, Camera camera, RenderableLevel level) {
        super();
        setFillParent(true);

        this.level = level;
        this.camera = camera;

        setTouchable(Touchable.disabled);

        // --- GAME INFO ---
        Table gameTable = new Table();
        gameTable.setFillParent(true);
        gameTable.pad(5f);
        gameTable.align(Align.topLeft);
        add(gameTable);

        this.gameLabel = new Label("", skin);
        gameTable.add(gameLabel).growX().row();

        this.chunkLabel = new Label("", skin);
        gameTable.add(chunkLabel).growX().row();

        this.entitiesLabel = new Label("", skin);
        gameTable.add(entitiesLabel).growX().row();

        this.cameraLabel = new Label("", skin);
        gameTable.add(cameraLabel).growX().row();

        // --- DEVICE INFO ---
        Table deviceTable = new Table();
        deviceTable.align(Align.topRight);
        deviceTable.setFillParent(true);
        deviceTable.pad(5f);
        add(deviceTable);

        this.memoryLabel = new Label("", skin);
        memoryLabel.setAlignment(Align.right);
        deviceTable.add(memoryLabel).growX().row();

        this.jvmLabel = new Label(String.format("Java %s", System.getProperty("java.version")), skin);
        jvmLabel.setAlignment(Align.right);
        deviceTable.add(jvmLabel).growX().row();
    }

    @Override
    public void act(float delta) {
        super.act(delta);
        boolean isEnabled = VoxelphaliaGame.getInstance().getPreferences().getBoolean("debug", false);

        String gameText = VoxelphaliaConstants.Metadata.APP_VERSION;

        if (isEnabled) {
            gameText += String.format(" (%s fps)", Gdx.graphics.getFramesPerSecond());

            if (level != null) {
                chunkLabel.setText(String.format("C: %s/%s, V: %s, I: %s", level.getRenderedChunkCount(), level.getLevel().getChunkCount(), level.getRenderedVertexCount(), level.getRenderedIndexCount()));
                entitiesLabel.setText(String.format("E: %s", level.getLevel().getEntityCount()));
            }

            if (camera != null) {
                cameraLabel.setText(String.format("x: %s\ny: %s\nz: %s", camera.position.x, camera.position.y, camera.position.z));
            }

            Runtime rt = Runtime.getRuntime();
            double usedMemMb = ((rt.totalMemory() - rt.freeMemory()) / 1024.0) / 1024.0;
            double totalMemMb = (rt.totalMemory() / 1024.0) / 1024.0;
            double percentMemUsage = Math.round((usedMemMb / totalMemMb) * 100.0);

            memoryLabel.setText(String.format("Used memory: %s%% (%sMB) of %sMB",
                Math.round(percentMemUsage),
                Math.round(usedMemMb),
                Math.round(totalMemMb)
            ));
        }

        gameLabel.setText(gameText);
        chunkLabel.setVisible(isEnabled);
        cameraLabel.setVisible(isEnabled);
        memoryLabel.setVisible(isEnabled);
        entitiesLabel.setVisible(isEnabled);
        jvmLabel.setVisible(isEnabled);
    }
}
