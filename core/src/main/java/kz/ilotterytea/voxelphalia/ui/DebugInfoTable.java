package kz.ilotterytea.voxelphalia.ui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.Align;
import kz.ilotterytea.voxelphalia.VoxelphaliaConstants;
import kz.ilotterytea.voxelphalia.level.RenderableLevel;

public class DebugInfoTable extends Table {
    private final RenderableLevel level;
    private final Label gameLabel, chunkLabel, memoryLabel, jvmLabel;
    private boolean isEnabled;

    public DebugInfoTable(Skin skin) {
        this(skin, null);
    }

    public DebugInfoTable(Skin skin, RenderableLevel level) {
        super();
        setFillParent(true);
        align(Align.topLeft);
        pad(5f);

        this.level = level;

        // --- GAME INFO ---
        Table gameTable = new Table();
        gameTable.align(Align.topLeft);
        add(gameTable).grow();

        this.gameLabel = new Label("", skin);
        gameTable.add(gameLabel).growX().row();

        this.chunkLabel = new Label("", skin);
        gameTable.add(chunkLabel).growX().row();

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

        if (Gdx.input.isKeyJustPressed(Input.Keys.F3)) {
            isEnabled = !isEnabled;
        }

        String gameText = VoxelphaliaConstants.Metadata.APP_VERSION;

        if (isEnabled) {
            gameText += String.format(" (%s fps)", Gdx.graphics.getFramesPerSecond());

            if (level != null) {
                chunkLabel.setText(String.format("C: %s/%s", level.getRenderedChunkCount(), level.getLevel().getChunkCount()));
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
        memoryLabel.setVisible(isEnabled);
        jvmLabel.setVisible(isEnabled);
    }
}
