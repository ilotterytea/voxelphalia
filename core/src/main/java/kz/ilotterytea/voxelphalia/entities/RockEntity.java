package kz.ilotterytea.voxelphalia.entities;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector3;
import kz.ilotterytea.voxelphalia.VoxelphaliaGame;

import java.util.Random;

public class RockEntity extends RenderableEntity {
    public RockEntity() {
        this(new Vector3());
    }

    public RockEntity(Vector3 position) {
        super(new TextureRegion(
                VoxelphaliaGame.getInstance()
                    .getAssetManager().get("textures/entities/rock.png", Texture.class),
                128 * new Random().nextInt(0, 2),
                128 * new Random().nextInt(0, 2),
                128, 128
            ),
            2, 2,
            position
        );
    }
}
