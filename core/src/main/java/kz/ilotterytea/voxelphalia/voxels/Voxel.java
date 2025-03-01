package kz.ilotterytea.voxelphalia.voxels;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector3;
import kz.ilotterytea.voxelphalia.VoxelphaliaGame;
import kz.ilotterytea.voxelphalia.entities.DropEntity;
import kz.ilotterytea.voxelphalia.entities.ParticleEntity;
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

        // generating texture from terrain.png
        TextureRegion region = voxel.getMaterial().getFrontTextureRegion(VoxelphaliaGame.getInstance()
            .getAssetManager().get("textures/terrain.png", Texture.class));

        if (!region.getTexture().getTextureData().isPrepared()) {
            region.getTexture().getTextureData().prepare();
        }

        Pixmap terrainPixmap = region.getTexture().getTextureData().consumePixmap();

        Pixmap pixmap = new Pixmap(16, 16, Pixmap.Format.RGBA8888);
        pixmap.drawPixmap(terrainPixmap, 0, 0, region.getRegionX(), region.getRegionY(), region.getRegionWidth(), region.getRegionHeight());

        terrainPixmap.dispose();

        for (int i = 0; i < 100; i++) {
            ParticleEntity p = new ParticleEntity(new Color(pixmap.getPixel(MathUtils.random(0, 15), MathUtils.random(0, 15))), 4f, 1.5f);
            p.setPosition(position.x + 0.5f, position.y + 0.5f, position.z + 0.5f);
            level.addEntity(p);
        }

        pixmap.dispose();
    }
}
