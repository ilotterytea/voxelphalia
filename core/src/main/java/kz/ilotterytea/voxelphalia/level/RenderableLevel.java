package kz.ilotterytea.voxelphalia.level;

import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.g3d.Environment;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Disposable;
import kz.ilotterytea.voxelphalia.utils.Renderable;
import kz.ilotterytea.voxelphalia.utils.Tickable;

public class RenderableLevel implements Disposable, Tickable, Renderable {
    private final Level level;
    private final Camera camera;
    private final Array<RenderableChunk> renderableChunks;
    private int renderedChunkCount;

    public RenderableLevel(Camera camera, Level level) {
        this.level = level;
        this.camera = camera;
        int chunkCapacity = level.width * level.height * level.depth;
        this.renderableChunks = new Array<>(chunkCapacity);

        for (int i = 0; i < chunkCapacity; i++) {
            Chunk chunk = level.chunks.get(i);
            Vector3 offset = level.getChunkPositionFromIndex(i);

            this.renderableChunks.add(new RenderableChunk(chunk, offset));
        }
    }

    @Override
    public void render(ModelBatch batch, Environment environment) {
        renderedChunkCount = 0;
        for (RenderableChunk chunk : renderableChunks) {
            if (isChunkVisible(chunk)) {
                chunk.render(batch, environment);
                renderedChunkCount++;
            }
        }
    }

    @Override
    public void tick(float delta) {
        for (RenderableChunk chunk : renderableChunks) {
            if (isChunkVisible(chunk)) chunk.tick(delta);
        }
    }

    @Override
    public void dispose() {
        for (RenderableChunk chunk : renderableChunks) {
            chunk.dispose();
        }
    }

    private boolean isChunkVisible(RenderableChunk chunk) {
        Vector3 o = chunk.getOffset();
        return this.camera.frustum.boundsInFrustum(
            o.x + 16 / 2f,
            o.y + 16 / 2f,
            o.z + 16 / 2f
            ,
            16, 16, 16
        );
    }

    public int getRenderedChunkCount() {
        return renderedChunkCount;
    }

    public Level getLevel() {
        return level;
    }
}
