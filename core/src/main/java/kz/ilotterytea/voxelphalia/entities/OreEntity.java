package kz.ilotterytea.voxelphalia.entities;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector3;
import kz.ilotterytea.voxelphalia.VoxelphaliaGame;

public class OreEntity extends RenderableEntity {
    public OreEntity(int type) {
        this(type, new Vector3());
    }

    public OreEntity(int type, Vector3 position) {
        super(new TextureRegion(
                VoxelphaliaGame.getInstance()
                    .getAssetManager().get("textures/entities/ore.png", Texture.class),
                128 * type, 0,
                128, 128
            ),
            2, 2,
            position
        );
    }
}
