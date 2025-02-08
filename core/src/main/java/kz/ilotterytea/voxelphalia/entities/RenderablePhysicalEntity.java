package kz.ilotterytea.voxelphalia.entities;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.collision.BoundingBox;
import kz.ilotterytea.voxelphalia.level.Level;

public class RenderablePhysicalEntity extends RenderableEntity {
    protected final Vector2 velocity;
    protected final BoundingBox box;
    protected final float width, height, depth, weight;
    protected boolean onGround;

    public RenderablePhysicalEntity(TextureRegion region, float width, float height) {
        this(region, width, height, new Vector3());
    }

    public RenderablePhysicalEntity(TextureRegion region, float width, float height, Vector3 position) {
        super(region, width, height, position);

        this.velocity = new Vector2(8f, 0f);
        this.width = 0.5f;
        this.height = 1.68f;
        this.depth = 0.5f;
        this.weight = 8f;

        this.box = new BoundingBox(
            new Vector3(position.x - (width / 2f), position.y, position.z - (depth / 2f)),
            new Vector3(position.x + (width / 2f), position.y + height, position.z + (depth / 2f))
        );
    }

    @Override
    public void setPosition(float x, float y, float z) {
        super.setPosition(x, y, z);
        this.box.set(
            new Vector3(position.x - (width / 2f), position.y, position.z - (depth / 2f)),
            new Vector3(position.x + (width / 2f), position.y + height, position.z + (depth / 2f))
        );
    }

    @Override
    public void tick(float delta, Level level) {
        super.tick(delta, level);

        if (Gdx.input.isKeyJustPressed(Input.Keys.SPACE) && onGround) {
            velocity.y = weight * 1.3f;
            onGround = false;
        }

        // applying gravity
        float gravity = -9.81f;
        float fallSpeed = gravity * delta * weight * 0.6f;

        velocity.y += fallSpeed;

        float nextY = position.y + velocity.y * delta;
        BoundingBox nextBox = new BoundingBox(
            new Vector3(box.min.x, nextY, box.min.z),
            new Vector3(box.max.x, nextY + height, box.max.z)
        );

        if (isColliding(nextBox, level)) {
            if (velocity.y < 0) {
                onGround = true;
                velocity.y = 0;
                nextY = (float) Math.floor(position.y + 0.5f);
            }
        } else {
            onGround = false;
        }

        setPosition(position.x, nextY, position.z);
    }

    // cv pasted these four functions from https://stackoverflow.com/a/34058580
    protected void moveForward(float speed, float deltaTime, Level level) {
        Vector3 v = direction.cpy();
        v.y = 0f;
        v.x *= speed * deltaTime;
        v.z *= speed * deltaTime;
        collideVector(v, level);
        setPosition(position.x + v.x, position.y + v.y, position.z + v.z);
    }

    protected void moveBackward(float speed, float deltaTime, Level level) {
        Vector3 v = direction.cpy();
        v.y = 0f;
        v.x = -v.x;
        v.z = -v.z;
        v.x *= speed * deltaTime;
        v.z *= speed * deltaTime;
        collideVector(v, level);
        setPosition(position.x + v.x, position.y + v.y, position.z + v.z);
    }

    protected void moveLeft(float speed, float deltaTime, Level level) {
        Vector3 v = direction.cpy();
        v.y = 0f;
        v.rotate(Vector3.Y, 90);
        v.x *= speed * deltaTime;
        v.z *= speed * deltaTime;
        collideVector(v, level);
        setPosition(position.x + v.x, position.y + v.y, position.z + v.z);
    }

    protected void moveRight(float speed, float deltaTime, Level level) {
        Vector3 v = direction.cpy();
        v.y = 0f;
        v.rotate(Vector3.Y, -90);
        v.x *= speed * deltaTime;
        v.z *= speed * deltaTime;
        collideVector(v, level);
        setPosition(position.x + v.x, position.y + v.y, position.z + v.z);
    }

    protected boolean isColliding(BoundingBox box, Level level) {
        for (int x = (int) Math.floor(box.min.x); x <= Math.floor(box.max.x); x++) {
            for (int y = (int) Math.floor(box.min.y); y <= Math.floor(box.max.y); y++) {
                for (int z = (int) Math.floor(box.min.z); z <= Math.floor(box.max.z); z++) {
                    if (level.hasSolidVoxel(x, y, z)) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    private void collideVector(Vector3 v, Level level) {
        BoundingBox nextBoxX = new BoundingBox(
            new Vector3(position.x + v.x - (width / 2f), position.y, position.z - (depth / 2f)),
            new Vector3(position.x + v.x + (width / 2f), position.y + height, position.z + (depth / 2f))
        );

        BoundingBox nextBoxZ = new BoundingBox(
            new Vector3(position.x - (width / 2f), position.y, position.z + v.z - (depth / 2f)),
            new Vector3(position.x + (width / 2f), position.y + height, position.z + v.z + (depth / 2f))
        );

        if (isColliding(nextBoxX, level)) {
            v.x = 0;
        }

        if (isColliding(nextBoxZ, level)) {
            v.z = 0;
        }
    }
}
