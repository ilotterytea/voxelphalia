package kz.ilotterytea.voxelphalia.level;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

public enum VoxelType {
    AIR(-1, -1),
    GRASS(0, 0),
    ROCK(1, 0),
    DIRT(2, 0),
    SAND(3, 0),
    MANTLE(4, 0),
    SNOW(5, 0),
    WATER(0, 15),
    MISSING_VOXEL(15, 15);

    private final int offsetX, offsetY;

    VoxelType(int offsetX, int offsetY) {
        this.offsetX = offsetX;
        this.offsetY = offsetY;
    }

    public TextureRegion getTextureRegion(Texture texture) {
        return new TextureRegion(
            texture,
            offsetX * 16, offsetY * 16,
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
