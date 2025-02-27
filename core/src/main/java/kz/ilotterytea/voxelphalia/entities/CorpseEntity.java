package kz.ilotterytea.voxelphalia.entities;

import com.badlogic.gdx.graphics.Color;
import kz.ilotterytea.voxelphalia.VoxelphaliaGame;

public class CorpseEntity extends RenderablePhysicalEntity {
    private float angleZ, time, size;
    private boolean dead;

    public CorpseEntity(LivingEntity entity) {
        super();
        setDecal(entity.getTextureRegion(), 1f, 1f);
        setPosition(entity.position.x, entity.position.y, entity.position.z);
        this.size = 1f;
    }

    @Override
    public void tick(float delta) {
        super.tick(delta);

        if (VoxelphaliaGame.getInstance().getPreferences()
            .getBoolean("violent-content", true)) {
            size -= 3f * delta;
            position.y += 2f * delta;
            setDecal(decal.getTextureRegion(), size, size);
            setColor(Color.RED);
        } else {
            position.y += 2f * delta;
            setColor(decal.getColor().add(0f, 0f, 0f, -0.01f));

            angleZ = Math.min(angleZ + delta * 40f, 90f);
            decal.rotateZ(angleZ);
        }

        if ((time += delta) >= 5f || size <= 0f) {
            dead = true;
        }
    }

    public boolean isDead() {
        return dead;
    }
}
