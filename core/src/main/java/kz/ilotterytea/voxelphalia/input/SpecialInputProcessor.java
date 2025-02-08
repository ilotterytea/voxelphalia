package kz.ilotterytea.voxelphalia.input;

import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.Preferences;
import kz.ilotterytea.voxelphalia.VoxelphaliaGame;

public class SpecialInputProcessor implements InputProcessor {
    private final Preferences preferences;

    public SpecialInputProcessor() {
        preferences = VoxelphaliaGame.getInstance().getPreferences();
    }

    @Override
    public boolean keyDown(int keycode) {
        boolean processed = false;

        if (keycode == Input.Keys.F3) {
            preferences.putBoolean("debug", !preferences.getBoolean("debug", false));
            preferences.flush();
            processed = true;
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
