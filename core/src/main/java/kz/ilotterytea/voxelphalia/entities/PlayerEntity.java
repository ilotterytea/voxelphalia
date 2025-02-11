package kz.ilotterytea.voxelphalia.entities;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.math.Vector3;
import kz.ilotterytea.voxelphalia.inventory.Inventory;
import kz.ilotterytea.voxelphalia.level.Level;

public class PlayerEntity extends RenderablePhysicalEntity {
    private final Inventory inventory;
    private final Camera camera;

    public PlayerEntity(Camera camera) {
        super(null, 0, 0,
            0.5f, 1.9f, 0.5f, 10f, 7f
        );
        this.camera = camera;
        this.inventory = new Inventory(5, (byte) 100);
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
            processBlockManipulation(level);
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
                if (inventory.remove(voxel) > 0 || voxel == 0) return;
            } else if (inventory.add(level.getVoxel(x, y, z)) > 0) {
                // TODO: drop a voxel instead of just blocking the action
                return;
            }

            level.placeVoxel(voxel, x, y, z);
        }
    }

    public Inventory getInventory() {
        return inventory;
    }
}
