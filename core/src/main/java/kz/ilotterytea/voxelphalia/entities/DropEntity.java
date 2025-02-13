package kz.ilotterytea.voxelphalia.entities;

import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g3d.decals.DecalBatch;
import kz.ilotterytea.voxelphalia.VoxelphaliaGame;
import kz.ilotterytea.voxelphalia.level.VoxelType;

public class DropEntity extends RenderablePhysicalEntity {
    private final byte voxel;
    private final float maxLifeTime;
    private float lifeTime;
    private boolean dead;

    public DropEntity(VoxelType type) {
        super();
        this.voxel = type.getVoxelId();

        setWeight(0.5f);
        setSize(0.8f, 0.8f, 0.8f);
        maxLifeTime = 60f * 7f;

        TextureAtlas atlas = VoxelphaliaGame.getInstance()
            .getAssetManager()
            .get("textures/gui/gui_voxels.atlas");

        setDecal(atlas.findRegion(String.valueOf(type.getVoxelId())), 0.5f, 0.5f);
    }

    @Override
    public void tick(float delta) {
        super.tick(delta);
        lifeTime += delta;
        jump();

        if (lifeTime >= maxLifeTime) {
            dead = true;
        }
    }

    @Override
    public void tick(float delta, PlayerEntity playerEntity) {
        super.tick(delta, playerEntity);

        if (playerEntity.getBox().intersects(box)) {
            dead = playerEntity.getInventory().add(voxel, (byte) 1) == 0;
        }
    }

    @Override
    public void render(DecalBatch batch) {
        decal.setPosition(position.x, position.y + 0.5f, position.z);
        super.render(batch);
    }

    public boolean isDead() {
        return dead;
    }
}
