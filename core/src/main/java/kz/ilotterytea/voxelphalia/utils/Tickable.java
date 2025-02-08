package kz.ilotterytea.voxelphalia.utils;

import com.badlogic.gdx.graphics.Camera;

public interface Tickable {
    default void tick(float delta) {
    }

    default void tick(float delta, Camera camera) {
        tick(delta);
    }
}
