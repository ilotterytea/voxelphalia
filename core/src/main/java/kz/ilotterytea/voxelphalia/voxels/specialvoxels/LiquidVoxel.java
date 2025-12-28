package kz.ilotterytea.voxelphalia.voxels.specialvoxels;

import com.badlogic.gdx.math.Vector3;
import kz.ilotterytea.voxelphalia.level.Level;
import kz.ilotterytea.voxelphalia.utils.Identifier;
import kz.ilotterytea.voxelphalia.voxels.PhysicalVoxel;
import kz.ilotterytea.voxelphalia.voxels.Voxel;
import kz.ilotterytea.voxelphalia.voxels.VoxelMaterial;

public class LiquidVoxel extends Voxel implements PhysicalVoxel {
    public LiquidVoxel(Identifier id, VoxelMaterial material) {
        super(id, material);
    }

    @Override
    public void onPhysicsApply(Level level, Vector3 position) {
        if (!level.hasVoxel(position.x, position.y - 1, position.z)) {
            level.placeVoxel(this, position.x, position.y - 1, position.z);
            return;
        }

        int[][] sides = {
            {1, 0},
            {-1, 0},
            {0, 1},
            {0, -1}
        };

        for (int[] s : sides) {
            float nx = position.x + s[0];
            float ny = position.y;
            float nz = position.z + s[1];

            if (!level.hasVoxel(nx, ny, nz)) {
                level.placeVoxel(this, nx, ny, nz);
                break;
            }
        }
    }

    @Override
    public LiquidVoxel clone() {
        return (LiquidVoxel) super.clone();
    }
}
