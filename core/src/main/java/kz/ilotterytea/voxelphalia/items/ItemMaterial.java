package kz.ilotterytea.voxelphalia.items;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;

public class ItemMaterial {
    private Vector2 region;

    public ItemMaterial() {
    }

    public void setRegion(Vector2 region) {
        this.region = region;
    }

    public TextureRegion getTextureRegion(Texture texture) {
        return new TextureRegion(
            texture,
            (int) region.x * 16, (int) region.y * 16,
            16, 16
        );
    }
}
