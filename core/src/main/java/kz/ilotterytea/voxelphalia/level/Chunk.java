package kz.ilotterytea.voxelphalia.level;

public class Chunk {
    protected final byte[] voxels;
    protected final int width, height, depth;
    protected boolean isDirty;

    public Chunk(int width, int height, int depth) {
        this.width = width;
        this.height = height;
        this.depth = depth;

        int voxelCapacity = width * height * depth;
        this.voxels = new byte[voxelCapacity];

        // filling chunk with air
        for (int i = 0; i < voxelCapacity; i++) {
            this.voxels[i] = 0;
        }
    }

    public byte getVoxel(int x, int y, int z) {
        return this.voxels[getIndex(x, y, z)];
    }

    public void placeVoxel(byte voxel, int x, int y, int z) {
        this.voxels[getIndex(x, y, z)] = voxel;
        this.isDirty = true;
    }

    public boolean hasVoxel(int x, int y, int z) {
        return getVoxel(x, y, z) != 0;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public int getDepth() {
        return depth;
    }

    private int getIndex(int x, int y, int z) {
        return x + z * width + y * width * height;
    }
}
