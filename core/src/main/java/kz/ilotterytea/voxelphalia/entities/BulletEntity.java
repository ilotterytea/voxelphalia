package kz.ilotterytea.voxelphalia.entities;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector3;
import kz.ilotterytea.voxelphalia.VoxelphaliaGame;
import kz.ilotterytea.voxelphalia.items.Weapon;
import kz.ilotterytea.voxelphalia.level.Level;

public class BulletEntity extends RenderableEntity {
    private final Weapon weapon;
    private final Vector3 initialPosition, initialDirection;
    private boolean hit;

    public BulletEntity(Weapon shotByWeapon, Vector3 initialPosition, Vector3 initialDirection) {
        this.weapon = shotByWeapon;
        this.initialDirection = initialDirection;
        this.initialPosition = initialPosition;

        setPosition(initialPosition.x, initialPosition.y, initialPosition.z);

        Texture texture = VoxelphaliaGame.getInstance().getAssetManager()
            .get("textures/entities/bullet.png");

        setDecal(new TextureRegion(texture), 0.1f, 0.1f);
    }

    @Override
    public void tick(float delta, Level level) {
        position.add(initialDirection.cpy().scl(delta * 50f));
        setPosition(position.x, position.y, position.z);

        int x = (int) position.x, y = (int) position.y, z = (int) position.z;

        if (level.hasSolidVoxel(x, y, z)) {
            hit = true;
        } else if (level.hasEntityByHitBox(x, y, z, LivingEntity.class)) {
            if (level.getEntityByHitBox(x, y, z, LivingEntity.class) instanceof LivingEntity e) {
                e.takeDamage(weapon.getDamage());
                hit = true;
            }
        } else if (position.dst(initialPosition) >= 100f) {
            hit = true;
        }
    }

    public boolean isHit() {
        return hit;
    }

    public Weapon getWeapon() {
        return weapon;
    }

    public Vector3 getInitialDirection() {
        return initialDirection;
    }

    public Vector3 getInitialPosition() {
        return initialPosition;
    }
}
