package kz.ilotterytea.voxelphalia.voxels.specialvoxels;

import kz.ilotterytea.voxelphalia.VoxelphaliaGame;
import kz.ilotterytea.voxelphalia.entities.PlayerEntity;
import kz.ilotterytea.voxelphalia.recipes.Recipe;
import kz.ilotterytea.voxelphalia.screens.GameScreen;
import kz.ilotterytea.voxelphalia.voxels.InteractableVoxel;
import kz.ilotterytea.voxelphalia.voxels.Voxel;
import kz.ilotterytea.voxelphalia.voxels.VoxelMaterial;

public class FurnaceVoxel extends Voxel implements InteractableVoxel {
    private Voxel voxel;
    private float smeltTime, maxSmeltTime;
    private boolean finished;

    public FurnaceVoxel(byte id, VoxelMaterial material) {
        super(id, material);
    }

    @Override
    public void onInteract(PlayerEntity entity) {
        if (!VoxelphaliaGame.getInstance().getScreen().getClass().equals(GameScreen.class)) {
            return;
        }

        GameScreen screen = (GameScreen) VoxelphaliaGame.getInstance().getScreen();
        screen.getSmeltingWindow().setVisible(true, this);
    }

    @Override
    public void tick(float delta) {
        super.tick(delta);
        if (voxel == null) return;

        smeltTime += delta;

        if (smeltTime >= maxSmeltTime) {
            finished = true;
        }
    }

    public void setVoxelToSmelt(Voxel voxel) {
        this.voxel = voxel;
        this.smeltTime = 0f;

        this.maxSmeltTime = 0f;

        if (voxel != null) {
            Recipe recipe = VoxelphaliaGame.getInstance()
                .getRecipeRegistry()
                .getEntryById(voxel.getId());

            this.maxSmeltTime = recipe.craftingTime();
        }

        this.finished = false;
    }

    public Voxel getVoxel() {
        return voxel;
    }

    public float getSmeltTime() {
        return smeltTime;
    }

    public float getMaxSmeltTime() {
        return maxSmeltTime;
    }

    public boolean isFinished() {
        return finished;
    }

    public boolean isVoxelSmelting() {
        return this.voxel != null && !finished;
    }

    @Override
    public FurnaceVoxel clone() {
        return (FurnaceVoxel) super.clone();
    }
}
