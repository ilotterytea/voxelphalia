package kz.ilotterytea.voxelphalia.voxels.specialvoxels;

import kz.ilotterytea.voxelphalia.VoxelphaliaGame;
import kz.ilotterytea.voxelphalia.entities.PlayerEntity;
import kz.ilotterytea.voxelphalia.inventory.Inventory;
import kz.ilotterytea.voxelphalia.screens.GameScreen;
import kz.ilotterytea.voxelphalia.voxels.InteractableVoxel;
import kz.ilotterytea.voxelphalia.voxels.Voxel;
import kz.ilotterytea.voxelphalia.voxels.VoxelMaterial;

public class ChestVoxel extends Voxel implements InteractableVoxel {
    private Inventory inventory;

    public ChestVoxel(byte id, VoxelMaterial material) {
        super(id, material);
        this.inventory = new Inventory(30, (byte) 100);
    }

    @Override
    public void onInteract(PlayerEntity entity) {
        if (!VoxelphaliaGame.getInstance().getScreen().getClass().equals(GameScreen.class)) {
            return;
        }

        GameScreen screen = (GameScreen) VoxelphaliaGame.getInstance().getScreen();
        screen.getChestWindow().setVisible(true, this);
    }

    public Inventory getInventory() {
        return inventory;
    }

    @Override
    public ChestVoxel clone() {
        ChestVoxel clone = (ChestVoxel) super.clone();
        clone.inventory = inventory.clone();
        return clone;
    }
}
