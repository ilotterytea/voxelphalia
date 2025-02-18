package kz.ilotterytea.voxelphalia.voxels.specialvoxels;

import kz.ilotterytea.voxelphalia.VoxelphaliaGame;
import kz.ilotterytea.voxelphalia.entities.PlayerEntity;
import kz.ilotterytea.voxelphalia.recipes.RecipeWorkbenchLevel;
import kz.ilotterytea.voxelphalia.screens.GameScreen;
import kz.ilotterytea.voxelphalia.voxels.InteractableVoxel;
import kz.ilotterytea.voxelphalia.voxels.Voxel;
import kz.ilotterytea.voxelphalia.voxels.VoxelMaterial;

public class WorkbenchVoxel extends Voxel implements InteractableVoxel {
    public WorkbenchVoxel(byte id, VoxelMaterial material) {
        super(id, material);
    }

    @Override
    public void onInteract(PlayerEntity entity) {
        if (!VoxelphaliaGame.getInstance().getScreen().getClass().equals(GameScreen.class)) {
            return;
        }

        GameScreen screen = (GameScreen) VoxelphaliaGame.getInstance().getScreen();
        screen.getCraftingWindow().setVisible(true, RecipeWorkbenchLevel.WORKBENCH);
    }

    @Override
    public WorkbenchVoxel clone() {
        return (WorkbenchVoxel) super.clone();
    }
}
