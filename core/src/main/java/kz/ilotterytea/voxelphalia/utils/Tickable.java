package kz.ilotterytea.voxelphalia.utils;

import com.badlogic.gdx.graphics.Camera;
import kz.ilotterytea.voxelphalia.level.Level;

public interface Tickable {
    default void tick(float delta) {
    }

    default void tick(float delta, Camera camera) {
        tick(delta);
    }

    default void tick(float delta, Level level) {
        tick(delta);
    }
}
