package kz.ilotterytea.voxelphalia.level.generators;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;
import kz.ilotterytea.voxelphalia.VoxelphaliaGame;
import kz.ilotterytea.voxelphalia.entities.SaplingEntity;
import kz.ilotterytea.voxelphalia.entities.mobs.MobEntity;
import kz.ilotterytea.voxelphalia.entities.mobs.MobType;
import kz.ilotterytea.voxelphalia.entities.mobs.friendly.MobPig;
import kz.ilotterytea.voxelphalia.entities.mobs.hostile.MobFish;
import kz.ilotterytea.voxelphalia.entities.mobs.neutral.MobPenguin;
import kz.ilotterytea.voxelphalia.level.Chunk;
import kz.ilotterytea.voxelphalia.level.Level;
import kz.ilotterytea.voxelphalia.utils.Identifier;
import kz.ilotterytea.voxelphalia.utils.registries.VoxelRegistry;
import kz.ilotterytea.voxelphalia.voxels.Voxel;

import java.util.*;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class IslandTerrainGenerator implements TerrainGenerator {
    private final Array<Chunk> caveChunks;

    private final FastNoiseLite heightNoise, detailNoise, treeNoise;
    private final FastNoiseLite caveNoise, caveWrapNoise, oreNoise;

    private final Voxel mantle, stone, water, sand, dirt, grass, snow;

    public IslandTerrainGenerator(int seed) {
        caveChunks = new Array<>();

        heightNoise = new FastNoiseLite(seed);
        heightNoise.SetNoiseType(FastNoiseLite.NoiseType.Perlin);
        heightNoise.SetFrequency(0.05f);

        detailNoise = new FastNoiseLite(seed + 1);
        detailNoise.SetNoiseType(FastNoiseLite.NoiseType.Perlin);
        detailNoise.SetFrequency(0.2f);

        treeNoise = new FastNoiseLite(seed + 4);
        treeNoise.SetNoiseType(FastNoiseLite.NoiseType.Perlin);
        treeNoise.SetFrequency(0.6f);

        caveNoise = new FastNoiseLite(seed + 8);
        caveNoise.SetNoiseType(FastNoiseLite.NoiseType.OpenSimplex2S);
        caveNoise.SetFrequency(0.008f);
        caveNoise.SetFractalType(FastNoiseLite.FractalType.FBm);
        caveNoise.SetFractalOctaves(3);
        caveNoise.SetFractalGain(0.5f);
        caveNoise.SetFractalLacunarity(2.0f);

        caveWrapNoise = new FastNoiseLite(seed + 9);
        caveWrapNoise.SetNoiseType(FastNoiseLite.NoiseType.OpenSimplex2);
        caveWrapNoise.SetFrequency(0.05f);

        oreNoise = new FastNoiseLite(seed + 121);
        oreNoise.SetNoiseType(FastNoiseLite.NoiseType.OpenSimplex2);
        oreNoise.SetFrequency(0.08f);

        VoxelRegistry voxels = VoxelphaliaGame.getInstance().getVoxelRegistry();

        mantle = voxels.getEntry("mantle");
        stone = voxels.getEntry("stone");
        water = voxels.getEntry("water");
        sand = voxels.getEntry("sand");
        dirt = voxels.getEntry("dirt");
        grass = voxels.getEntry("grass_voxel");
        snow = voxels.getEntry("snow");
    }

    @Override
    public void generateLandscape(Level level) {
        final float WATER_HEIGHT = 10f;
        final float SAND_LEVEL = 0.18f;
        final float GRASS_LEVEL = 0.6f;
        final float ROCK_LEVEL = 0.66f;
        final int DIRT_DEPTH = 4;
        final float HEIGHT = 5 * 16;

        for (int x = 0; x < level.getWidthInVoxels(); x++) {
            for (int z = 0; z < level.getDepthInVoxels(); z++) {
                float base = heightNoise.GetNoise(x, z) * 5;
                float detail = detailNoise.GetNoise(x, z) * 2;
                float rawHeight = 20f + Math.round(base + detail);

                // island borders
                float cx = level.getWidthInVoxels() / 2f, cz = level.getDepthInVoxels() / 2f;
                float dx = x - cx, dz = z - cz;
                double distance = Math.sqrt(dx * dx + dz * dz);
                double maxDistance = Math.min(cx, cz) * 1.3;
                double fallout = Math.max(0, 1 - distance / maxDistance);

                // mountain
                double centerFactor = 1 - distance / (maxDistance * 0.8f);
                if (centerFactor < 0) centerFactor = 0;
                centerFactor = Math.pow(centerFactor, 2);
                int peakHeight = 50;

                int terrainHeight = (int) (rawHeight * fallout) + (int) (peakHeight * centerFactor);

                // base layer
                for (int y = 0; y < terrainHeight; y++) {
                    if (y == 0) {
                        level.placeVoxel(mantle, x, y, z);
                    } else {
                        level.placeVoxel(stone, x, y, z);
                    }
                }

                // water
                if (terrainHeight < WATER_HEIGHT) {
                    // filling with the water
                    for (int y = terrainHeight; y <= WATER_HEIGHT; y++) {
                        level.placeVoxel(water, x, y, z);
                    }
                    for (int i = 0; i <= DIRT_DEPTH; i++) {
                        level.placeVoxel(sand, x, terrainHeight - i, z);
                    }
                }
                //beach
                else if (terrainHeight < SAND_LEVEL * HEIGHT) {
                    for (int i = 0; i <= DIRT_DEPTH; i++) {
                        level.placeVoxel(sand, x, terrainHeight - i, z);
                    }
                }
                // grass
                else if (terrainHeight < GRASS_LEVEL * HEIGHT) {
                    level.placeVoxel(grass, x, terrainHeight, z);
                    for (int i = 1; i <= DIRT_DEPTH; i++) {
                        level.placeVoxel(dirt, x, terrainHeight - i, z);
                    }
                    level.placeVoxel(stone, x, terrainHeight - DIRT_DEPTH - 1, z);
                }
                // rock hills
                else if (terrainHeight < ROCK_LEVEL * HEIGHT) {
                    level.placeVoxel(stone, x, terrainHeight, z);
                }
                // snowy peaks
                else {
                    level.placeVoxel(snow, x, terrainHeight, z);
                    level.placeVoxel(stone, x, terrainHeight - 1, z);
                }

                // generating caves
                for (int y = 4; y < terrainHeight + 3; y++) {
                    if (!isCave(x, y, z)) continue;

                    if (y <= WATER_HEIGHT) {
                        level.placeVoxel(water, x, y, z);
                    } else {
                        level.placeVoxel(null, x, y, z);
                    }

                    caveChunks.add(level.getChunk(x, y, z));
                }
            }
        }
    }

    @Override
    public void generateMobs(Level level, MobType mob) {
        Random random = new Random(level.getSeed() + 18 + mob.ordinal());
        int success = 0;

        try {
            for (int i = 0; i < 2000; i++) {
                int x = random.nextInt(level.getWidthInVoxels());
                int z = random.nextInt(level.getDepthInVoxels());
                float value = treeNoise.GetNoise(x, z);

                if (value > 0.6f) {
                    float y = level.getHighestY(x, z);
                    Identifier v = level.getVoxel(x, (int) y - 1, z);

                    if (v != null && !v.equals("water")) {
                        MobEntity entity = switch (mob) {
                            case PIG -> new MobPig();
                            case FISH -> new MobFish();
                            case PENGUIN -> new MobPenguin();
                        };

                        entity.setPosition(x, y, z);
                        level.addEntity(entity);
                        success++;
                    }
                }
            }

            Gdx.app.log("TerrainGenerator", "Generated " + success + " " + mob + " mob");
        } catch (Exception e) {
            Gdx.app.log("TerrainGenerator", "Failed to create a mob");
        }
    }

    @Override
    public void generateMinerals(Level level) {
        VoxelRegistry registry = VoxelphaliaGame.getInstance().getVoxelRegistry();
        Voxel[] minerals = new Voxel[]{
            registry.getEntry("coal_mineral"),
            registry.getEntry("iron_mineral"),
            registry.getEntry("gold_mineral"),
            registry.getEntry("ruby_mineral"),
            registry.getEntry("gemstone_mineral"),
        };

        // minY, maxY, minimal noise
        float[] settings = new float[]{
            40f, 100f, 0.6f,
            32f, 49f, 0.94f,
            21f, 33f, 0.85f,
            40f, 100f, 0.9f,
            11f, 20f, 0.97f,
        };

        int[] generatedAmount = new int[minerals.length];

        // multithreaded ore placement
        int threadCount = Runtime.getRuntime().availableProcessors();

        try (ExecutorService executor = Executors.newFixedThreadPool(threadCount)) {
            List<List<Chunk>> chunkBatches = new ArrayList<>();

            for (int i = 0; i < threadCount; i++) chunkBatches.add(new ArrayList<>());
            for (int i = 0; i < caveChunks.size; i++) chunkBatches.get(i % threadCount).add(caveChunks.get(i));

            List<Callable<Void>> tasks = new ArrayList<>();
            for (List<Chunk> batch : chunkBatches) {
                tasks.add(() -> {
                    for (Chunk chunk : batch) {
                        for (int i = 0; i < minerals.length; i++) {
                            float minY = settings[i * 3], maxY = settings[i * 3 + 1], value = settings[i * 3 + 2];
                            int generatedMinerals = generateMineral(chunk, level, minerals[i], minY, maxY, value, level.getSeed() + 121);
                            generatedAmount[i] += generatedMinerals;
                        }
                    }
                    return null;
                });
            }

            executor.invokeAll(tasks);
            executor.shutdown();
        } catch (InterruptedException exception) {
            throw new RuntimeException(exception);
        }

        for (int i = 0; i < generatedAmount.length; i++) {
            Gdx.app.log(getClass().getSimpleName(), "Generated " + generatedAmount[i] + " " + minerals[i]);
        }
    }

    private int generateMineral(Chunk chunk, Level level, Voxel mineral, float minY, float maxY, float value, int seed) {
        if (chunk.getOffset().y < minY || chunk.getOffset().y > maxY) {
            return 0;
        }

        Random random = new Random(seed + mineral.getId().hashCode());

        Array<Vector3> positions = new Array<>();

        // determining potential ore positions
        for (int x = 0; x < Chunk.SIZE; x++) {
            int lx = (int) (chunk.getOffset().x + x);
            for (int z = 0; z < Chunk.SIZE; z++) {
                int lz = (int) (chunk.getOffset().z + z);
                for (int y = 0; y < Chunk.SIZE; y++) {
                    int ly = (int) (chunk.getOffset().y + y);
                    Identifier v = level.getVoxel(lx, ly, lz);
                    if (ly < minY || ly > maxY || v == null || !v.equals("stone")) continue;

                    float n = oreNoise.GetNoise(lx, ly, lz);
                    if (n < value) continue;

                    positions.add(new Vector3(lx, ly, lz));
                }
            }
        }

        int veins = Math.max(1, positions.size / 50);
        int amount = 0;
        for (int i = 0; i < veins; i++) {
            if (positions.isEmpty()) break;

            int idx = random.nextInt(positions.size);
            Vector3 position = positions.removeIndex(idx);

            Queue<Vector3> queue = new ArrayDeque<>();
            Set<Vector3> visited = new HashSet<>();
            queue.add(position);

            int size = 3 + random.nextInt(3);
            while (!queue.isEmpty() && size > 0) {
                Vector3 c = queue.poll();
                if (!visited.add(c)) continue;
                level.placeVoxel(mineral, (int) c.x, (int) c.y, (int) c.z);
                amount++;
                size--;

                List<Vector3> neighbors = Arrays.asList(
                    new Vector3(c.x + 1, c.y, c.z),
                    new Vector3(c.x - 1, c.y, c.z),
                    new Vector3(c.x, c.y + 1, c.z),
                    new Vector3(c.x, c.y - 1, c.z),
                    new Vector3(c.x, c.y, c.z + 1),
                    new Vector3(c.x, c.y, c.z - 1)
                );
                Collections.shuffle(neighbors, random);

                for (Vector3 n : neighbors) {
                    if (random.nextFloat() < 0.2f) queue.add(n);
                }
            }
        }

        return amount;
    }

    @Override
    public void generateTrees(Level level) {
        Random random = new Random(level.getSeed() + 4);
        int attempts = 16000, success = 0;

        for (int i = 0; i < attempts; i++) {
            int x = random.nextInt(level.getWidthInVoxels());
            int z = random.nextInt(level.getDepthInVoxels());
            float value = treeNoise.GetNoise(x, z);

            if (value > 0.4f) {
                float y = level.getHighestY(x, z);
                Identifier v = level.getVoxel(x, (int) y - 1, z);

                if (v != null && v.equals("grass_voxel")) {
                    SaplingEntity entity = new SaplingEntity(new Vector3(x, y, z), 0.0f);
                    level.addEntity(entity);
                    success++;
                }
            }
        }

        Gdx.app.log(getClass().getSimpleName(), "Placed " + success + " trees");
    }

    private boolean isCave(int x, int y, int z) {
        FastNoiseLite.Vector3 v = new FastNoiseLite.Vector3(x, y, z);
        caveWrapNoise.DomainWarp(v);
        float n = Math.abs(caveNoise.GetNoise(v.x, v.y, v.z));
        return n < 0.05f && y > 3 && y < 75;
    }
}
