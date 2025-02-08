package kz.ilotterytea.voxelphalia.entities;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.math.Vector3;
import kz.ilotterytea.voxelphalia.level.Level;

public class PlayerEntity extends RenderablePhysicalEntity {
    private final Camera camera;
    private final float cameraRotateSpeed;

    private int dragX, dragY;

    public PlayerEntity(Camera camera) {
        super(null, 0, 0);
        this.camera = camera;
        this.cameraRotateSpeed = 0.2f;
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
        processMovement(delta, level);
        processCameraLook();
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

    private void processCameraLook() {
        int screenX = Gdx.input.getX();
        int screenY = Gdx.input.getY();

        float x = dragX - screenX;
        this.camera.rotate(Vector3.Y, x * cameraRotateSpeed);

        float y = (float) Math.sin((double) (dragY - screenY) / 180f);
        if (Math.abs(camera.direction.y + y * (cameraRotateSpeed * 5.0f)) < 0.9f) {
            camera.direction.y += y * (cameraRotateSpeed * 5.0f);
        }

        camera.update();
        dragX = screenX;
        dragY = screenY;

        setDirection(camera.direction.x, camera.direction.y, camera.direction.z);
    }
}
