package kz.ilotterytea.voxelphalia.voxels;

import com.badlogic.gdx.math.Vector3;
import kz.ilotterytea.voxelphalia.entities.DropEntity;
import kz.ilotterytea.voxelphalia.entities.PlayerEntity;
import kz.ilotterytea.voxelphalia.level.Level;
import kz.ilotterytea.voxelphalia.utils.Identifiable;
import kz.ilotterytea.voxelphalia.utils.Identifier;
import kz.ilotterytea.voxelphalia.utils.Tickable;

public class Voxel implements Cloneable, Tickable, DestroyableVoxel, Identifiable {
    private Identifier id, dropId;
    private VoxelMaterial material;
    private byte dropAmount;

    public Voxel(Identifier id, VoxelMaterial material) {
        this.id = id;
        this.dropId = id;
        this.dropAmount = 1;
        this.material = material;
    }

    @Override
    public Identifier getId() {
        return id;
    }

    public VoxelMaterial getMaterial() {
        return material;
    }

    public Identifier getDropId() {
        return dropId;
    }

    public void setDropId(Identifier dropId) {
        this.dropId = dropId;
    }

    public int getDropAmount() {
        return dropAmount;
    }

    public void setDropAmount(byte dropAmount) {
        this.dropAmount = dropAmount;
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
            clone.material = material;
            return clone;
        } catch (CloneNotSupportedException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void onDestroy(Voxel voxel, PlayerEntity entity, Level level, Vector3 position) {
        DropEntity drop = new DropEntity(dropId, dropAmount);

        drop.setPosition(position.x + 0.5f, position.y, position.z + 0.5f);
        level.addEntity(drop);
    }
}
