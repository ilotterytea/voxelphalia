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

    private final TerrainGeneratorSettings settings;

    public IslandTerrainGenerator(int seed) {
        this(seed, TerrainGeneratorSettings.defaultIslandSettings());
    }

    public IslandTerrainGenerator(int seed, TerrainGeneratorSettings settings) {
        caveChunks = new Array<>();
        this.settings = settings;

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
                if (terrainHeight < settings.waterHeight) {
                    // filling with the water
                    for (int y = terrainHeight; y <= settings.waterHeight; y++) {
                        level.placeVoxel(water, x, y, z);
                    }
                    for (int i = 0; i <= settings.dirtDepth; i++) {
                        level.placeVoxel(sand, x, terrainHeight - i, z);
                    }
                }
                //beach
                else if (terrainHeight < settings.sandLevel * settings.height) {
                    for (int i = 0; i <= settings.dirtDepth; i++) {
                        level.placeVoxel(sand, x, terrainHeight - i, z);
                    }
                }
                // grass
                else if (terrainHeight < settings.grassLevel * settings.height) {
                    level.placeVoxel(grass, x, terrainHeight, z);
                    for (int i = 1; i <= settings.dirtDepth; i++) {
                        level.placeVoxel(dirt, x, terrainHeight - i, z);
                    }
                    level.placeVoxel(stone, x, terrainHeight - settings.dirtDepth - 1, z);
                }
                // rock hills
                else if (terrainHeight < settings.rockLevel * settings.height) {
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

                    if (y <= settings.waterHeight) {
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
            for (int i = 0; i < settings.mobAmount; i++) {
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

        int[] generatedAmount = new int[settings.minerals.size];

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
                        for (int i = 0; i < settings.minerals.size; i++) {
                            int generatedMinerals = generateMineral(chunk, level, settings.minerals.get(i), level.getSeed() + 121);
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
            Gdx.app.log(getClass().getSimpleName(), "Generated " + generatedAmount[i] + " " + settings.minerals.get(i));
        }
    }

    private int generateMineral(Chunk chunk, Level level, TerrainGeneratorSettings.Mineral mineral, int seed) {
        if (chunk.getOffset().y < mineral.minY() || chunk.getOffset().y > mineral.maxY()) {
            return 0;
        }

        Voxel voxel = VoxelphaliaGame.getInstance().getVoxelRegistry().getEntry(mineral.identifier());

        Random random = new Random(seed + mineral.identifier().hashCode());

        Array<Vector3> positions = new Array<>();

        // determining potential ore positions
        for (int x = 0; x < Chunk.SIZE; x++) {
            int lx = (int) (chunk.getOffset().x + x);
            for (int z = 0; z < Chunk.SIZE; z++) {
                int lz = (int) (chunk.getOffset().z + z);
                for (int y = 0; y < Chunk.SIZE; y++) {
                    int ly = (int) (chunk.getOffset().y + y);
                    Identifier v = level.getVoxel(lx, ly, lz);
                    if (ly < mineral.minY() || ly > mineral.maxY() || v == null || !v.equals("stone")) continue;

                    float n = oreNoise.GetNoise(lx, ly, lz);
                    if (n < mineral.threshold()) continue;

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
                level.placeVoxel(voxel, c.x, c.y, c.z);
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
        int attempts = settings.treeAmount, success = 0;

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

    @Override
    public TerrainGeneratorSettings getSettings() {
        return settings;
    }
}
