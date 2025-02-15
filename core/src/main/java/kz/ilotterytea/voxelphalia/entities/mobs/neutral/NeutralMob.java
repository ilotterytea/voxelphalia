package kz.ilotterytea.voxelphalia.entities.mobs.neutral;

import kz.ilotterytea.voxelphalia.entities.PlayerEntity;
import kz.ilotterytea.voxelphalia.entities.mobs.MobEntity;

public class NeutralMob extends MobEntity {
    private boolean isHostile;
    private float lastTimeAttack;

    @Override
    public void takeDamage(int damage) {
        super.takeDamage(damage);
        isHostile = true;
        lastTimeAttack = 0;
    }

    @Override
    public void tick(float delta, PlayerEntity playerEntity) {
        super.tick(delta, playerEntity);
        lastTimeAttack += delta;

        if (lastTimeAttack >= 15f) {
            isHostile = false;
        }

        // copied this logic from HostileMob
        if (isHostile) {
            float distance = playerEntity.getPosition().dst(position);

            if (distance < 2f) {
                playerEntity.takeDamage(damage);
            } else if (distance < 15f) {
                followEntity(playerEntity);
            }
        }
    }
}
