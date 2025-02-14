package kz.ilotterytea.voxelphalia.entities.mobs.friendly;

import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.math.MathUtils;
import kz.ilotterytea.voxelphalia.VoxelphaliaGame;

public class MobPig extends FriendlyMob {
    private float stateTime;
    private TextureAtlas atlas;

    public MobPig() {
        atlas = VoxelphaliaGame.getInstance()
            .getAssetManager()
            .get("textures/entities/mobs/pig.atlas", TextureAtlas.class);

        setWeight(8f);
        setSpeed(7f);
        setSize(0.9f, 0.9f, 0.9f);
        setDecal(atlas.findRegion("front_idle"), 1.5f, 1.5f);

        setHealth(10);
        setMaxHealth(10);
        setDamage(0);
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
        decal.setPosition(position.x, position.y + 0.8f, position.z);
    }
}
