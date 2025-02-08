package kz.ilotterytea.voxelphalia.entities;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector3;
import kz.ilotterytea.voxelphalia.VoxelphaliaGame;

import java.util.Random;

public class TreeEntity extends RenderableEntity {
    public TreeEntity() {
        this(new Vector3());
    }

    public TreeEntity(Vector3 position) {
        super(new TextureRegion(
                VoxelphaliaGame.getInstance()
                    .getAssetManager().get("textures/entities/tree.png", Texture.class),
                64 * new Random().nextInt(0, 2),
                128 * new Random().nextInt(0, 2),
                64, 128
            ),
            2, 4,
            position
        );
    }

    @Override
    public void tick(float delta) {

    }
}
