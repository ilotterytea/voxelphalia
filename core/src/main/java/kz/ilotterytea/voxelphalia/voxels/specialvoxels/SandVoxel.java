package kz.ilotterytea.voxelphalia.voxels.specialvoxels;

import com.badlogic.gdx.math.Vector3;
import kz.ilotterytea.voxelphalia.level.Level;
import kz.ilotterytea.voxelphalia.utils.Identifier;
import kz.ilotterytea.voxelphalia.voxels.PhysicalVoxel;
import kz.ilotterytea.voxelphalia.voxels.Voxel;
import kz.ilotterytea.voxelphalia.voxels.VoxelMaterial;

public class SandVoxel extends Voxel implements PhysicalVoxel {
    public SandVoxel(Identifier id, VoxelMaterial material) {
        super(id, material);
    }

    @Override
    public void onPhysicsApply(Level level, Vector3 position) {
        if (!level.hasVoxel(position.x, position.y - 1, position.z)) {
            level.placeVoxel(this, position.x, position.y - 1, position.z);
            level.placeVoxel(null, position.x, position.y, position.z);
        }
    }

    @Override
    public SandVoxel clone() {
        return (SandVoxel) super.clone();
    }
}
