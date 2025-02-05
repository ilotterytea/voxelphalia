package kz.ilotterytea.voxelphalia;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.assets.AssetManager;
import kz.ilotterytea.voxelphalia.screens.SplashScreen;

/**
 * {@link com.badlogic.gdx.ApplicationListener} implementation shared by all platforms.
 */
public class VoxelphaliaGame extends Game {
    private static VoxelphaliaGame instance;

    private AssetManager assetManager;

    @Override
    public void create() {
        assetManager = new AssetManager();

        setScreen(new SplashScreen());
    }

    public static VoxelphaliaGame getInstance() {
        if (instance == null) instance = new VoxelphaliaGame();
        return instance;
    }

    public AssetManager getAssetManager() {
        return assetManager;
    }
}
