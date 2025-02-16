package kz.ilotterytea.voxelphalia.entities;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector3;
import kz.ilotterytea.voxelphalia.VoxelphaliaGame;
import kz.ilotterytea.voxelphalia.inventory.Inventory;
import kz.ilotterytea.voxelphalia.level.Level;

public class PlayerEntity extends LivingEntity {
    private final Inventory inventory;
    private final Camera camera;
    private final Vector3 spawnPoint;

    private float respawnTime, bobbingY, time;

    public PlayerEntity(Vector3 spawnPoint, Camera camera) {
        this.camera = camera;
        this.inventory = new Inventory(50, (byte) 100);
        this.spawnPoint = spawnPoint;
        setPosition(spawnPoint.x, spawnPoint.y, spawnPoint.z);

        setSize(0.5f, 1.7f, 0.5f);
        setWeight(10f);
        setSpeed(7f);

        setHealth(20);
        setMaxHealth(20);
        setDamage(1);
    }

    @Override
    public void setPosition(float x, float y, float z) {
        if (dead) return;
        super.setPosition(x, y, z);

        float bobbing = MathUtils.sin(time * velocity.x * 2f) * bobbingY * 0.1f;

        camera.position.set(x, y + height + bobbing, z);
        camera.update();
    }

    @Override
    public void tick(float delta, Level level) {
        super.tick(delta, level);
        time += delta;

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
            if (VoxelphaliaGame.getInstance().getPreferences().getBoolean("view-bobbing", true)) {
                bobbingY = 1f;
            }
        } else {
            bobbingY = 0f;
        }
    }

    public Inventory getInventory() {
        return inventory;
    }

    public void respawn() {
        dead = false;
        inventory.clear();
        setPosition(spawnPoint.x, spawnPoint.y, spawnPoint.z);
        setDirection(0f, 0f, 0f);
        setHealth(maxHealth);
    }

    @Override
    public void takeDamage(int damage) {
        super.takeDamage(damage);

        if (dead && respawnTime <= 0f) {
            Gdx.input.setCursorCatched(false);
            respawnTime = 10f;
        }
    }

    @Override
    public void tick(float delta) {
        super.tick(delta);
        respawnTime -= delta;

        // auto respawn
        if (dead && respawnTime <= 0.1f) {
            respawn();
        } else if (dead && camera.position.y > position.y + 0.2f) {
            camera.position.y -= 2f * delta;
            camera.update();
        }
    }

    public float getRespawnTime() {
        return respawnTime;
    }
}
