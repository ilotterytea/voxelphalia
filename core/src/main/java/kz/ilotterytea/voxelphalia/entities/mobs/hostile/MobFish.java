package kz.ilotterytea.voxelphalia.entities.mobs.hostile;

import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.math.MathUtils;
import kz.ilotterytea.voxelphalia.VoxelphaliaGame;
import kz.ilotterytea.voxelphalia.level.Level;

public class MobFish extends HostileMob {
    private TextureAtlas atlas;

    public MobFish() {
        atlas = VoxelphaliaGame.getInstance()
            .getAssetManager()
            .get("textures/entities/mobs/fish.atlas", TextureAtlas.class);

        setWeight(8f);
        setSpeed(5f);
        setSize(0.9f, 0.9f, 0.9f);
        setDecal(atlas.findRegion("front_idle"), 1f, 1f);

        setHealth(4);
        setMaxHealth(4);
        setDamage(1);
    }

    @Override
    public void tick(float delta, Camera camera) {
        super.tick(delta, camera);

        double angle = MathUtils.atan2(direction.z, direction.x) - MathUtils.atan2(camera.direction.z, camera.direction.x);
        angle = (angle * MathUtils.radiansToDegrees + 360) % 360;

        String name;

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

        name += "_idle";

        decal.setTextureRegion(atlas.findRegion(name));
        decal.setPosition(position.x, position.y + 0.5f, position.z);
    }

    @Override
    public void wander(float delta, Level level) {
        super.wander(delta, level);
        jump();
    }
}
