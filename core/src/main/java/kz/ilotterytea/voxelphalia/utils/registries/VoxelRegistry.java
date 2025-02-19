package kz.ilotterytea.voxelphalia.utils.registries;

import com.badlogic.gdx.math.Vector2;
import kz.ilotterytea.voxelphalia.utils.Identifier;
import kz.ilotterytea.voxelphalia.voxels.Voxel;
import kz.ilotterytea.voxelphalia.voxels.VoxelMaterial;
import kz.ilotterytea.voxelphalia.voxels.specialvoxels.ChestVoxel;
import kz.ilotterytea.voxelphalia.voxels.specialvoxels.FurnaceVoxel;
import kz.ilotterytea.voxelphalia.voxels.specialvoxels.StoneVoxel;
import kz.ilotterytea.voxelphalia.voxels.specialvoxels.WorkbenchVoxel;

public class VoxelRegistry extends Registry<Voxel> {
    @Override
    protected void load() {
        // air
        Voxel air = new Voxel(new Identifier("air"), new VoxelMaterial());
        air.getMaterial().setTranslucent(true);
        addEntry(air);
        // grass
        addEntry(new Voxel(new Identifier("grass_voxel"), new VoxelMaterial(new Vector2(0, 0))));
        // stone
        addEntry(new StoneVoxel(new Identifier("stone"), new VoxelMaterial(new Vector2(1, 0))));
        // dirt
        addEntry(new Voxel(new Identifier("dirt"), new VoxelMaterial(new Vector2(2, 0))));
        // sand
        addEntry(new Voxel(new Identifier("sand"), new VoxelMaterial(new Vector2(3, 0))));
        // mantle
        addEntry(new Voxel(new Identifier("mantle"), new VoxelMaterial(new Vector2(4, 0))));
        // snow
        addEntry(new Voxel(new Identifier("snow"), new VoxelMaterial(new Vector2(5, 0))));
        // water
        Voxel water = new Voxel(new Identifier("water"), new VoxelMaterial(new Vector2(0, 15)));
        water.getMaterial().setTranslucent(true);
        addEntry(water);
        // missing voxel
        addEntry(new Voxel(new Identifier("missing_voxel"), new VoxelMaterial(new Vector2(15, 15))));
        // log
        addEntry(new Voxel(new Identifier("log"), new VoxelMaterial(new Vector2(6, 0), new Vector2(7, 0))));
        // leaves
        addEntry(new Voxel(new Identifier("leaves"), new VoxelMaterial(new Vector2(8, 0))));
        // cobblestone
        addEntry(new Voxel(new Identifier("cobblestone"), new VoxelMaterial(new Vector2(0, 1))));
        // coal voxel
        addEntry(new Voxel(new Identifier("coal_mineral"), new VoxelMaterial(new Vector2(0, 2))));
        // iron voxel
        addEntry(new Voxel(new Identifier("iron_mineral"), new VoxelMaterial(new Vector2(1, 2))));
        // gold voxel
        addEntry(new Voxel(new Identifier("gold_mineral"), new VoxelMaterial(new Vector2(2, 2))));
        // gem voxel
        addEntry(new Voxel(new Identifier("gem_mineral"), new VoxelMaterial(new Vector2(0, 3))));
        // ruby voxel
        addEntry(new Voxel(new Identifier("ruby_mineral"), new VoxelMaterial(new Vector2(1, 3))));
        // planks
        addEntry(new Voxel(new Identifier("planks"), new VoxelMaterial(new Vector2(6, 1))));
        // workbench
        addEntry(new WorkbenchVoxel(new Identifier("workbench"), new VoxelMaterial(new Vector2(15, 1), new Vector2(14, 1), new Vector2(15, 2))));
        // furnace
        VoxelMaterial furnace = new VoxelMaterial(
            new Vector2(13, 2),
            new Vector2(13, 2),
            new Vector2(13, 2),
            new Vector2(14, 2)
        );
        furnace.setStateSave(true);
        addEntry(new FurnaceVoxel(new Identifier("furnace"), furnace));
        // chest
        VoxelMaterial chest = new VoxelMaterial(
            new Vector2(14, 0),
            new Vector2(15, 2),
            new Vector2(15, 2),
            new Vector2(15, 0)
        );
        chest.setStateSave(true);
        addEntry(new ChestVoxel(new Identifier("chest"), chest));
    }
}
