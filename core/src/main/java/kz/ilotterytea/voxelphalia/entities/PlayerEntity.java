package kz.ilotterytea.voxelphalia.entities;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Camera;
import kz.ilotterytea.voxelphalia.inventory.Inventory;
import kz.ilotterytea.voxelphalia.level.Level;

public class PlayerEntity extends RenderablePhysicalEntity {
    private final Inventory inventory;
    private final Camera camera;

    public PlayerEntity(Camera camera) {
        this.camera = camera;
        this.inventory = new Inventory(5, (byte) 100);
        setSize(0.5f, 1.9f, 0.5f);
        setWeight(10f);
        setSpeed(7f);
    }

    @Override
    public void setPosition(float x, float y, float z) {
        super.setPosition(x, y, z);
        camera.position.set(x, y + height, z);
        camera.update();
    }

    @Override
    public void tick(float delta, Level level) {
        super.tick(delta, level);

        if (Gdx.input.isCursorCatched()) {
            processMovement(delta, level);
        }
    }

    // cv pasted these two methods from https://stackoverflow.com/a/34058580
    private void processMovement(float deltaTime, Level level) {
        boolean forward = Gdx.input.isKeyPressed(Input.Keys.W);
        boolean back = Gdx.input.isKeyPressed(Input.Keys.S);
        boolean left = Gdx.input.isKeyPressed(Input.Keys.A);
        boolean right = Gdx.input.isKeyPressed(Input.Keys.D);

        float speed = this.velocity.x;
        if ((forward | back) & (right | left) && !onGround) {
            speed /= (float) Math.sqrt(2);
        }

        if (forward) this.moveForward(speed, deltaTime, level);
        if (back) this.moveBackward(speed, deltaTime, level);
        if (left) this.moveLeft(speed, deltaTime, level);
        if (right) this.moveRight(speed, deltaTime, level);

        if (forward || back || left || right) {
            setPosition(position.x, position.y, position.z);
        }
    }

    public Inventory getInventory() {
        return inventory;
    }
}
