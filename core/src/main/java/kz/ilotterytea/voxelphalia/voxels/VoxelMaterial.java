package kz.ilotterytea.voxelphalia.voxels;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

public class VoxelMaterial implements Cloneable {
    private int topX, topY, sideX, sideY, bottomX, bottomY;
    private boolean translucent, stateSave;

    public VoxelMaterial(int sideX, int sideY) {
        this.topX = sideX;
        this.topY = sideY;
        this.sideX = sideX;
        this.sideY = sideY;
        this.bottomX = sideX;
        this.bottomY = sideY;
    }

    public VoxelMaterial(int sideX, int sideY, int topX, int topY) {
        this.topX = topX;
        this.topY = topY;
        this.sideX = sideX;
        this.sideY = sideY;
        this.bottomX = topX;
        this.bottomY = topY;
    }

    public VoxelMaterial(int sideX, int sideY, int topX, int topY, int bottomX, int bottomY) {
        this.topX = topX;
        this.topY = topY;
        this.sideX = sideX;
        this.sideY = sideY;
        this.bottomX = bottomX;
        this.bottomY = bottomY;
    }

    public TextureRegion getSideTextureRegion(Texture texture) {
        return new TextureRegion(
            texture,
            sideX * 16, sideY * 16,
            16, 16
        );
    }

    public TextureRegion getTopTextureRegion(Texture texture) {
        return new TextureRegion(
            texture,
            topX * 16, topY * 16,
            16, 16
        );
    }

    public TextureRegion getBottomTextureRegion(Texture texture) {
        return new TextureRegion(
            texture,
            bottomX * 16, bottomY * 16,
            16, 16
        );
    }

    public int getTopX() {
        return topX;
    }

    public int getTopY() {
        return topY;
    }

    public int getSideX() {
        return sideX;
    }

    public int getSideY() {
        return sideY;
    }

    public int getBottomX() {
        return bottomX;
    }

    public int getBottomY() {
        return bottomY;
    }

    public void setTranslucent(boolean translucent) {
        this.translucent = translucent;
    }

    public boolean isTranslucent() {
        return translucent;
    }

    public void setStateSave(boolean stateSave) {
        this.stateSave = stateSave;
    }

    public boolean isStateSave() {
        return stateSave;
    }

    @Override
    public VoxelMaterial clone() {
        try {
            VoxelMaterial clone = (VoxelMaterial) super.clone();
            clone.topX = topX;
            clone.topY = topY;
            clone.sideX = sideX;
            clone.sideY = sideY;
            clone.bottomX = bottomX;
            clone.bottomY = bottomY;
            clone.translucent = translucent;
            clone.stateSave = stateSave;
            return clone;
        } catch (CloneNotSupportedException e) {
            throw new RuntimeException(e);
        }
    }
}
