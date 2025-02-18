package kz.ilotterytea.voxelphalia.level;

import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;
import kz.ilotterytea.voxelphalia.VoxelphaliaGame;
import kz.ilotterytea.voxelphalia.entities.Entity;
import kz.ilotterytea.voxelphalia.entities.LivingEntity;
import kz.ilotterytea.voxelphalia.entities.SaplingEntity;
import kz.ilotterytea.voxelphalia.utils.Tickable;
import kz.ilotterytea.voxelphalia.voxels.Voxel;

public class Level implements Tickable {
    protected final Array<Chunk> chunks;
    protected final int width, height, depth;
    protected final Array<Entity> entities;
    protected final int seed;

    public Level(int width, int height, int depth, int seed) {
        this.width = width;
        this.height = height;
        this.depth = depth;
        this.seed = seed;

        int chunkCapacity = width * height * depth;
        this.chunks = new Array<>(chunkCapacity);

        for (int i = 0; i < chunkCapacity; i++) {
            this.chunks.add(new Chunk(16, 16, 16));
        }

        this.entities = new Array<>();
    }

    public byte getVoxel(int x, int y, int z) {
        Chunk chunk = getChunk(x, y, z);
        if (chunk == null) return 0;
        int cx = x % 16;
        int cy = y % 16;
        int cz = z % 16;
        return chunk.getVoxel(cx, cy, cz);
    }

    public void placeVoxel(Voxel voxel, int x, int y, int z) {
        placeVoxel(voxel.getId(), x, y, z);
    }

    public void placeVoxel(byte voxel, int x, int y, int z) {
        for (int xy = -1; xy < 2; xy++) {
            for (int xc = -1; xc < 2; xc++) {
                for (int xz = -1; xz < 2; xz++) {
                    Chunk chunk = getChunk(x + xc, y + xy, z + xz);
                    if (chunk == null) continue;
                    int cx = x % 16;
                    int cy = y % 16;
                    int cz = z % 16;

                    if (xc == 0 && xy == 0 && xz == 0) {
                        chunk.placeVoxel(voxel, cx, cy, cz);
                    } else {
                        // update cross chunks
                        chunk.isDirty = true;
                    }
                }
            }
        }
    }

    public Chunk getChunk(int x, int y, int z) {
        int index = getChunkIndex(x, y, z);
        if (index < 0 || index >= chunks.size) return null;
        return this.chunks.get(index);
    }

    public int getWidth() {
        return width;
    }

    public int getWidthInVoxels() {
        return getWidth() * 16;
    }

    public int getHeight() {
        return height;
    }

    public int getHeightInVoxels() {
        return getHeight() * 16;
    }

    public int getDepth() {
        return depth;
    }

    public int getDepthInVoxels() {
        return getDepth() * 16;
    }

    private int getChunkIndex(int x, int y, int z) {
        if (x < 0 || x > getWidthInVoxels() ||
            y < 0 || y > getHeightInVoxels() ||
            z < 0 || z > getDepthInVoxels()) return -1;

        int cx = x / 16;
        int cy = y / 16;
        int cz = z / 16;

        return cx + cz * width + cy * width * depth;
    }

    public Vector3 getChunkPositionFromIndex(int index) {
        int cx = index % width;
        int cz = (index / width) % depth;
        int cy = (index / (width * depth)) % height;

        int x = cx * 16;
        int y = cy * 16;
        int z = cz * 16;

        return new Vector3(x, y, z);
    }

    public int getChunkCount() {
        return this.chunks.size;
    }

    public float getHighestY(float x, float z) {
        int ix = (int) x;
        int iz = (int) z;
        if (ix < 0 || ix >= getWidthInVoxels()) return 0;
        if (iz < 0 || iz >= getDepthInVoxels()) return 0;
        for (int y = getHeightInVoxels() - 1; y > 0; y--) {
            if (getVoxel(ix, y, iz) > 0) return y + 1;
        }
        return 0;
    }

    public boolean hasVoxel(int x, int y, int z) {
        return getVoxel(x, y, z) != 0;
    }

    public boolean hasSolidVoxel(int x, int y, int z) {
        byte voxel = getVoxel(x, y, z);
        Voxel type = VoxelphaliaGame.getInstance().getVoxelRegistry().getEntryById(voxel);

        return !type.getMaterial().isTranslucent();
    }

    public void addEntity(Entity entity) {
        this.entities.add(entity);
    }

    public Entity getEntity(int x, int y, int z) {
        for (Entity entity : this.entities) {
            Vector3 pos = entity.getPosition();
            if (pos.x == x && pos.y == y && pos.z == z) {
                return entity;
            }
        }
        return null;
    }

    public <T extends Entity> T getEntity(int x, int y, int z, Class<T> type) {
        for (Entity entity : this.entities) {
            Vector3 pos = entity.getPosition();
            if (pos.x == x && pos.y == y && pos.z == z) {
                if (type.isInstance(entity)) {
                    return type.cast(entity);
                }
            }
        }
        return null;
    }

    public <T extends LivingEntity> T getEntityByHitBox(int x, int y, int z, Class<T> type) {
        Vector3 p = new Vector3(x, y, z);
        for (Entity entity : this.entities) {
            if (entity instanceof LivingEntity e) {
                if (e.getHitBox().contains(p)) {
                    if (type.isInstance(entity)) {
                        return type.cast(entity);
                    }
                }
            }
        }
        return null;
    }

    public void removeEntity(Entity entity) {
        this.entities.removeValue(entity, false);
    }

    public boolean hasEntity(int x, int y, int z) {
        return getEntity(x, y, z) != null;
    }

    public <T extends Entity> boolean hasEntity(int x, int y, int z, Class<T> type) {
        return getEntity(x, y, z, type) != null;
    }

    public <T extends LivingEntity> boolean hasEntityByHitBox(int x, int y, int z, Class<T> type) {
        return getEntityByHitBox(x, y, z, type) != null;
    }

    public int getEntityCount() {
        return this.entities.size;
    }

    public int getSeed() {
        return seed;
    }

    @Override
    public void tick(float delta) {
        Array<Entity> removeEntities = new Array<>();
        for (Entity entity : entities) {
            if (entity instanceof Tickable e) {
                e.tick(delta);
                e.tick(delta, this);
            }

            if (entity instanceof SaplingEntity tree) {
                if (tree.hasGrown()) removeEntities.add(entity);
            }
        }

        for (Entity e : removeEntities) {
            removeEntity(e);
        }
    }
}
