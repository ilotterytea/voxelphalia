package kz.ilotterytea.voxelphalia.voxels;

import com.badlogic.gdx.math.Vector3;
import kz.ilotterytea.voxelphalia.level.Level;

public interface PhysicalVoxel {
    void onPhysicsApply(Level level, Vector3 position);
}
