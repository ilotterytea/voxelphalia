package kz.ilotterytea.voxelphalia.level;

import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;

public class Level {
    protected final Array<Chunk> chunks;
    protected final int width, height, depth;

    public Level(int width, int height, int depth) {
        this.width = width;
        this.height = height;
        this.depth = depth;

        int chunkCapacity = width * height * depth;
        this.chunks = new Array<>(chunkCapacity);

        for (int i = 0; i < chunkCapacity; i++) {
            this.chunks.add(new Chunk(16, 16, 16));
        }
    }

    public byte getVoxel(int x, int y, int z) {
        Chunk chunk = getChunk(x, y, z);
        int cx = x % 16;
        int cy = y % 16;
        int cz = z % 16;
        return chunk.getVoxel(cx, cy, cz);
    }

    public void placeVoxel(byte voxel, int x, int y, int z) {
        Chunk chunk = getChunk(x, y, z);
        int cx = x % 16;
        int cy = y % 16;
        int cz = z % 16;
        chunk.placeVoxel(voxel, cx, cy, cz);
    }

    public Chunk getChunk(int x, int y, int z) {
        return this.chunks.get(getChunkIndex(x, y, z));
    }

    public int getWidth() {
        return width;
    }

    public int getWidthInVoxels() {
        return getWidth() * 16;
    }

    public int getHeight() {
        return height;
    }

    public int getHeightInVoxels() {
        return getHeight() * 16;
    }

    public int getDepth() {
        return depth;
    }

    public int getDepthInVoxels() {
        return getDepth() * 16;
    }

    private int getChunkIndex(int x, int y, int z) {
        int cx = x / 16;
        int cy = y / 16;
        int cz = z / 16;

        return cx + cz * width + cy * width * depth;
    }

    public Vector3 getChunkPositionFromIndex(int index) {
        int cx = index % width;
        int cz = (index / width) % depth;
        int cy = (index / (width * depth)) % height;

        int x = cx * 16;
        int y = cy * 16;
        int z = cz * 16;

        return new Vector3(x, y, z);
    }

    public int getChunkCount() {
        return this.chunks.size;
    }

    public float getHighestY(float x, float z) {
        int ix = (int) x;
        int iz = (int) z;
        if (ix < 0 || ix >= getWidthInVoxels()) return 0;
        if (iz < 0 || iz >= getDepthInVoxels()) return 0;
        for (int y = getHeightInVoxels() - 1; y > 0; y--) {
            if (getVoxel(ix, y, iz) > 0) return y + 1;
        }
        return 0;
    }
}
