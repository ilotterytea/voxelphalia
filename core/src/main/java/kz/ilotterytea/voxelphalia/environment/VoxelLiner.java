package kz.ilotterytea.voxelphalia.environment;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector3;
import kz.ilotterytea.voxelphalia.VoxelphaliaGame;
import kz.ilotterytea.voxelphalia.entities.PlayerEntity;
import kz.ilotterytea.voxelphalia.level.Level;
import kz.ilotterytea.voxelphalia.utils.Identifier;
import kz.ilotterytea.voxelphalia.utils.Tickable;
import kz.ilotterytea.voxelphalia.utils.registries.VoxelRegistry;
import kz.ilotterytea.voxelphalia.voxels.VoxelMaterial;

public class VoxelLiner implements Tickable {
    private final PlayerEntity playerEntity;
    private final Level level;
    private final ShapeRenderer renderer;

    public VoxelLiner(ShapeRenderer renderer, PlayerEntity playerEntity, Level level) {
        this.renderer = renderer;
        this.playerEntity = playerEntity;
        this.level = level;
    }

    @Override
    public void tick(float delta) {
        VoxelRegistry voxelRegistry = VoxelphaliaGame.getInstance().getVoxelRegistry();
        Vector3 pos = new Vector3(playerEntity.getPosition()), lastPos = new Vector3();
        Vector3 dir = new Vector3(playerEntity.getDirection());
        boolean collided = false;

        int x = (int) Math.floor(pos.x),
            y = (int) Math.floor(pos.y), z = (int) Math.floor(pos.z);

        for (float d = 0f; d <= 5f; d += 0.1f) {
            pos.set(playerEntity.getPosition()).mulAdd(dir, d).add(0f, playerEntity.getHeight(), 0f);
            x = (int) Math.floor(pos.x);
            y = (int) Math.floor(pos.y);
            z = (int) Math.floor(pos.z);

            Identifier voxelId = level.getVoxel(x, y, z);
            if (voxelId != null) {
                VoxelMaterial material = voxelRegistry.getEntry(voxelId).getMaterial();
                if (material != null && !material.isBreakable()) {
                    lastPos.set(pos);
                    break;
                }
            }

            if (level.hasSolidVoxel(x, y, z)) {
                collided = true;
                break;
            }

            lastPos.set(pos);
        }

        if (!collided) {
            return;
        }

        renderer.setProjectionMatrix(playerEntity.getCamera().combined);
        Gdx.gl.glEnable(GL20.GL_DEPTH_TEST);
        Gdx.gl.glDepthFunc(GL20.GL_LESS);
        Gdx.gl.glLineWidth(8f);
        renderer.begin(ShapeRenderer.ShapeType.Line);
        renderer.setColor(Color.BLACK);
        float padding = 0.01f;
        renderer.box(x - padding / 2f, y - padding / 2f, z + 1f + padding / 2f,
            1f + padding, 1f + padding, 1f + padding);
        renderer.end();
    }
}
