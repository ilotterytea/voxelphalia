package kz.ilotterytea.voxelphalia.utils.registries;

import kz.ilotterytea.voxelphalia.voxels.Voxel;
import kz.ilotterytea.voxelphalia.voxels.VoxelMaterial;
import kz.ilotterytea.voxelphalia.voxels.specialvoxels.WorkbenchVoxel;

public class VoxelRegistry extends Registry<Voxel> {
    @Override
    protected void load() {
        // air
        Voxel air = new Voxel((byte) 0, new VoxelMaterial(-1, -1));
        air.getMaterial().setTranslucent(true);
        addEntry(air);
        // grass
        addEntry(new Voxel((byte) 1, new VoxelMaterial(0, 0)));
        // stone
        addEntry(new Voxel((byte) 2, new VoxelMaterial(1, 0)));
        // dirt
        addEntry(new Voxel((byte) 3, new VoxelMaterial(2, 0)));
        // sand
        addEntry(new Voxel((byte) 4, new VoxelMaterial(3, 0)));
        // mantle
        addEntry(new Voxel((byte) 5, new VoxelMaterial(4, 0)));
        // snow
        addEntry(new Voxel((byte) 6, new VoxelMaterial(5, 0)));
        // water
        Voxel water = new Voxel((byte) 7, new VoxelMaterial(0, 15));
        water.getMaterial().setTranslucent(true);
        addEntry(water);
        // missing voxel
        addEntry(new Voxel((byte) 8, new VoxelMaterial(15, 15)));
        // log
        addEntry(new Voxel((byte) 9, new VoxelMaterial(6, 0, 7, 0, 7, 0)));
        // leaves
        addEntry(new Voxel((byte) 10, new VoxelMaterial(8, 0)));
        // cobblestone
        addEntry(new Voxel((byte) 11, new VoxelMaterial(0, 1)));
        // coal voxel
        addEntry(new Voxel((byte) 12, new VoxelMaterial(0, 2)));
        // iron voxel
        addEntry(new Voxel((byte) 13, new VoxelMaterial(1, 2)));
        // gold voxel
        addEntry(new Voxel((byte) 14, new VoxelMaterial(2, 2)));
        // gem voxel
        addEntry(new Voxel((byte) 15, new VoxelMaterial(0, 3)));
        // ruby voxel
        addEntry(new Voxel((byte) 16, new VoxelMaterial(1, 3)));
        // planks
        addEntry(new Voxel((byte) 17, new VoxelMaterial(6, 1)));
        // workbench
        addEntry(new WorkbenchVoxel((byte) 18, new VoxelMaterial(15, 1, 14, 1, 15, 2)));
    }

    @Override
    public Voxel getEntryById(byte id) {
        for (Voxel voxel : entries) {
            if (voxel.getId() == id) {
                return voxel;
            }
        }
        return getEntryById((byte) 8);
    }
}
