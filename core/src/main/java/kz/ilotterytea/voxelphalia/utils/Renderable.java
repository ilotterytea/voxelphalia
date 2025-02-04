package kz.ilotterytea.voxelphalia.utils;

import com.badlogic.gdx.graphics.g3d.Environment;
import com.badlogic.gdx.graphics.g3d.ModelBatch;

public interface Renderable {
    void render(ModelBatch batch, Environment environment);
}
