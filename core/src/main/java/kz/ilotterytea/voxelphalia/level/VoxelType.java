package kz.ilotterytea.voxelphalia.level;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

public enum VoxelType {
    AIR(-1, -1),
    GRASS(0, 0),
    ROCK(1, 0);

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
        return values()[voxel];
    }

    public byte getVoxelId() {
        return (byte) ordinal();
    }
}
