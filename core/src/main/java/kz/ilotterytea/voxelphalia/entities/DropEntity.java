package kz.ilotterytea.voxelphalia.entities;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.g3d.decals.DecalBatch;
import com.badlogic.gdx.math.MathUtils;
import kz.ilotterytea.voxelphalia.VoxelphaliaGame;
import kz.ilotterytea.voxelphalia.items.Item;
import kz.ilotterytea.voxelphalia.utils.Identifier;

public class DropEntity extends RenderablePhysicalEntity {
    private final Identifier identifier;
    private final float maxLifeTime;
    private final byte amount;
    private float lifeTime;
    private boolean dead;

    public DropEntity(Identifier identifier, byte amount) {
        super();
        this.identifier = identifier;
        this.amount = amount > 0 ? (byte) MathUtils.random(1, amount) : amount;

        setWeight(0.5f);
        setSize(0.8f, 0.8f, 0.8f);
        maxLifeTime = 60f * 7f;

        Texture itemTexture = VoxelphaliaGame.getInstance()
            .getAssetManager()
            .get("textures/items.png");

        TextureAtlas voxelAtlas = VoxelphaliaGame.getInstance()
            .getAssetManager()
            .get("textures/gui/gui_voxels.atlas");

        TextureRegion region;
        Item item;

        if ((item = VoxelphaliaGame.getInstance().getItemRegistry().getEntry(identifier)) != null) {
            region = item.getMaterial().getTextureRegion(itemTexture);
        } else {
            region = voxelAtlas.findRegion(identifier.getName());
        }

        if (region == null) {
            region = voxelAtlas.findRegion(VoxelphaliaGame.getInstance().getIdentifierRegistry().getEntry("missing_voxel").getName());
        }

        setDecal(region, 0.5f, 0.5f);
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
            dead = playerEntity.getInventory().add(identifier, amount) == 0;
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

    public float getLifeTime() {
        return lifeTime;
    }

    public void setLifeTime(float lifeTime) {
        this.lifeTime = lifeTime;
    }

    public Identifier getIdentifier() {
        return identifier;
    }

    public byte getAmount() {
        return amount;
    }
}
