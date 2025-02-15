package kz.ilotterytea.voxelphalia.entities.mobs;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector3;
import kz.ilotterytea.voxelphalia.entities.Entity;
import kz.ilotterytea.voxelphalia.entities.LivingEntity;
import kz.ilotterytea.voxelphalia.level.Level;

public class MobEntity extends LivingEntity {
    protected boolean walking, idling;
    protected float walkingTime, idleTime;

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

        setSize(width, height, depth);
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
        Vector3 pos = new Vector3(entity.getPosition());
        pos.sub(position).nor();
        pos.y = 0;

        setDirection(pos.x, pos.y, pos.z);

        walkingTime = 1f;
        idleTime = 0f;
    }

    @Override
    public void takeDamage(int damage) {
        super.takeDamage(damage);
        jump();
    }
}
