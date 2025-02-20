package kz.ilotterytea.voxelphalia.entities;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.collision.BoundingBox;
import kz.ilotterytea.voxelphalia.VoxelphaliaGame;
import kz.ilotterytea.voxelphalia.audio.IdentifiedSound;
import kz.ilotterytea.voxelphalia.level.Level;
import kz.ilotterytea.voxelphalia.screens.GameScreen;
import kz.ilotterytea.voxelphalia.voxels.Voxel;

public class LivingEntity extends RenderablePhysicalEntity {
    protected final BoundingBox hitBox;
    protected int health, maxHealth, damage;
    protected boolean dead;

    protected float damageInDelay, lastFootstepTime;

    public LivingEntity() {
        this.hitBox = new BoundingBox();
    }

    protected void setHealth(int health) {
        this.health = health;
    }

    protected void setMaxHealth(int maxHealth) {
        this.maxHealth = maxHealth;
    }

    public void takeDamage(int damage) {
        if (damageInDelay > 0f || dead) return;

        health -= damage;
        if (health <= 0) {
            health = 0;
            dead = true;
        }

        damageInDelay = 0.25f;

        if (decal != null) {
            new Thread(() -> {
                decal.setColor(Color.SALMON);
                try {
                    Thread.sleep(250);
                } catch (InterruptedException ignored) {
                }
                decal.setColor(Color.WHITE);
            }).start();
        }

        if (VoxelphaliaGame.getInstance().getScreen() instanceof GameScreen g) {
            Vector3 playerPosition = g.getPlayerEntity().getPosition();

            float volume = playerPosition.dst(position);
            if (volume >= 10f) return;
            volume /= 10f;

            IdentifiedSound sound = VoxelphaliaGame.getInstance().getSoundRegistry()
                .getEntry(dead ? "voxelphalia:sfx.hit.disassemble" : "voxelphalia:sfx.hit.stab");

            sound.getSound().play(MathUtils.clamp(VoxelphaliaGame.getInstance().getPreferences()
                    .getFloat("sfx", 1f) - volume,
                0f, 1f
            ));
        }
    }

    public int getHealth() {
        return health;
    }

    public int getMaxHealth() {
        return maxHealth;
    }

    protected void setDamage(int damage) {
        this.damage = damage;
    }

    public int getDamage() {
        return damage;
    }

    public boolean isDead() {
        return dead;
    }

    @Override
    protected void setSize(float width, float height, float depth) {
        super.setSize(width, height, depth);

        float w = width * 2f, h = height * 2f, d = depth * 2f;

        this.hitBox.set(
            new Vector3(position.x - (w / 2f), position.y, position.z - (d / 2f)),
            new Vector3(position.x + (w / 2f), position.y + h, position.z + (d / 2f))
        );
    }

    @Override
    public void tick(float delta) {
        super.tick(delta);
        damageInDelay -= delta;
        lastFootstepTime += delta;
    }

    public BoundingBox getHitBox() {
        return hitBox;
    }

    @Override
    protected void moveForward(float speed, float deltaTime, Level level) {
        super.moveForward(speed, deltaTime, level);
        playFootstepSound(speed, deltaTime, level);
    }

    @Override
    protected void moveBackward(float speed, float deltaTime, Level level) {
        super.moveBackward(speed, deltaTime, level);
        playFootstepSound(speed, deltaTime, level);
    }

    @Override
    protected void moveLeft(float speed, float deltaTime, Level level) {
        super.moveLeft(speed, deltaTime, level);
        playFootstepSound(speed, deltaTime, level);
    }

    @Override
    protected void moveRight(float speed, float deltaTime, Level level) {
        super.moveRight(speed, deltaTime, level);
        playFootstepSound(speed, deltaTime, level);
    }

    @Override
    public void jump() {
        if (VoxelphaliaGame.getInstance().getScreen() instanceof GameScreen g) {
            if (onGround) {
                playFootstepSound(velocity.x, Gdx.graphics.getDeltaTime(), g.getLevel());
            }
        }
        super.jump();
    }

    private void playFootstepSound(float speed, float delta, Level level) {
        if (lastFootstepTime < speed * delta * 3f || !onGround) return;

        VoxelphaliaGame game = VoxelphaliaGame.getInstance();

        if (game.getScreen() instanceof GameScreen g) {
            Vector3 playerPosition = g.getPlayerEntity().getPosition();

            float volume = playerPosition.dst(position);
            if (volume >= 10f) return;
            volume /= 10f;

            Voxel voxel = game.getVoxelRegistry()
                .getEntry(level.getVoxel((int) position.x, (int) position.y - 1, (int) position.z));

            if (voxel == null) return;

            String footstepName = switch (voxel.getMaterial().getType()) {
                case STONE -> "stone";
                case LOG -> "log";
                case DIRT -> "dirt";
                case SAND -> "sand";
                case LEAVES -> "leaves";
                case LIQUID -> "liquid";
                case null -> null;
                default -> "grass";
            };

            if (footstepName == null) return;

            IdentifiedSound sound = game.getSoundRegistry()
                .getEntry("voxelphalia:sfx.footsteps." + footstepName);

            sound.getSound().play(MathUtils.clamp(game.getPreferences()
                    .getFloat("sfx", 1f) - volume,
                0f, 1f
            ));
            lastFootstepTime = 0f;
        }
    }
}
