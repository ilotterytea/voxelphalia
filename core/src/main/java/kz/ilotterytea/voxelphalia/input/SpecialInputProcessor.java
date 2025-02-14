package kz.ilotterytea.voxelphalia.input;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.graphics.Camera;
import kz.ilotterytea.voxelphalia.VoxelphaliaGame;

public class SpecialInputProcessor implements InputProcessor {
    private final Preferences preferences;
    private final Camera camera;

    public SpecialInputProcessor(Camera camera) {
        preferences = VoxelphaliaGame.getInstance().getPreferences();
        this.camera = camera;
    }

    @Override
    public boolean keyDown(int keycode) {
        boolean processed = false;

        switch (keycode) {
            // debug info
            case Input.Keys.F3: {
                preferences.putBoolean("debug", !preferences.getBoolean("debug", false));
                preferences.flush();
                processed = true;
                break;
            }
            // render distance
            case Input.Keys.F4: {
                int distance = preferences.getInteger("render-distance", 1);
                distance += 3;
                if (distance >= 10) distance = 1;

                preferences.putInteger("render-distance", distance);
                preferences.flush();

                camera.far = 40.0f * distance;
                camera.update();

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
            // press two times to close the game, press one time to unfocus
            case Input.Keys.ESCAPE: {
                if (Gdx.input.isCursorCatched()) {
                    Gdx.input.setCursorCatched(false);
                } else {
                    Gdx.app.exit();
                }
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
        if (!Gdx.input.isCursorCatched()) {
            Gdx.input.setCursorCatched(true);
            return true;
        }

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
