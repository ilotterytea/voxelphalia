package kz.ilotterytea.voxelphalia.voxels;

import com.badlogic.gdx.math.Vector3;
import kz.ilotterytea.voxelphalia.VoxelphaliaGame;
import kz.ilotterytea.voxelphalia.entities.DropEntity;
import kz.ilotterytea.voxelphalia.entities.PlayerEntity;
import kz.ilotterytea.voxelphalia.level.Level;
import kz.ilotterytea.voxelphalia.utils.Identifiable;
import kz.ilotterytea.voxelphalia.utils.Identifier;
import kz.ilotterytea.voxelphalia.utils.Tickable;

public class Voxel implements Cloneable, Tickable, DestroyableVoxel, Identifiable {
    private Identifier id;
    private VoxelMaterial material;

    public Voxel(Identifier id, VoxelMaterial material) {
        this.id = id;
        this.material = material;
    }

    @Override
    public Identifier getId() {
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
            clone.material = material;
            return clone;
        } catch (CloneNotSupportedException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void onDestroy(Voxel voxel, PlayerEntity entity, Level level, Vector3 position) {
        DropEntity drop = new DropEntity(VoxelphaliaGame.getInstance()
            .getVoxelRegistry()
            .getEntry(voxel.getId()));
        drop.setPosition(position.x + 0.5f, position.y, position.z + 0.5f);
        level.addEntity(drop);
    }
}
