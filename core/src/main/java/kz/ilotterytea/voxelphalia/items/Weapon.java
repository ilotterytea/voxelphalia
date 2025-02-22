package kz.ilotterytea.voxelphalia.items;

import kz.ilotterytea.voxelphalia.utils.Identifier;

public class Weapon extends Item {
    private final float recoilTime, energyCost;
    private final boolean spawnBullet;
    private final int damage;

    public Weapon(Identifier identifier,
                  ItemMaterial material,
                  float recoilTime,
                  float energyCost,
                  int damage,
                  boolean spawnBullet
    ) {
        super(identifier, material);
        this.recoilTime = recoilTime;
        this.energyCost = energyCost;
        this.damage = damage;
        this.spawnBullet = spawnBullet;
    }

    public float getRecoilTime() {
        return recoilTime;
    }

    public float getEnergyCost() {
        return energyCost;
    }

    public int getDamage() {
        return damage;
    }

    public boolean isSpawnBullet() {
        return spawnBullet;
    }
}
