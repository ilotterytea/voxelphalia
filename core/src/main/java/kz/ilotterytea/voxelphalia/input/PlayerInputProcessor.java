package kz.ilotterytea.voxelphalia.input;

import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector3;
import kz.ilotterytea.voxelphalia.VoxelphaliaGame;
import kz.ilotterytea.voxelphalia.audio.IdentifiedSound;
import kz.ilotterytea.voxelphalia.entities.BulletEntity;
import kz.ilotterytea.voxelphalia.entities.PlayerEntity;
import kz.ilotterytea.voxelphalia.entities.mobs.MobEntity;
import kz.ilotterytea.voxelphalia.inventory.Inventory;
import kz.ilotterytea.voxelphalia.items.Weapon;
import kz.ilotterytea.voxelphalia.level.Level;
import kz.ilotterytea.voxelphalia.voxels.DestroyableVoxel;
import kz.ilotterytea.voxelphalia.voxels.InteractableVoxel;
import kz.ilotterytea.voxelphalia.voxels.Voxel;

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
        if (!playerEntity.isFocused()) return false;

        return false;
    }

    @Override
    public boolean keyUp(int keycode) {
        return false;
    }

    @Override
    public boolean keyTyped(char character) {
        Inventory inv = playerEntity.getInventory();

        char c = Character.toLowerCase(character);

        if (c == ' ') {
            playerEntity.jump();
            return true;
        } else if (c == 'z') {
            inv.previousSlotIndex();
            return true;
        } else if (c == 'x') {
            inv.nextSlotIndex();
            return true;
        }

        return false;
    }

    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        if (!playerEntity.isFocused()) return false;

        boolean destroy = button == Input.Buttons.LEFT;
        boolean place = button == Input.Buttons.RIGHT;

        if (!destroy && !place) {
            return false;
        }

        if (destroy && hitEntity()) {
            return true;
        }

        Vector3 pos = new Vector3(playerEntity.getPosition()), lastPos = new Vector3();
        Vector3 dir = new Vector3(playerEntity.getDirection());
        boolean collided = false;

        int x = (int) Math.floor(pos.x),
            y = (int) Math.floor(pos.y), z = (int) Math.floor(pos.z);

        for (float d = destroy ? 0f : 1f; d <= 5f; d += 0.1f) {
            pos.set(playerEntity.getPosition()).mulAdd(dir, d).add(0f, playerEntity.getHeight(), 0f);
            x = (int) Math.floor(pos.x);
            y = (int) Math.floor(pos.y);
            z = (int) Math.floor(pos.z);

            if (destroy) {
                if (level.hasSolidVoxel(x, y, z)) {
                    collided = true;
                    break;
                }
            } else {
                if (level.hasInteractableVoxel(x, y, z)) {
                    collided = true;
                    break;
                } else if (level.hasSolidVoxel(x, y, z)) {
                    collided = true;
                    pos.set(lastPos);
                    x = (int) Math.floor(pos.x);
                    y = (int) Math.floor(pos.y);
                    z = (int) Math.floor(pos.z);
                    break;
                }
            }

            lastPos.set(pos);
        }

        if (collided && place && level.hasInteractableVoxel(x, y, z)) {
            if (level.getVoxelState(x, y, z) instanceof InteractableVoxel v) {
                v.onInteract(playerEntity);
            }
            return true;
        }

        if (collided) {
            Voxel voxel = null, nextVoxel = null;

            if (place) {
                Inventory.Slot slot = playerEntity.getInventory().getCurrentSlot();
                voxel = VoxelphaliaGame.getInstance()
                    .getVoxelRegistry().getEntry(slot.id);
                if (voxel != null && (playerEntity.getInventory().remove(voxel.getId()) > 0 || voxel.getId() == null))
                    return false;
                nextVoxel = voxel;
            } else {
                Voxel v = VoxelphaliaGame.getInstance()
                    .getVoxelRegistry().getEntry(level.getVoxel(x, y, z));

                if (v instanceof DestroyableVoxel vv) {
                    Voxel vvv = v;

                    if (level.hasInteractableVoxel(x, y, z)) {
                        vvv = level.getVoxelState(x, y, z);
                    }

                    vv.onDestroy(vvv, playerEntity, level, new Vector3(x, y, z));
                }

                nextVoxel = v;
            }

            if (voxel instanceof InteractableVoxel) {
                level.placeVoxelState(voxel.clone(), x, y, z);
            }

            level.placeVoxel(voxel, x, y, z);

            if (nextVoxel != null) {
                VoxelphaliaGame game = VoxelphaliaGame.getInstance();

                // playing destroy/place sound
                String footstepName = switch (nextVoxel.getMaterial().getType()) {
                    case STONE -> "stone";
                    case LOG -> "log";
                    case DIRT -> "dirt";
                    case SAND -> "sand";
                    case LEAVES -> "leaves";
                    case LIQUID -> "liquid";
                    case null -> null;
                    default -> "grass";
                };

                if (footstepName != null) {
                    IdentifiedSound sound = game.getSoundRegistry()
                        .getEntry("voxelphalia:sfx.footsteps." + footstepName);

                    sound.getSound().play(MathUtils.clamp(game.getPreferences()
                            .getFloat("sfx", 1f),
                        0f, 1f
                    ));
                }
            }
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
        if (!playerEntity.isFocused()) return false;

        Inventory inv = playerEntity.getInventory();

        if (amountY > 0f) {
            inv.previousSlotIndex();
        } else if (amountY < 0f) {
            inv.nextSlotIndex();
        }
        return true;
    }

    private boolean moveCamera(int screenX, int screenY) {
        if (!playerEntity.isFocused()) return false;

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

    private boolean hitEntity() {
        if (shotWithWeapon()) return true;

        Vector3 pos = new Vector3(playerEntity.getPosition());
        Vector3 dir = new Vector3(playerEntity.getDirection());
        boolean collided = false;

        for (float d = 0; d <= 5f; d += 0.1f) {
            pos.set(playerEntity.getPosition()).mulAdd(dir, d).add(0f, playerEntity.getHeight(), 0f);

            if (level.hasEntityByHitBox((int) Math.floor(pos.x), (int) Math.floor(pos.y), (int) Math.floor(pos.z), MobEntity.class)) {
                collided = true;
                break;
            }
        }

        if (collided) {
            MobEntity entity = level.getEntityByHitBox((int) Math.floor(pos.x), (int) Math.floor(pos.y), (int) Math.floor(pos.z), MobEntity.class);

            int damage = playerEntity.getDamage();
            VoxelphaliaGame game = VoxelphaliaGame.getInstance();
            Inventory.Slot currentSlot = playerEntity.getInventory().getCurrentSlot();
            if (currentSlot != null && game.getItemRegistry().containsEntry(currentSlot.id)) {
                if (game.getItemRegistry().getEntry(currentSlot.id) instanceof Weapon w) {
                    damage = w.getDamage();
                }
            }

            entity.takeDamage(damage);
            return true;
        }

        return false;
    }

    private boolean shotWithWeapon() {
        VoxelphaliaGame game = VoxelphaliaGame.getInstance();
        Inventory.Slot currentSlot = playerEntity.getInventory().getCurrentSlot();

        if (currentSlot != null && game.getItemRegistry().containsEntry(currentSlot.id)) {
            if (game.getItemRegistry().getEntry(currentSlot.id) instanceof Weapon w) {
                if (w.isSpawnBullet()) {
                    Vector3 p = playerEntity.getPosition().cpy();
                    p.add(0f, playerEntity.getHeight(), 0f);
                    p.add(playerEntity.getDirection().cpy().scl(1.5f));

                    BulletEntity bullet = new BulletEntity(w, p, playerEntity.getDirection().cpy());
                    level.addEntity(bullet);
                    return true;
                }
            }
        }

        return false;
    }
}
