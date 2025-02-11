package kz.ilotterytea.voxelphalia.input;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.math.Vector3;
import kz.ilotterytea.voxelphalia.VoxelphaliaGame;
import kz.ilotterytea.voxelphalia.entities.PlayerEntity;
import kz.ilotterytea.voxelphalia.inventory.Inventory;
import kz.ilotterytea.voxelphalia.level.Level;

public class PlayerInputProcessor implements InputProcessor {
    private final Level level;
    private final PlayerEntity playerEntity;
    private final Camera camera;

    private int dragX, dragY;

    public PlayerInputProcessor(PlayerEntity playerEntity, Level level, Camera camera) {
        this.playerEntity = playerEntity;
        this.camera = camera;
        this.level = level;
    }

    @Override
    public boolean keyDown(int keycode) {
        if (!Gdx.input.isCursorCatched()) return false;

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
        boolean destroy = button == Input.Buttons.LEFT;
        boolean place = button == Input.Buttons.RIGHT;

        if (!destroy && !place) {
            return false;
        }

        Vector3 pos = new Vector3(playerEntity.getPosition());
        Vector3 dir = new Vector3(playerEntity.getDirection());
        boolean collided = false;

        if (destroy) {
            for (float d = 0; d <= 5f; d += 0.1f) {
                pos.set(playerEntity.getPosition()).mulAdd(dir, d).add(0f, playerEntity.getHeight(), 0f);

                if (level.hasSolidVoxel((int) Math.floor(pos.x), (int) Math.floor(pos.y), (int) Math.floor(pos.z))) {
                    collided = true;
                    break;
                }
            }
        } else {
            boolean foundSolidVoxel = false;

            Vector3 lastPos = new Vector3();

            for (float d = 5f; d > 1; d -= 0.1f) {
                pos.set(playerEntity.getPosition()).mulAdd(dir, d).add(0f, playerEntity.getHeight(), 0f);

                if (foundSolidVoxel) lastPos.set(pos);

                foundSolidVoxel = level.hasSolidVoxel((int) Math.floor(pos.x), (int) Math.floor(pos.y), (int) Math.floor(pos.z));
            }

            collided = !lastPos.isZero();
            pos.set(lastPos);
        }

        if (collided) {
            byte voxel = 0;
            int x = (int) Math.floor(pos.x),
                y = (int) Math.floor(pos.y), z = (int) Math.floor(pos.z);

            if (place) {
                Inventory.Slot slot = playerEntity.getInventory().getCurrentSlot();
                voxel = slot.id;
                if (playerEntity.getInventory().remove(voxel) > 0 || voxel == 0) return false;
            } else if (playerEntity.getInventory().add(level.getVoxel(x, y, z)) > 0) {
                // TODO: drop a voxel instead of just blocking the action
                return false;
            }

            level.placeVoxel(voxel, x, y, z);
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
        return moveCamera(screenX, screenY);
    }

    @Override
    public boolean mouseMoved(int screenX, int screenY) {
        return moveCamera(screenX, screenY);
    }

    @Override
    public boolean scrolled(float amountX, float amountY) {
        if (!Gdx.input.isCursorCatched()) return false;
        if (amountY > 0f) {
            playerEntity.getInventory().previousSlotIndex();
        } else {
            playerEntity.getInventory().nextSlotIndex();
        }
        return true;
    }

    private boolean moveCamera(int screenX, int screenY) {
        if (!Gdx.input.isCursorCatched()) return false;

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
}
