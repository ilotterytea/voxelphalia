package kz.ilotterytea.voxelphalia.voxels;

import kz.ilotterytea.voxelphalia.entities.PlayerEntity;

public interface InteractableVoxel extends DestroyableVoxel {
    void onInteract(PlayerEntity entity);
}
