package kz.ilotterytea.voxelphalia.level;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector3;
import com.github.czyzby.noise4j.map.Grid;
import com.github.czyzby.noise4j.map.generator.noise.NoiseGenerator;
import kz.ilotterytea.voxelphalia.VoxelphaliaGame;
import kz.ilotterytea.voxelphalia.entities.SaplingEntity;
import kz.ilotterytea.voxelphalia.entities.mobs.MobEntity;
import kz.ilotterytea.voxelphalia.entities.mobs.MobType;
import kz.ilotterytea.voxelphalia.entities.mobs.friendly.MobPig;
import kz.ilotterytea.voxelphalia.entities.mobs.hostile.MobFish;
import kz.ilotterytea.voxelphalia.entities.mobs.neutral.MobPenguin;
import kz.ilotterytea.voxelphalia.utils.registries.VoxelRegistry;
import kz.ilotterytea.voxelphalia.voxels.Voxel;

import java.util.Random;

public class TerrainGenerator {
    public static void applyGrid(Level level, Grid grid) {
        final float WATER_HEIGHT = 20f;
        final float SAND_LEVEL = 0.35f;
        final float GRASS_LEVEL = 0.6f;
        final float ROCK_LEVEL = 0.7f;
        final int DIRT_DEPTH = 4;

        VoxelRegistry voxelRegistry = VoxelphaliaGame.getInstance().getVoxelRegistry();
        Voxel mantle = voxelRegistry.getEntryById((byte) 5);
        Voxel stone = voxelRegistry.getEntryById((byte) 2);
        Voxel water = voxelRegistry.getEntryById((byte) 7);
        Voxel sand = voxelRegistry.getEntryById((byte) 4);
        Voxel dirt = voxelRegistry.getEntryById((byte) 3);
        Voxel grass = voxelRegistry.getEntryById((byte) 1);
        Voxel snow = voxelRegistry.getEntryById((byte) 6);

        for (int x = 0; x < grid.getWidth(); x++) {
            for (int z = 0; z < grid.getHeight(); z++) {
                float heightValue = grid.get(x, z) * level.getHeightInVoxels();
                int terrainHeight = (int) heightValue;

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
                else if (terrainHeight < SAND_LEVEL * level.getHeightInVoxels()) {
                    for (int i = 0; i <= DIRT_DEPTH; i++) {
                        level.placeVoxel(sand, x, terrainHeight - i, z);
                    }
                }
                // grass
                else if (terrainHeight < GRASS_LEVEL * level.getHeightInVoxels()) {
                    level.placeVoxel(grass, x, terrainHeight, z);
                    for (int i = 1; i <= DIRT_DEPTH; i++) {
                        level.placeVoxel(dirt, x, terrainHeight - i, z);
                    }
                    level.placeVoxel(stone, x, terrainHeight - DIRT_DEPTH - 1, z);
                }
                // rock hills
                else if (terrainHeight < ROCK_LEVEL * level.getHeightInVoxels()) {
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

    public static Grid generateGrid(int width, int height, int seed) {
        NoiseGenerator noiseGenerator = new NoiseGenerator();
        Grid grid = new Grid(width, height);
        noiseStage(grid, noiseGenerator, 32, 0.6f, seed);
        noiseStage(grid, noiseGenerator, 16, 0.2f, seed);
        noiseStage(grid, noiseGenerator, 8, 0.1f, seed);
        noiseStage(grid, noiseGenerator, 4, 0.1f, seed);
        noiseStage(grid, noiseGenerator, 1, 0.05f, seed);
        return grid;
    }

    private static void noiseStage(final Grid grid, final NoiseGenerator noiseGenerator, final int radius,
                                   final float modifier, int seed) {
        noiseGenerator.setRadius(radius);
        noiseGenerator.setModifier(modifier);
        noiseGenerator.setSeed(seed);
        noiseGenerator.generate(grid);
    }

    public static void generateMinerals(Level level, Voxel mineral, int minRadius, int minY, int minAmount, int seed) {
        Random random = new Random(seed);

        for (int i = 0; i < random.nextInt(minAmount, (int) Math.pow(minAmount, 2)); i++) {
            int x, y, z, attempts = 0;
            boolean pointFound;

            do {
                x = MathUtils.random(0, level.getWidthInVoxels());
                z = MathUtils.random(0, level.getDepthInVoxels());
                y = MathUtils.random(minY, minY + 5);

                pointFound = level.getVoxel(x, y, z) == 2;

                attempts++;
                if (pointFound) break;
            } while (attempts < 20);

            if (!pointFound) continue;

            int radius = MathUtils.random(minRadius, minRadius + 1);

            for (int my = y - radius; my <= y + radius; my++) {
                for (int mx = x - radius; mx <= x + radius; mx++) {
                    for (int mz = z - radius; mz <= z + radius; mz++) {
                        // sphere check
                        if ((mx - x) * (mx - x) + (my - y) * (my - y) + (mz - z) * (mz - z) > radius * radius) {
                            continue;
                        }

                        if (MathUtils.random(0, 100) > 20 && level.getVoxel(mx, my, mz) == 2) {
                            level.placeVoxel(mineral, mx, my, mz);
                        }
                    }
                }
            }
        }

        Gdx.app.log("TerrainGenerator", "Generated " + mineral + " minerals");
    }

    public static void generateMobs(Level level, MobType mob, int seed) {
        Random random = new Random(seed + 18);

        try {
            int amount = random.nextInt(50, 90);
            for (int i = 0; i < amount; i++) {
                int x = random.nextInt(20, level.getWidthInVoxels() - 40);
                int z = random.nextInt(20, level.getDepthInVoxels() - 40);

                MobEntity entity = switch (mob) {
                    case PIG -> new MobPig();
                    case FISH -> new MobFish();
                    case PENGUIN -> new MobPenguin();
                };

                entity.setPosition(x, level.getHighestY(x, z), z);
                level.addEntity(entity);
            }

            Gdx.app.log("TerrainGenerator", "Generated " + amount + " " + mob + " mob");
        } catch (Exception e) {
            Gdx.app.log("TerrainGenerator", "Failed to create a mob");
        }
    }

    public static void generateTrees(Level level, int minAmount, int seed) {
        Random random = new Random(seed);

        for (int i = 0; i < random.nextInt(minAmount, (int) Math.pow(minAmount, 2)); i++) {
            int x = random.nextInt(0, level.getWidthInVoxels());
            int z = random.nextInt(0, level.getDepthInVoxels());
            int y = (int) level.getHighestY(x, z);

            VoxelRegistry voxelRegistry = VoxelphaliaGame.getInstance().getVoxelRegistry();

            Voxel voxelBelow = voxelRegistry.getEntryById(level.getVoxel(x, y - 1, z));

            if (voxelBelow.getId() != 1) {
                continue;
            }

            SaplingEntity entity = new SaplingEntity(new Vector3(x, y, z), 0);
            level.addEntity(entity);
        }
    }
}
