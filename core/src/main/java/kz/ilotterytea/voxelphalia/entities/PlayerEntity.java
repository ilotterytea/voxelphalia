package kz.ilotterytea.voxelphalia.entities;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector3;
import kz.ilotterytea.voxelphalia.VoxelphaliaGame;
import kz.ilotterytea.voxelphalia.audio.IdentifiedSound;
import kz.ilotterytea.voxelphalia.inventory.Inventory;
import kz.ilotterytea.voxelphalia.level.Level;

public class PlayerEntity extends LivingEntity {
    private final Inventory inventory;
    private final Vector3 spawnPoint;

    private Camera camera;
    private float respawnTime, bobbing, bobbingY, time, recoilDelayTime;
    private float energy, maxEnergy;
    private boolean focused;

    public PlayerEntity(Vector3 spawnPoint, Camera camera) {
        this.camera = camera;
        this.inventory = new Inventory(50, (byte) 100);
        this.spawnPoint = spawnPoint;
        setPosition(spawnPoint.x, spawnPoint.y, spawnPoint.z);

        setSize(0.5f, 1.7f, 0.5f);
        setWeight(10f);
        setSpeed(7f);

        setHealth(60);
        setMaxHealth(60);
        setDamage(1);

        this.maxEnergy = 120;
        this.energy = this.maxEnergy;
    }

    @Override
    public void setPosition(float x, float y, float z) {
        super.setPosition(x, y, z);

        if (camera != null) {
            bobbing = MathUtils.sin(time * velocity.x * 2f) * bobbingY * 0.1f;

            camera.position.set(x, y + height + bobbing, z);
            camera.update();
        }
    }

    @Override
    public void tick(float delta, Level level) {
        super.tick(delta, level);
        time += delta;

        if (focused) {
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

            Preferences prefs = VoxelphaliaGame.getInstance().getPreferences();
            if (prefs.getBoolean("view-bobbing", true)) {
                bobbingY = 1f;
            }
            // auto jump
            if (prefs.getBoolean("auto-jump", true)) {
                Vector3 nextVoxelPos = position.cpy().add(direction);
                nextVoxelPos.y = position.y;

                if ((collidingX || collidingZ) &&
                    !level.hasSolidVoxel((int) nextVoxelPos.x, (int) nextVoxelPos.y + 1, (int) nextVoxelPos.z) &&
                    !level.hasSolidVoxel((int) nextVoxelPos.x, (int) nextVoxelPos.y + 2, (int) nextVoxelPos.z) &&
                    level.hasSolidVoxel((int) nextVoxelPos.x, (int) nextVoxelPos.y, (int) nextVoxelPos.z)
                ) {
                    jump();
                }
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
        setRecoilDelayTime(0f);
    }

    @Override
    public void takeDamage(int damage) {
        super.takeDamage(damage);

        if (dead && respawnTime <= 0f) {
            setFocused(false);
            respawnTime = 10f;
            IdentifiedSound sound = VoxelphaliaGame.getInstance().getSoundRegistry()
                .getEntry("voxelphalia:sfx.player.death");

            sound.getSound().play(MathUtils.clamp(VoxelphaliaGame.getInstance().getPreferences()
                    .getFloat("sfx", 1f),
                0f, 1f
            ));
        }
    }

    @Override
    public void tick(float delta) {
        super.tick(delta);
        respawnTime -= delta;
        recoilDelayTime -= delta;

        this.energy += delta * 5f;

        if (this.energy > this.maxEnergy) {
            this.energy = this.maxEnergy;
        }

        // auto respawn
        if (dead && respawnTime <= 0.1f) {
            setFocused(true);
            respawn();
        } else if (dead && camera.position.y > position.y + 0.2f) {
            camera.position.y -= 2f * delta;
            camera.update();
        }
    }

    public float getRespawnTime() {
        return respawnTime;
    }

    public void setFocused(boolean focused) {
        this.focused = focused;
        Gdx.input.setCursorCatched(focused);
    }

    public boolean isFocused() {
        return focused;
    }

    public float getBobbing() {
        return bobbing;
    }

    public void setRecoilDelayTime(float recoilDelayTime) {
        this.recoilDelayTime = recoilDelayTime;
    }

    public float getRecoilDelayTime() {
        return recoilDelayTime;
    }

    public void takeEnergy(float energy) {
        this.energy = Math.max(this.energy - energy, 0);
    }

    public float getEnergy() {
        return energy;
    }

    public float getMaxEnergy() {
        return maxEnergy;
    }

    public void setEnergy(float energy) {
        this.energy = energy;
    }

    public void setMaxEnergy(float maxEnergy) {
        this.maxEnergy = maxEnergy;
    }

    public Vector3 getSpawnPoint() {
        return spawnPoint;
    }

    public void setCamera(Camera camera) {
        this.camera = camera;
    }
}
