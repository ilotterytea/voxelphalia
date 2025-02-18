package kz.ilotterytea.voxelphalia.voxels;

public class Voxel implements Cloneable {
    private byte id;
    private VoxelMaterial material;

    public Voxel(byte id, VoxelMaterial material) {
        this.id = id;
        this.material = material;
    }

    public byte getId() {
        return id;
    }

    public VoxelMaterial getMaterial() {
        return material;
    }

    @Override
    public String toString() {
        return "Voxel{" +
            "id=" + id +
            '}';
    }

    @Override
    public Voxel clone() {
        try {
            Voxel clone = (Voxel) super.clone();
            clone.id = id;
            clone.material = material.clone();
            return clone;
        } catch (CloneNotSupportedException e) {
            throw new RuntimeException(e);
        }
    }
}
