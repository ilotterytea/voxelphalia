package kz.ilotterytea.voxelphalia.level.generators;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Vector3;
import kz.ilotterytea.voxelphalia.VoxelphaliaGame;
import kz.ilotterytea.voxelphalia.entities.SaplingEntity;
import kz.ilotterytea.voxelphalia.entities.mobs.MobEntity;
import kz.ilotterytea.voxelphalia.entities.mobs.MobType;
import kz.ilotterytea.voxelphalia.entities.mobs.friendly.MobPig;
import kz.ilotterytea.voxelphalia.entities.mobs.hostile.MobFish;
import kz.ilotterytea.voxelphalia.entities.mobs.neutral.MobPenguin;
import kz.ilotterytea.voxelphalia.level.Level;
import kz.ilotterytea.voxelphalia.utils.Identifier;
import kz.ilotterytea.voxelphalia.utils.registries.VoxelRegistry;
import kz.ilotterytea.voxelphalia.voxels.Voxel;

import java.util.Random;

public class IslandTerrainGenerator implements TerrainGenerator {
    private final FastNoiseLite heightNoise, detailNoise, treeNoise;

    private final Voxel mantle, stone, water, sand, dirt, grass, snow;

    public IslandTerrainGenerator(int seed) {
        heightNoise = new FastNoiseLite(seed);
        heightNoise.SetNoiseType(FastNoiseLite.NoiseType.Perlin);
        heightNoise.SetFrequency(0.05f);

        detailNoise = new FastNoiseLite(seed + 1);
        detailNoise.SetNoiseType(FastNoiseLite.NoiseType.Perlin);
        detailNoise.SetFrequency(0.2f);

        treeNoise = new FastNoiseLite(seed + 4);
        treeNoise.SetNoiseType(FastNoiseLite.NoiseType.Perlin);
        treeNoise.SetFrequency(0.6f);

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
}
