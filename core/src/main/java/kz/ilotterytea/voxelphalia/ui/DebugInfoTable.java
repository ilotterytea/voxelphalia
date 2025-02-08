package kz.ilotterytea.voxelphalia.ui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.Align;
import kz.ilotterytea.voxelphalia.VoxelphaliaConstants;
import kz.ilotterytea.voxelphalia.VoxelphaliaGame;
import kz.ilotterytea.voxelphalia.level.RenderableLevel;

public class DebugInfoTable extends Table {
    private final RenderableLevel level;
    private final Camera camera;
    private final Label gameLabel, cameraLabel, chunkLabel, memoryLabel, jvmLabel;

    public DebugInfoTable(Skin skin) {
        this(skin, null, null);
    }

    public DebugInfoTable(Skin skin, Camera camera, RenderableLevel level) {
        super();
        setFillParent(true);
        align(Align.topLeft);
        pad(5f);

        this.level = level;
        this.camera = camera;

        // --- GAME INFO ---
        Table gameTable = new Table();
        gameTable.align(Align.topLeft);
        add(gameTable).grow();

        this.gameLabel = new Label("", skin);
        gameTable.add(gameLabel).growX().row();

        this.chunkLabel = new Label("", skin);
        gameTable.add(chunkLabel).growX().row();

        this.cameraLabel = new Label("", skin);
        gameTable.add(cameraLabel).growX().row();

        // --- DEVICE INFO ---
        Table deviceTable = new Table();
        deviceTable.align(Align.topRight);
        add(deviceTable).grow();

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
                chunkLabel.setText(String.format("C: %s/%s", level.getRenderedChunkCount(), level.getLevel().getChunkCount()));
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
        jvmLabel.setVisible(isEnabled);
    }
}
