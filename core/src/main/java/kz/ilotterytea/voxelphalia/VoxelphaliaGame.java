package kz.ilotterytea.voxelphalia;

import com.badlogic.gdx.Game;
import kz.ilotterytea.voxelphalia.screens.GameScreen;

/**
 * {@link com.badlogic.gdx.ApplicationListener} implementation shared by all platforms.
 */
public class VoxelphaliaGame extends Game {
    private static VoxelphaliaGame instance;

    @Override
    public void create() {
        setScreen(new GameScreen());
    }

    public static VoxelphaliaGame getInstance() {
        if (instance == null) instance = new VoxelphaliaGame();
        return instance;
    }
}
