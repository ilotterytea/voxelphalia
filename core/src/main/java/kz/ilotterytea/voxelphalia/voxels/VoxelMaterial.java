package kz.ilotterytea.voxelphalia.voxels;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;

public class VoxelMaterial implements Cloneable {
    private Vector2 top, bottom, left, right, back, front;
    private boolean translucent, stateSave;

    public VoxelMaterial() {
        this(new Vector2());
    }

    public VoxelMaterial(Vector2 side) {
        this.top = side;
        this.bottom = side;
        this.left = side;
        this.right = side;
        this.back = side;
        this.front = side;
    }

    public VoxelMaterial(Vector2 side, Vector2 top) {
        this.top = top;
        this.bottom = top;
        this.left = side;
        this.right = side;
        this.back = side;
        this.front = side;
    }

    public VoxelMaterial(Vector2 side, Vector2 top, Vector2 bottom) {
        this.top = top;
        this.bottom = bottom;
        this.left = side;
        this.right = side;
        this.back = side;
        this.front = side;
    }

    public VoxelMaterial(Vector2 side, Vector2 top, Vector2 bottom, Vector2 front) {
        this.top = top;
        this.bottom = bottom;
        this.left = side;
        this.right = side;
        this.back = side;
        this.front = front;
    }

    public VoxelMaterial(Vector2 top, Vector2 bottom, Vector2 left, Vector2 right, Vector2 back, Vector2 front) {
        this.top = top;
        this.bottom = bottom;
        this.left = left;
        this.right = right;
        this.back = back;
        this.front = front;
    }

    public TextureRegion getTopTextureRegion(Texture texture) {
        return new TextureRegion(
            texture,
            (int) top.x * 16, (int) top.y * 16,
            16, 16
        );
    }

    public TextureRegion getBottomTextureRegion(Texture texture) {
        return new TextureRegion(
            texture,
            (int) bottom.x * 16, (int) bottom.y * 16,
            16, 16
        );
    }

    public TextureRegion getLeftTextureRegion(Texture texture) {
        return new TextureRegion(
            texture,
            (int) left.x * 16, (int) left.y * 16,
            16, 16
        );
    }

    public TextureRegion getRightTextureRegion(Texture texture) {
        return new TextureRegion(
            texture,
            (int) right.x * 16, (int) right.y * 16,
            16, 16
        );
    }

    public TextureRegion getBackTextureRegion(Texture texture) {
        return new TextureRegion(
            texture,
            (int) back.x * 16, (int) back.y * 16,
            16, 16
        );
    }

    public TextureRegion getFrontTextureRegion(Texture texture) {
        return new TextureRegion(
            texture,
            (int) front.x * 16, (int) front.y * 16,
            16, 16
        );
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
            clone.top = new Vector2(top.x, top.y);
            clone.bottom = new Vector2(bottom.x, bottom.y);
            clone.left = new Vector2(left.x, left.y);
            clone.right = new Vector2(right.x, right.y);
            clone.back = new Vector2(back.x, back.y);
            clone.front = new Vector2(front.x, front.y);
            clone.translucent = translucent;
            clone.stateSave = stateSave;
            return clone;
        } catch (CloneNotSupportedException e) {
            throw new RuntimeException(e);
        }
    }
}
