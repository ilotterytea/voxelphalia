package kz.ilotterytea.voxelphalia.voxels;

import com.badlogic.gdx.math.Vector3;
import kz.ilotterytea.voxelphalia.VoxelphaliaGame;
import kz.ilotterytea.voxelphalia.entities.DropEntity;
import kz.ilotterytea.voxelphalia.entities.PlayerEntity;
import kz.ilotterytea.voxelphalia.level.Level;
import kz.ilotterytea.voxelphalia.utils.Tickable;

public class Voxel implements Cloneable, Tickable, DestroyableVoxel {
    private byte id;
    private VoxelMaterial material;

    public Voxel(byte id, VoxelMaterial material) {
        this.id = id;
        this.material = material;
    }

    public byte getId() {
        return id;
    }

    public VoxelMaterial getMaterial() {
        return material;
    }

    @Override
    public String toString() {
        return "Voxel{" +
            "id=" + id +
            '}';
    }

    @Override
    public Voxel clone() {
        try {
            Voxel clone = (Voxel) super.clone();
            clone.id = id;
            clone.material = material.clone();
            return clone;
        } catch (CloneNotSupportedException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void onDestroy(Voxel voxel, PlayerEntity entity, Level level, Vector3 position) {
        DropEntity drop = new DropEntity(VoxelphaliaGame.getInstance()
            .getVoxelRegistry()
            .getEntryById(voxel.getId()));
        drop.setPosition(position.x + 0.5f, position.y, position.z + 0.5f);
        level.addEntity(drop);
    }
}
