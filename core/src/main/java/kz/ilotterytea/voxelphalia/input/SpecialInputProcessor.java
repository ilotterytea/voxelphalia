package kz.ilotterytea.voxelphalia.input;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.PixmapIO;
import kz.ilotterytea.voxelphalia.VoxelphaliaConstants;
import kz.ilotterytea.voxelphalia.VoxelphaliaGame;
import kz.ilotterytea.voxelphalia.audio.IdentifiedSound;
import kz.ilotterytea.voxelphalia.entities.PlayerEntity;
import kz.ilotterytea.voxelphalia.utils.OSUtils;

import java.io.File;
import java.util.zip.Deflater;

public class SpecialInputProcessor implements InputProcessor {
    private final Preferences preferences;
    private final Camera camera;
    private final PlayerEntity playerEntity;

    public SpecialInputProcessor(PlayerEntity playerEntity, Camera camera) {
        preferences = VoxelphaliaGame.getInstance().getPreferences();
        this.camera = camera;
        this.playerEntity = playerEntity;
    }

    @Override
    public boolean keyDown(int keycode) {
        boolean processed = false;

        switch (keycode) {
            // display gui
            case Input.Keys.F1: {
                preferences.putBoolean("gui-display", !preferences.getBoolean("gui-display", true));
                preferences.flush();
                processed = true;
                break;
            }
            // screenshot
            case Input.Keys.F2: {
                Pixmap pixmap = Pixmap.createFromFrameBuffer(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());

                String screenShotName = String.format("%s/screenshots", OSUtils.getUserDataDirectory(VoxelphaliaConstants.Metadata.APP_DEV + "/" + VoxelphaliaConstants.Metadata.APP_ID));
                new File(screenShotName).mkdirs();
                screenShotName += "/" + System.currentTimeMillis() + ".png";

                PixmapIO.writePNG(Gdx.files.absolute(screenShotName), pixmap, Deflater.NO_COMPRESSION, true);
                pixmap.dispose();

                IdentifiedSound sound = VoxelphaliaGame.getInstance().getSoundRegistry().getEntry("sfx.ui.screenshot");

                if (sound != null) {
                    sound.getSound().play(VoxelphaliaGame.getInstance().getPreferences().getFloat("sfx", 1f));
                }

                processed = true;
                break;
            }
            // debug info
            case Input.Keys.F3: {
                preferences.putBoolean("debug", !preferences.getBoolean("debug", false));
                preferences.flush();
                processed = true;
                break;
            }
            // render mode
            case Input.Keys.F5: {
                int mode = preferences.getInteger("render-mode", 0);

                if (mode == 0) mode = 1;
                else mode = 0;

                preferences.putInteger("render-mode", mode);
                preferences.flush();

                processed = true;
                break;
            }
            // full screen
            case Input.Keys.F12: {
                boolean fullscreen = preferences.getBoolean("fullscreen", false);
                fullscreen = !fullscreen;

                preferences.putBoolean("fullscreen", fullscreen);
                preferences.flush();

                if (fullscreen) {
                    Gdx.graphics.setFullscreenMode(Gdx.graphics.getDisplayMode());
                } else {
                    int width = preferences.getInteger("screen-width", 800);
                    int height = preferences.getInteger("screen-height", 600);
                    Gdx.graphics.setWindowedMode(width, height);
                }

                processed = true;
                break;
            }
            default:
                break;
        }

        return processed;
    }

    @Override
    public boolean keyUp(int keycode) {
        return false;
    }

    @Override
    public boolean keyTyped(char character) {
        return false;
    }

    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        return false;
    }

    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button) {
        return false;
    }

    @Override
    public boolean touchCancelled(int screenX, int screenY, int pointer, int button) {
        return false;
    }

    @Override
    public boolean touchDragged(int screenX, int screenY, int pointer) {
        return false;
    }

    @Override
    public boolean mouseMoved(int screenX, int screenY) {
        return false;
    }

    @Override
    public boolean scrolled(float amountX, float amountY) {
        return false;
    }
}
