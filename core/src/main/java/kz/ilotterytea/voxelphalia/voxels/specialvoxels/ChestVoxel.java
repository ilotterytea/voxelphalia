package kz.ilotterytea.voxelphalia.voxels.specialvoxels;

import com.badlogic.gdx.math.Vector3;
import kz.ilotterytea.voxelphalia.VoxelphaliaGame;
import kz.ilotterytea.voxelphalia.entities.DropEntity;
import kz.ilotterytea.voxelphalia.entities.PlayerEntity;
import kz.ilotterytea.voxelphalia.inventory.Inventory;
import kz.ilotterytea.voxelphalia.level.Level;
import kz.ilotterytea.voxelphalia.screens.GameScreen;
import kz.ilotterytea.voxelphalia.utils.Identifier;
import kz.ilotterytea.voxelphalia.voxels.InteractableVoxel;
import kz.ilotterytea.voxelphalia.voxels.Voxel;
import kz.ilotterytea.voxelphalia.voxels.VoxelMaterial;

public class ChestVoxel extends Voxel implements InteractableVoxel {
    private Inventory inventory;

    public ChestVoxel(Identifier id, VoxelMaterial material) {
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

    @Override
    public void onDestroy(Voxel voxel, PlayerEntity entity, Level level, Vector3 position) {
        super.onDestroy(voxel, entity, level, position);
        ChestVoxel chest = (ChestVoxel) voxel;

        for (Inventory.Slot slot : chest.inventory.getSlots()) {
            if (slot.id == null) continue;
            DropEntity drop = new DropEntity(slot.id, slot.quantity);
            drop.setPosition(position.x + 0.5f, position.y, position.z + 0.5f);
            level.addEntity(drop);
        }
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
