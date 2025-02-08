package kz.ilotterytea.voxelphalia.entities;

import com.badlogic.gdx.math.Vector3;
import kz.ilotterytea.voxelphalia.utils.Tickable;

public abstract class Entity implements Tickable {
    protected final Vector3 position, direction;

    public Entity() {
        this(new Vector3(), new Vector3());
    }

    public Entity(Vector3 position, Vector3 direction) {
        this.position = position;
        this.direction = direction;
    }

    public Vector3 getPosition() {
        return position;
    }

    public void setPosition(float x, float y, float z) {
        this.position.set(x, y, z);
    }

    public Vector3 getDirection() {
        return direction;
    }

    public void setDirection(float x, float y, float z) {
        this.direction.set(x, y, z);
    }
}
