package kz.ilotterytea.voxelphalia.voxels.specialvoxels;

import com.badlogic.gdx.math.Vector3;
import kz.ilotterytea.voxelphalia.VoxelphaliaGame;
import kz.ilotterytea.voxelphalia.entities.DropEntity;
import kz.ilotterytea.voxelphalia.entities.PlayerEntity;
import kz.ilotterytea.voxelphalia.level.Level;
import kz.ilotterytea.voxelphalia.utils.Identifier;
import kz.ilotterytea.voxelphalia.voxels.DestroyableVoxel;
import kz.ilotterytea.voxelphalia.voxels.Voxel;
import kz.ilotterytea.voxelphalia.voxels.VoxelMaterial;

public class StoneVoxel extends Voxel implements DestroyableVoxel {
    public StoneVoxel(Identifier id, VoxelMaterial material) {
        super(id, material);
    }

    @Override
    public void onDestroy(Voxel voxel, PlayerEntity entity, Level level, Vector3 position) {
        DropEntity drop = new DropEntity(VoxelphaliaGame.getInstance()
            .getVoxelRegistry()
            .getEntry("cobblestone"));
        drop.setPosition(position.x + 0.5f, position.y, position.z + 0.5f);
        level.addEntity(drop);
    }

    @Override
    public StoneVoxel clone() {
        return (StoneVoxel) super.clone();
    }
}
