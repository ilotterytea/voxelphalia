package kz.ilotterytea.voxelphalia.voxels.specialvoxels;

import kz.ilotterytea.voxelphalia.VoxelphaliaGame;
import kz.ilotterytea.voxelphalia.entities.PlayerEntity;
import kz.ilotterytea.voxelphalia.screens.GameScreen;
import kz.ilotterytea.voxelphalia.utils.Identifier;
import kz.ilotterytea.voxelphalia.voxels.InteractableVoxel;
import kz.ilotterytea.voxelphalia.voxels.Voxel;
import kz.ilotterytea.voxelphalia.voxels.VoxelMaterial;

public class WorkbenchVoxel extends Voxel implements InteractableVoxel {
    public WorkbenchVoxel(Identifier id, VoxelMaterial material) {
        super(id, material);
    }

    @Override
    public void onInteract(PlayerEntity entity) {
        if (!VoxelphaliaGame.getInstance().getScreen().getClass().equals(GameScreen.class)) {
            return;
        }

        GameScreen screen = (GameScreen) VoxelphaliaGame.getInstance().getScreen();
        screen.getWorkbenchWindow().setVisible(true);
    }

    @Override
    public WorkbenchVoxel clone() {
        return (WorkbenchVoxel) super.clone();
    }
}
