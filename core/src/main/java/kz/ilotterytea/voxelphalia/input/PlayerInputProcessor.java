package kz.ilotterytea.voxelphalia.input;

import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.math.Vector3;
import kz.ilotterytea.voxelphalia.VoxelphaliaGame;
import kz.ilotterytea.voxelphalia.entities.PlayerEntity;

public class PlayerInputProcessor implements InputProcessor {
    private final PlayerEntity playerEntity;
    private final Camera camera;

    private int dragX, dragY;

    public PlayerInputProcessor(PlayerEntity playerEntity, Camera camera) {
        this.playerEntity = playerEntity;
        this.camera = camera;
    }

    @Override
    public boolean keyDown(int keycode) {
        if (keycode == Input.Keys.SPACE) {
            playerEntity.jump();
            return true;
        }

        if (keycode >= Input.Keys.NUM_1 && keycode <= Input.Keys.NUM_1 + playerEntity.getInventory().getSize()) {
            for (int i = 0; i < playerEntity.getInventory().getSize(); i++) {
                if (keycode == Input.Keys.NUM_1 + i) {
                    playerEntity.getInventory().setSlotIndex(i);
                    return true;
                }
            }
        }

        return false;
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
        float sensitivity = VoxelphaliaGame.getInstance().getPreferences().getFloat("mouse-sensitivity", 20f) / 100f;
        float x = dragX - screenX;
        camera.rotate(Vector3.Y, x * sensitivity);

        Vector3 oldPitchAxis = camera.direction.cpy().crs(camera.up).nor();
        Vector3 newDirection = camera.direction.cpy().rotate(oldPitchAxis, (dragY - screenY) * sensitivity);
        Vector3 newPitchAxis = newDirection.cpy().crs(camera.up);

        if (!newPitchAxis.hasOppositeDirection(oldPitchAxis)) {
            camera.direction.set(newDirection);
        }

        camera.update();
        dragX = screenX;
        dragY = screenY;

        playerEntity.setDirection(camera.direction.x, camera.direction.y, camera.direction.z);
        return true;
    }

    @Override
    public boolean scrolled(float amountX, float amountY) {
        if (amountY > 0f) {
            playerEntity.getInventory().previousSlotIndex();
        } else {
            playerEntity.getInventory().nextSlotIndex();
        }
        return true;
    }
}
