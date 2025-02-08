package kz.ilotterytea.voxelphalia;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.assets.AssetManager;
import kz.ilotterytea.voxelphalia.screens.SplashScreen;

/**
 * {@link com.badlogic.gdx.ApplicationListener} implementation shared by all platforms.
 */
public class VoxelphaliaGame extends Game {
    private static VoxelphaliaGame instance;

    private AssetManager assetManager;
    private Preferences preferences;

    @Override
    public void create() {
        assetManager = new AssetManager();
        preferences = Gdx.app.getPreferences(VoxelphaliaConstants.Metadata.APP_PACKAGE);

        if (preferences.getBoolean("fullscreen", false)) {
            Gdx.graphics.setFullscreenMode(Gdx.graphics.getDisplayMode());
        }

        setScreen(new SplashScreen());
    }

    @Override
    public void resize(int width, int height) {
        super.resize(width, height);
        preferences.putInteger("screen-width", width);
        preferences.putInteger("screen-height", height);
        preferences.flush();
    }

    public static VoxelphaliaGame getInstance() {
        if (instance == null) instance = new VoxelphaliaGame();
        return instance;
    }

    public Preferences getPreferences() {
        return preferences;
    }

    public AssetManager getAssetManager() {
        return assetManager;
    }
}
