package kz.ilotterytea.voxelphalia.entities.mobs;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector3;
import kz.ilotterytea.voxelphalia.entities.Entity;
import kz.ilotterytea.voxelphalia.entities.RenderablePhysicalEntity;
import kz.ilotterytea.voxelphalia.level.Level;

public class MobEntity extends RenderablePhysicalEntity {
    protected int health, maxHealth;
    protected boolean dead, walking, idling;
    private float walkingTime, idleTime;

    @Override
    public void tick(float delta, Level level) {
        super.tick(delta, level);
        idleTime -= delta;
        idling = (int) idleTime >= 0;
        walking = (int) walkingTime >= 0;

        if (idleTime <= 0) {
            wander(delta, level);

            if (walkingTime <= 0 && MathUtils.random(0, 10) > 7) {
                idleTime = MathUtils.random(5, 10);
            }
        }

        if (collidingX || collidingZ) {
            jump();
        }
    }

    public void wander(float delta, Level level) {
        if (walkingTime <= 0) {
            Vector3 newPos = new Vector3(
                    position.x + MathUtils.random(-10, 10),
                    0f,
                    position.z + MathUtils.random(-10, 10)
            );

            newPos.sub(position).nor();
            newPos.y = 0;

            setDirection(newPos.x, newPos.y, newPos.z);

            walkingTime = MathUtils.random(5, 10);
        }

        moveForward(this.velocity.x, delta, level);
        this.walkingTime -= delta;
    }

    public void followEntity(Entity entity) {

    }

    public void takeDamage(int damage) {
        health -= damage;
        if (health <= 0) {
            health = 0;
            dead = true;
        }
    }

    public boolean isDead() {
        return dead;
    }
}
