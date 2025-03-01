package kz.ilotterytea.voxelphalia.level;

import com.badlogic.gdx.math.Vector3;
import kz.ilotterytea.voxelphalia.utils.Identifier;
import kz.ilotterytea.voxelphalia.voxels.Voxel;

public class Chunk {
    protected final Identifier[] voxels;
    protected final Voxel[] voxelStates;
    protected final Vector3 offset;
    protected final int size;
    protected boolean isDirty, modified, locked;

    public Chunk(int size, Vector3 offset) {
        this.size = size;
        this.offset = offset;

        int voxelCapacity = (int) Math.pow(size, 3);
        this.voxels = new Identifier[voxelCapacity];

        this.voxelStates = new Voxel[voxelCapacity];
    }

    public Identifier getVoxel(int x, int y, int z) {
        int index = getIndex(x, y, z);
        if (index < 0 || index >= voxels.length) return null;
        return this.voxels[index];
    }

    public Voxel getVoxelState(int x, int y, int z) {
        int index = getIndex(x, y, z);
        if (index < 0 || index >= voxelStates.length) return null;
        return this.voxelStates[index];
    }

    public void placeVoxel(Voxel voxel, int x, int y, int z) {
        if (locked) return;
        int index = getIndex(x, y, z);
        if (index < 0 || index >= voxels.length) return;
        this.voxels[index] = voxel == null ? null : voxel.getId();
        this.isDirty = true;
        this.modified = true;
    }

    public void placeVoxelState(Voxel voxel, int x, int y, int z) {
        if (locked) return;
        int index = getIndex(x, y, z);
        if (index < 0 || index >= voxelStates.length) return;
        this.voxelStates[index] = voxel;
        this.isDirty = true;
        this.modified = true;
    }

    public boolean hasVoxel(int x, int y, int z) {
        return getVoxel(x, y, z) != null;
    }

    public boolean hasVoxelState(int x, int y, int z) {
        return getVoxelState(x, y, z) != null;
    }

    public int getSize() {
        return size;
    }

    private int getIndex(int x, int y, int z) {
        if (x < 0 || x > size ||
            y < 0 || y > size ||
            z < 0 || z > size) return -1;
        return x + z * size + y * size * size;
    }

    public Vector3 getOffset() {
        return offset;
    }

    public boolean isModified() {
        return modified;
    }

    public void setModified(boolean modified) {
        this.modified = modified;
    }

    public boolean isLocked() {
        return locked;
    }

    public void setLock(boolean lock) {
        this.locked = lock;
    }
}
