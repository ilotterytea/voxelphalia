package kz.ilotterytea.voxelphalia.entities;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import kz.ilotterytea.voxelphalia.level.Level;

public class ParticleEntity extends RenderablePhysicalEntity {
    private boolean dead;
    private final float maxLifeTime;
    private float lifeTime;

    public ParticleEntity(TextureRegion region) {
        setDecal(region, 0.1f, 0.1f);
        setWeight(1f);
        setSpeed(10f);
        setSize(0.1f, 0.2f, 0.1f);

        this.maxLifeTime = MathUtils.random(5f, 10f);

        setDirection(MathUtils.random(-1f, 1f), MathUtils.random(-1f, 1f), MathUtils.random(-1f, 1f));

        velocity.y = MathUtils.random(2f, 4f);
        onGround = false;
    }

    @Override
    public void tick(float delta, Level level) {
        super.tick(delta, level);
        velocity.x = Math.max(velocity.x - 0.01f, 0f);
        moveForward(velocity.x, delta, level);

        lifeTime += delta;

        if (lifeTime >= maxLifeTime) {
            dead = true;
        }
    }

    @Override
    public void setPosition(float x, float y, float z) {
        super.setPosition(x, y, z);
        decal.setPosition(x, y + 0.1f, z);
    }

    public boolean isDead() {
        return dead;
    }
}
