package kz.ilotterytea.voxelphalia.utils;

import com.badlogic.gdx.graphics.g3d.Environment;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.graphics.g3d.decals.DecalBatch;

public interface Renderable {
    default void render(ModelBatch batch, Environment environment) {
    }

    default void render(DecalBatch batch) {
    }
}
