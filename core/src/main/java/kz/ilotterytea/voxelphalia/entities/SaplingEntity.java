package kz.ilotterytea.voxelphalia.entities;

import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector3;
import kz.ilotterytea.voxelphalia.VoxelphaliaGame;
import kz.ilotterytea.voxelphalia.level.Level;
import kz.ilotterytea.voxelphalia.voxels.Voxel;

import java.util.Random;

public class SaplingEntity extends RenderableEntity {
    private boolean grown;
    private float lifeTime;
    private final float adultLifeTime;

    public SaplingEntity(Vector3 position) {
        this(position, MathUtils.random(2800, 3600));
    }

    public SaplingEntity(Vector3 position, float adultLifeTime) {
        this.adultLifeTime = adultLifeTime;
        setDecal(new TextureRegion(
            VoxelphaliaGame.getInstance()
                .getAssetManager().get("textures/entities/tree.png", Texture.class),
            64 * new Random().nextInt(0, 2),
            128 * new Random().nextInt(0, 2),
            64, 128
        ), 2, 4);
        setPosition(position.x, position.y, position.z);
    }

    @Override
    public void tick(float delta, Level level) {
        super.tick(delta, level);
        lifeTime += delta;
        if (lifeTime < adultLifeTime || hasGrown()) return;

        int h = MathUtils.random(3, 4);

        Voxel leaves = VoxelphaliaGame.getInstance().getVoxelRegistry().getEntry("leaves");
        Voxel log = VoxelphaliaGame.getInstance().getVoxelRegistry().getEntry("log");

        // hat
        for (int y = 0; y < 2; y++) {
            for (int z = -2; z < 3; z++) {
                for (int x = -2; x < 3; x++) {
                    level.placeVoxel(leaves, (int) (position.x + x), (int) (position.y + h + y), (int) (position.z + z));
                }
            }
        }

        for (int z = -1; z < 2; z++) {
            for (int x = -1; x < 2; x++) {
                level.placeVoxel(leaves, (int) (position.x + x), (int) (position.y + h + 2), (int) (position.z + z));
            }
        }

        level.placeVoxel(leaves, (int) position.x, (int) (position.y + h + 3), (int) position.z);

        // root
        for (int i = 0; i < h + 3; i++) {
            level.placeVoxel(log, (int) position.x, (int) position.y + i, (int) position.z);
        }

        grown = true;
    }

    @Override
    public void setPosition(float x, float y, float z) {
        super.setPosition(x, y, z);
        this.decal.setPosition(x, y + 2, z);
    }

    @Override
    public void tick(float delta, Camera camera) {
        Vector3 lookAt = camera.position.cpy();
        lookAt.y = this.position.y + 2;

        if (this.decal != null) this.decal.lookAt(lookAt, Vector3.Y);
    }

    public boolean hasGrown() {
        return grown;
    }
}
