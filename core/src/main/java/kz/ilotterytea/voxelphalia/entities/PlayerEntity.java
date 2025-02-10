package kz.ilotterytea.voxelphalia.entities;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.math.Vector3;
import kz.ilotterytea.voxelphalia.inventory.Inventory;
import kz.ilotterytea.voxelphalia.level.Level;
import kz.ilotterytea.voxelphalia.level.VoxelType;

public class PlayerEntity extends RenderablePhysicalEntity {
    private final Inventory inventory;
    private final Camera camera;
    private final float cameraRotateSpeed;

    private int dragX, dragY;

    public PlayerEntity(Camera camera) {
        super(null, 0, 0,
            0.5f, 1.9f, 0.5f, 10f, 7f
        );
        this.camera = camera;
        this.cameraRotateSpeed = 0.2f;
        this.inventory = new Inventory(5, (byte) 100);

        for (VoxelType type : VoxelType.values()) {
            if (type.getVoxelId() == 0) continue;
            inventory.add(type.getVoxelId(), (byte) 5);
        }
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
        if (Gdx.input.isKeyPressed(Input.Keys.SPACE)) {
            jump();
        }

        processMovement(delta, level);
        processCameraLook();
        processBlockManipulation(level);
        processInventory();
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

        Vector3 oldPitchAxis = camera.direction.cpy().crs(camera.up).nor();
        Vector3 newDirection = camera.direction.cpy().rotate(oldPitchAxis, (dragY - screenY) * cameraRotateSpeed);
        Vector3 newPitchAxis = newDirection.cpy().crs(camera.up);

        if (!newPitchAxis.hasOppositeDirection(oldPitchAxis)) {
            camera.direction.set(newDirection);
        }

        camera.update();
        dragX = screenX;
        dragY = screenY;

        setDirection(camera.direction.x, camera.direction.y, camera.direction.z);
    }

    private void processBlockManipulation(Level level) {
        boolean destroy = Gdx.input.isButtonJustPressed(Input.Buttons.LEFT);
        boolean place = Gdx.input.isButtonJustPressed(Input.Buttons.RIGHT);

        if (!destroy && !place) {
            return;
        }

        Vector3 pos = new Vector3(position);
        Vector3 dir = new Vector3(direction);
        boolean collided = false;

        if (destroy) {
            for (float d = 0; d <= 5f; d += 0.1f) {
                pos.set(position).mulAdd(dir, d).add(0f, height, 0f);

                if (level.hasSolidVoxel((int) Math.floor(pos.x), (int) Math.floor(pos.y), (int) Math.floor(pos.z))) {
                    collided = true;
                    break;
                }
            }
        } else {
            boolean foundSolidVoxel = false;

            Vector3 lastPos = new Vector3();

            for (float d = 5f; d > 1; d -= 0.1f) {
                pos.set(position).mulAdd(dir, d).add(0f, height, 0f);

                if (foundSolidVoxel) lastPos.set(pos);

                foundSolidVoxel = level.hasSolidVoxel((int) Math.floor(pos.x), (int) Math.floor(pos.y), (int) Math.floor(pos.z));
            }

            collided = !lastPos.isZero();
            pos.set(lastPos);
        }

        if (collided) {
            byte voxel = 0;
            int x = (int) Math.floor(pos.x),
                y = (int) Math.floor(pos.y), z = (int) Math.floor(pos.z);

            if (place) {
                Inventory.Slot slot = inventory.getCurrentSlot();
                voxel = slot.id;
                if (slot.remove() > 0) return;
            } else {
                inventory.add(level.getVoxel(x, y, z));
            }

            level.placeVoxel(voxel, x, y, z);
        }
    }

    private void processInventory() {
        for (int i = 0; i < inventory.getSize(); i++) {
            if (Gdx.input.isKeyPressed(Input.Keys.NUM_1 + i)) {
                inventory.setSlotIndex(i + 1);
                break;
            }
        }
    }
}
