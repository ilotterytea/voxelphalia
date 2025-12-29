package kz.ilotterytea.voxelphalia.level;

import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;
import kz.ilotterytea.voxelphalia.VoxelphaliaGame;
import kz.ilotterytea.voxelphalia.entities.Entity;
import kz.ilotterytea.voxelphalia.entities.LivingEntity;
import kz.ilotterytea.voxelphalia.utils.Identifier;
import kz.ilotterytea.voxelphalia.utils.Tickable;
import kz.ilotterytea.voxelphalia.utils.registries.VoxelRegistry;
import kz.ilotterytea.voxelphalia.voxels.InteractableVoxel;
import kz.ilotterytea.voxelphalia.voxels.PhysicalVoxel;
import kz.ilotterytea.voxelphalia.voxels.Voxel;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Queue;

public class Level implements Tickable {
    public enum LevelGeneratorType {
        LIMITED,
        ISLAND
    }

    public enum LevelGameMode {
        SURVIVAL;
    }

    protected final Array<Chunk> chunks;
    protected final Queue<Vector3> voxelPhysicsQueue;
    protected final int width, height, depth;
    protected final ArrayList<Entity> entities;
    protected final int seed;
    protected final String name;
    protected long lastTimeOpened;

    protected final LevelGameMode gameMode;
    protected final LevelGeneratorType generatorType;

    private boolean applyPhysics;

    public Level(String name, int width, int height, int depth, int seed, LevelGeneratorType generatorType, LevelGameMode gameMode) {
        this.width = width;
        this.height = height;
        this.depth = depth;
        this.seed = seed;
        this.name = name;
        this.generatorType = generatorType;
        this.gameMode = gameMode;
        this.voxelPhysicsQueue = new ArrayDeque<>();

        int chunkCapacity = width * height * depth;
        this.chunks = new Array<>(chunkCapacity);

        for (int i = 0; i < chunkCapacity; i++) {
            this.chunks.add(new Chunk(16, getChunkPositionFromIndex(i)));
        }

        this.entities = new ArrayList<>();
        this.lastTimeOpened = System.currentTimeMillis();
    }

    public Identifier getVoxel(float x, float y, float z) {
        return getVoxel((int) x, (int) y, (int) z);
    }

    public Identifier getVoxel(int x, int y, int z) {
        Chunk chunk = getChunk(x, y, z);
        if (chunk == null) return null;
        int cx = x % 16;
        int cy = y % 16;
        int cz = z % 16;
        return chunk.getVoxel(cx, cy, cz);
    }

    public Voxel getVoxelState(int x, int y, int z) {
        Chunk chunk = getChunk(x, y, z);
        if (chunk == null) return null;
        int cx = x % 16;
        int cy = y % 16;
        int cz = z % 16;
        return chunk.getVoxelState(cx, cy, cz);
    }

    public void placeVoxelState(Voxel voxel, int x, int y, int z) {
        for (int xy = -1; xy < 2; xy++) {
            for (int xc = -1; xc < 2; xc++) {
                for (int xz = -1; xz < 2; xz++) {
                    Chunk chunk = getChunk(x + xc, y + xy, z + xz);
                    if (chunk == null) continue;
                    int cx = x % 16;
                    int cy = y % 16;
                    int cz = z % 16;

                    if (xc == 0 && xy == 0 && xz == 0) {
                        chunk.placeVoxelState(voxel, cx, cy, cz);
                    } else {
                        // update cross chunks
                        chunk.isDirty = true;
                    }
                }
            }
        }
    }

    public void placeVoxel(Voxel voxel, float x, float y, float z) {
        placeVoxel(voxel, (int) x, (int) y, (int) z);
    }

    public void placeVoxel(Voxel voxel, int x, int y, int z) {
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

        if (isApplyingPhysics()) {
            voxelPhysicsQueue.add(new Vector3(x, y, z));
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

    public int getChunkIndex(int x, int y, int z) {
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

    public int getHighestY(float x, float z) {
        int ix = (int) x;
        int iz = (int) z;
        if (ix < 0 || ix >= getWidthInVoxels()) return 0;
        if (iz < 0 || iz >= getDepthInVoxels()) return 0;
        for (int y = getHeightInVoxels() - 1; y > 0; y--) {
            if (getVoxel(ix, y, iz) != null) return y + 1;
        }
        return 0;
    }

    public boolean hasVoxel(float x, float y, float z) {
        return hasVoxel((int) x, (int) y, (int) z);
    }

    public boolean hasVoxel(int x, int y, int z) {
        return getVoxel(x, y, z) != null;
    }

    public boolean hasSolidVoxel(int x, int y, int z) {
        Identifier voxel = getVoxel(x, y, z);
        Voxel type = VoxelphaliaGame.getInstance().getVoxelRegistry().getEntry(voxel);

        return type != null && !type.getMaterial().isTranslucent();
    }

    public boolean hasVoxelState(int x, int y, int z) {
        return getVoxelState(x, y, z) != null;
    }

    public boolean hasInteractableVoxel(int x, int y, int z) {
        Identifier voxel = getVoxel(x, y, z);
        Voxel type = VoxelphaliaGame.getInstance().getVoxelRegistry().getEntry(voxel);

        return type instanceof InteractableVoxel;
    }

    public void addEntity(Entity entity) {
        Chunk chunk = getChunk((int) entity.getPosition().x, (int) entity.getPosition().y, (int) entity.getPosition().z);
        if (chunk.locked) return;
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
        Chunk chunk = getChunk((int) entity.getPosition().x, (int) entity.getPosition().y, (int) entity.getPosition().z);
        if (chunk.locked) return;

        this.entities.remove(entity);
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
        return this.entities.size();
    }

    public int getSeed() {
        return seed;
    }

    @Override
    public void tick(float delta) {
        if (!isApplyingPhysics()) return;
        VoxelRegistry registry = VoxelphaliaGame.getInstance().getVoxelRegistry();
        int budget = 60;
        while (!voxelPhysicsQueue.isEmpty() && budget-- > 0) {
            Vector3 p = voxelPhysicsQueue.poll();

            float[] posAround = new float[]{
                p.x, p.y, p.z,
                p.x - 1, p.y, p.z,
                p.x + 1, p.y, p.z,
                p.x, p.y - 1, p.z,
                p.x, p.y + 1, p.z,
                p.x, p.y, p.z - 1,
                p.x, p.y, p.z + 1,
            };

            for (int i = 0; i < 6; i++) {
                float x = posAround[i * 3], y = posAround[i * 3 + 1], z = posAround[i * 3 + 2];
                Voxel v = registry.getEntry(getVoxel(x, y, z));
                if (v instanceof PhysicalVoxel physicalVoxel) {
                    physicalVoxel.onPhysicsApply(this, new Vector3(x, y, z));
                }
            }
        }
    }

    public Array<Chunk> getChunks() {
        return chunks;
    }

    public ArrayList<Entity> getEntities() {
        return entities;
    }

    public String getName() {
        return name;
    }

    public long getLastTimeOpened() {
        return lastTimeOpened;
    }

    public void setLastTimeOpened(long lastTimeOpened) {
        this.lastTimeOpened = lastTimeOpened;
    }

    public LevelGameMode getGameMode() {
        return gameMode;
    }

    public LevelGeneratorType getGeneratorType() {
        return generatorType;
    }

    public boolean isApplyingPhysics() {
        return applyPhysics;
    }

    public void setApplyPhysics(boolean applyPhysics) {
        this.applyPhysics = applyPhysics;
    }
}
