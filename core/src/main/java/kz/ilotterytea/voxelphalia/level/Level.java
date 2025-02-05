package kz.ilotterytea.voxelphalia.level;

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

    private int getChunkIndex(int worldX, int worldY, int worldZ) {
        int cx = worldX / 16;
        int cy = worldY / 16;
        int cz = worldZ / 16;

        return cx + cz * width + cy * width * depth;
    }
}
