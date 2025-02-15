package kz.ilotterytea.voxelphalia.entities;

import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.collision.BoundingBox;

public class LivingEntity extends RenderablePhysicalEntity {
    protected final BoundingBox hitBox;
    protected int health, maxHealth, damage;
    protected boolean dead;

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
        health -= damage;
        if (health <= 0) {
            health = 0;
            dead = true;
        }
    }

    public int getHealth() {
        return health;
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

    public BoundingBox getHitBox() {
        return hitBox;
    }
}
