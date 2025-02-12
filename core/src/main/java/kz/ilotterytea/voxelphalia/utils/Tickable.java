package kz.ilotterytea.voxelphalia.utils;

import com.badlogic.gdx.graphics.Camera;
import kz.ilotterytea.voxelphalia.level.Level;

public interface Tickable {
    default void tick(float delta) {
    }

    default void tick(float delta, Camera camera) {
    }

    default void tick(float delta, Level level) {
    }

    default void tick(float delta, Level level, Camera camera) {
    }
}
