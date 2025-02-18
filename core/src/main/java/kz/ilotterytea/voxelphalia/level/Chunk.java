package kz.ilotterytea.voxelphalia.level;

import kz.ilotterytea.voxelphalia.voxels.Voxel;

public class Chunk {
    protected final byte[] voxels;
    protected final Voxel[] voxelStates;
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

        this.voxelStates = new Voxel[voxelCapacity];
    }

    public byte getVoxel(int x, int y, int z) {
        int index = getIndex(x, y, z);
        if (index < 0 || index >= voxels.length) return 0;
        return this.voxels[index];
    }

    public Voxel getVoxelState(int x, int y, int z) {
        int index = getIndex(x, y, z);
        if (index < 0 || index >= voxelStates.length) return null;
        return this.voxelStates[index];
    }

    public void placeVoxel(byte voxel, int x, int y, int z) {
        int index = getIndex(x, y, z);
        if (index < 0 || index >= voxels.length) return;
        this.voxels[index] = voxel;
        this.isDirty = true;
    }

    public void placeVoxelState(Voxel voxel, int x, int y, int z) {
        int index = getIndex(x, y, z);
        if (index < 0 || index >= voxelStates.length) return;
        this.voxelStates[index] = voxel;
        this.isDirty = true;
    }

    public boolean hasVoxel(int x, int y, int z) {
        return getVoxel(x, y, z) != 0;
    }

    public boolean hasVoxelState(int x, int y, int z) {
        return getVoxelState(x, y, z) != null;
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
        if (x < 0 || x > width ||
            y < 0 || y > height ||
            z < 0 || z > depth) return -1;
        return x + z * width + y * width * height;
    }
}
