package kz.ilotterytea.voxelphalia.voxels;

import com.badlogic.gdx.math.Vector3;
import kz.ilotterytea.voxelphalia.entities.PlayerEntity;
import kz.ilotterytea.voxelphalia.level.Level;

public interface DestroyableVoxel {
    void onDestroy(Voxel voxel, PlayerEntity entity, Level level, Vector3 position);
}
