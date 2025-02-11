package kz.ilotterytea.voxelphalia.level;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

public enum VoxelType {
    AIR(-1, -1),
    GRASS(0, 0),
    STONE(1, 0),
    DIRT(2, 0),
    SAND(3, 0),
    MANTLE(4, 0),
    SNOW(5, 0),
    WATER(0, 15),
    MISSING_VOXEL(15, 15),
    LOG(6, 0, 7, 0, 7, 0),
    LEAVES(8, 0),
    COBBLESTONE(0, 1),
    COAL_VOXEL(0, 2),
    IRON_VOXEL(1, 2),
    GOLD_VOXEL(2, 2),
    GEM_VOXEL(0, 3),
    RUBY_VOXEL(1, 3);

    private final int sx, sy,
        tx, ty, bx, by;

    VoxelType(int sx, int sy) {
        this.sx = sx;
        this.sy = sy;
        this.tx = sx;
        this.ty = sy;
        this.bx = sx;
        this.by = sy;
    }

    VoxelType(int sx, int sy, int tx, int ty) {
        this.sx = sx;
        this.sy = sy;
        this.tx = tx;
        this.ty = ty;
        this.bx = sx;
        this.by = sy;
    }

    VoxelType(int sx, int sy, int tx, int ty, int bx, int by) {
        this.sx = sx;
        this.sy = sy;
        this.tx = tx;
        this.ty = ty;
        this.bx = bx;
        this.by = by;
    }

    public TextureRegion getSideTextureRegion(Texture texture) {
        return new TextureRegion(
            texture,
            sx * 16, sy * 16,
            16, 16
        );
    }

    public TextureRegion getTopTextureRegion(Texture texture) {
        return new TextureRegion(
            texture,
            tx * 16, ty * 16,
            16, 16
        );
    }

    public TextureRegion getBottomTextureRegion(Texture texture) {
        return new TextureRegion(
            texture,
            bx * 16, by * 16,
            16, 16
        );
    }

    public static VoxelType getById(byte voxel) {
        if (voxel >= values().length) {
            return VoxelType.MISSING_VOXEL;
        }

        return values()[voxel];
    }

    public byte getVoxelId() {
        return (byte) ordinal();
    }
}
