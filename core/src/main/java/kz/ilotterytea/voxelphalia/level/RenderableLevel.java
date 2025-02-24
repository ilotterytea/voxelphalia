package kz.ilotterytea.voxelphalia.level;

import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.g3d.Environment;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.graphics.g3d.decals.DecalBatch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Disposable;
import kz.ilotterytea.voxelphalia.VoxelphaliaGame;
import kz.ilotterytea.voxelphalia.entities.*;
import kz.ilotterytea.voxelphalia.utils.Renderable;
import kz.ilotterytea.voxelphalia.utils.Tickable;

public class RenderableLevel implements Disposable, Tickable, Renderable {
    private final Level level;
    private final Camera camera;
    private final Array<RenderableChunk> renderableChunks;
    private final PlayerEntity player;
    private int renderedChunkCount, renderedVertexCount, renderedIndexCount;

    public RenderableLevel(Camera camera, PlayerEntity player, Level level) {
        this.level = level;
        this.camera = camera;
        this.player = player;
        int chunkCapacity = level.width * level.height * level.depth;
        this.renderableChunks = new Array<>(chunkCapacity);

        for (int i = 0; i < chunkCapacity; i++) {
            Chunk chunk = level.chunks.get(i);
            Vector3 offset = level.getChunkPositionFromIndex(i);

            this.renderableChunks.add(new RenderableChunk(chunk, level, offset));
        }
    }

    @Override
    public void render(ModelBatch batch, Environment environment) {
        renderedChunkCount = 0;
        renderedIndexCount = 0;
        renderedVertexCount = 0;
        for (RenderableChunk chunk : renderableChunks) {
            if (isChunkVisible(chunk)) {
                batch.render(chunk, environment);
                renderedChunkCount++;
                renderedIndexCount += chunk.getMeshIndexCount();
                renderedVertexCount += chunk.getMeshVertexCount();
            }
        }
    }

    @Override
    public void render(DecalBatch batch) {
        for (Entity entity : level.entities) {
            if (entity instanceof Renderable e) {
                e.render(batch);
            }
        }
    }

    @Override
    public void tick(float delta) {
        for (RenderableChunk chunk : renderableChunks) {
            if (isChunkVisible(chunk)) {
                chunk.tick(delta);
            } else {
                chunk.dispose();
            }
        }
    }

    @Override
    public void tick(float delta, Level level, Camera camera) {
        Array<Entity> removeEntities = new Array<>();
        for (Entity entity : level.entities) {
            if (entity instanceof Tickable e) {
                e.tick(delta);
                e.tick(delta, player);
                e.tick(delta, level);
                e.tick(delta, camera);
            }

            switch (entity) {
                case SaplingEntity tree -> {
                    if (tree.hasGrown()) removeEntities.add(entity);
                }
                case DropEntity drop -> {
                    if (drop.isDead()) removeEntities.add(drop);
                }
                // we need this empty clause because we don't want player to be deleted, right?
                case PlayerEntity ignored -> {
                }
                case LivingEntity living -> {
                    if (living.isDead()) removeEntities.add(living);
                }
                case BulletEntity bullet -> {
                    if (bullet.isHit()) {
                        removeEntities.add(bullet);
                    }
                }
                case ParticleEntity particle -> {
                    if (particle.isDead()) removeEntities.add(particle);
                }
                default -> {
                }
            }
        }

        for (Entity e : removeEntities) {
            level.removeEntity(e);
            if (e instanceof LivingEntity x) {
                Vector3 pos = e.getPosition();

                for (int i = 0; i < 1000; i++) {
                    ParticleEntity p = new ParticleEntity(new TextureRegion(
                        VoxelphaliaGame.getInstance()
                            .getAssetManager()
                            .get("textures/entities/particles/pixel.png", Texture.class)),
                        10f, 2f
                    );
                    p.setPosition(pos.x, pos.y, pos.z);
                    float cr = MathUtils.random(-0.5f, 0.5f);
                    p.setColor(Color.RED.cpy().add(cr, cr, cr, 0f));
                    level.addEntity(p);
                }
            }
        }
    }

    @Override
    public void dispose() {
        for (RenderableChunk chunk : renderableChunks) {
            chunk.dispose();
        }
    }

    private boolean isChunkVisible(RenderableChunk chunk) {
        Vector3 o = chunk.getOffset();
        return this.camera.frustum.boundsInFrustum(
            o.x + 16 / 2f,
            o.y + 16 / 2f,
            o.z + 16 / 2f
            ,
            16, 16, 16
        );
    }

    public int getRenderedChunkCount() {
        return renderedChunkCount;
    }

    public int getRenderedIndexCount() {
        return renderedIndexCount;
    }

    public int getRenderedVertexCount() {
        return renderedVertexCount;
    }

    public Level getLevel() {
        return level;
    }
}
