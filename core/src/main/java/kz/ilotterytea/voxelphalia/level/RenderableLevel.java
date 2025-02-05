package kz.ilotterytea.voxelphalia.level;

import com.badlogic.gdx.graphics.g3d.Environment;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Disposable;
import kz.ilotterytea.voxelphalia.utils.Renderable;
import kz.ilotterytea.voxelphalia.utils.Tickable;

public class RenderableLevel implements Disposable, Tickable, Renderable {
    private final Array<RenderableChunk> renderableChunks;

    public RenderableLevel(Level level) {
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
        for (RenderableChunk chunk : renderableChunks) {
            chunk.render(batch, environment);
        }
    }

    @Override
    public void tick(float delta) {
        for (RenderableChunk chunk : renderableChunks) {
            chunk.tick(delta);
        }
    }

    @Override
    public void dispose() {
        for (RenderableChunk chunk : renderableChunks) {
            chunk.dispose();
        }
    }
}
