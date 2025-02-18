package kz.ilotterytea.voxelphalia.voxels;

public class Voxel {
    private final byte id;
    private final VoxelMaterial material;

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
}
