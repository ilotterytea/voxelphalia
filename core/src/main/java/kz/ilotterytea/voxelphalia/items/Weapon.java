package kz.ilotterytea.voxelphalia.items;

import kz.ilotterytea.voxelphalia.utils.Identifier;

public class Weapon extends Item {
    private final float recoilTime, energyCost, bulletSpread;
    private final boolean spawnBullet;
    private final int damage, bulletMinAmount;

    public Weapon(Identifier identifier,
                  ItemMaterial material,
                  float recoilTime,
                  float energyCost,
                  int damage,
                  int bulletMinAmount,
                  float bulletSpread,
                  boolean spawnBullet
    ) {
        super(identifier, material);
        this.recoilTime = recoilTime;
        this.energyCost = energyCost;
        this.damage = damage;
        this.spawnBullet = spawnBullet;
        this.bulletSpread = bulletSpread;
        this.bulletMinAmount = bulletMinAmount;
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

    public int getBulletMinAmount() {
        return bulletMinAmount;
    }

    public float getBulletSpread() {
        return bulletSpread;
    }

    public boolean isSpawnBullet() {
        return spawnBullet;
    }
}
