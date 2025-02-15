package kz.ilotterytea.voxelphalia.entities.mobs.neutral;

import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g3d.decals.DecalBatch;
import com.badlogic.gdx.math.MathUtils;
import kz.ilotterytea.voxelphalia.VoxelphaliaGame;

public class MobPenguin extends NeutralMob {
    private TextureAtlas atlas;
    private float stateTime;

    public MobPenguin() {
        atlas = VoxelphaliaGame.getInstance()
            .getAssetManager()
            .get("textures/entities/mobs/penguin.atlas", TextureAtlas.class);

        setWeight(8f);
        setSpeed(2f);
        setSize(0.9f, 0.9f, 0.9f);
        setDecal(atlas.findRegion("front_idle"), 1f, 1f);

        setHealth(6);
        setMaxHealth(6);
        setDamage(2);
    }

    @Override
    public void tick(float delta, Camera camera) {
        super.tick(delta, camera);
        stateTime += delta;

        double angle = MathUtils.atan2(direction.z, direction.x) - MathUtils.atan2(camera.direction.z, camera.direction.x);
        angle = (angle * MathUtils.radiansToDegrees + 360) % 360;

        String name;
        int index = -1;

        if (225f <= angle && angle < 315f) {
            name = "side";
        } else if (45f <= angle && angle < 135f) {
            name = "side";
            decal.rotateY(180f);
        } else if (135f <= angle && angle < 225f) {
            name = "front";
        } else {
            name = "back";
        }

        if (idling) {
            name += "_idle";
        } else {
            name += "_walk";
            index = (int) ((velocity.x * stateTime) % 2 + 1);
        }

        decal.setTextureRegion(index < 0 ? atlas.findRegion(name) : atlas.findRegion(name, index));
    }

    @Override
    public void render(DecalBatch batch) {
        decal.setPosition(position.x, position.y + 0.5f, position.z);
        super.render(batch);
    }
}
