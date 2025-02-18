package kz.ilotterytea.voxelphalia.voxels;

import kz.ilotterytea.voxelphalia.entities.PlayerEntity;

public interface InteractableVoxel {
    void onInteract(PlayerEntity entity);
}
