package kz.ilotterytea.voxelphalia.entities.mobs.hostile;

import kz.ilotterytea.voxelphalia.entities.PlayerEntity;
import kz.ilotterytea.voxelphalia.entities.mobs.MobEntity;

public class HostileMob extends MobEntity {
    @Override
    public void tick(float delta, PlayerEntity playerEntity) {
        float distance = playerEntity.getPosition().dst(position);

        if (distance < 2f) {
            playerEntity.takeDamage(damage);
        } else if (distance < 15f) {
            followEntity(playerEntity);
        }
    }
}
