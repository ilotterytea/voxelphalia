package kz.ilotterytea.voxelphalia.level;

import com.github.czyzby.noise4j.map.Grid;
import com.github.czyzby.noise4j.map.generator.noise.NoiseGenerator;

public class TerrainGenerator {
    public static void generateTerrain(Level level, int seed) {
        Grid grid = generateGrid(level.getWidthInVoxels(), level.getDepthInVoxels(), seed);
        applyGrid(level, grid);
    }

    private static void applyGrid(Level level, Grid grid) {
        final float WATER_HEIGHT = 20f;
        final float SAND_LEVEL = 0.35f;
        final float GRASS_LEVEL = 0.6f;
        final float ROCK_LEVEL = 0.7f;
        final int DIRT_DEPTH = 4;

        for (int x = 0; x < grid.getWidth(); x++) {
            for (int z = 0; z < grid.getHeight(); z++) {
                float heightValue = grid.get(x, z) * level.getHeightInVoxels();
                int terrainHeight = (int) heightValue;

                // base layer
                for (int y = 0; y < terrainHeight; y++) {
                    if (y == 0) {
                        level.placeVoxel(VoxelType.MANTLE, x, y, z);
                    } else {
                        level.placeVoxel(VoxelType.ROCK, x, y, z);
                    }
                }

                // water
                if (terrainHeight < WATER_HEIGHT) {
                    // filling with the water
                    for (int y = terrainHeight; y <= WATER_HEIGHT; y++) {
                        level.placeVoxel(VoxelType.WATER, x, y, z);
                    }
                    for (int i = 0; i <= DIRT_DEPTH; i++) {
                        level.placeVoxel(VoxelType.SAND, x, terrainHeight - i, z);
                    }
                }
                //beach
                else if (terrainHeight < SAND_LEVEL * level.getHeightInVoxels()) {
                    for (int i = 0; i <= DIRT_DEPTH; i++) {
                        level.placeVoxel(VoxelType.SAND, x, terrainHeight - i, z);
                    }
                }
                // grass
                else if (terrainHeight < GRASS_LEVEL * level.getHeightInVoxels()) {
                    level.placeVoxel(VoxelType.GRASS, x, terrainHeight, z);
                    for (int i = 1; i <= DIRT_DEPTH; i++) {
                        level.placeVoxel(VoxelType.DIRT, x, terrainHeight - i, z);
                    }
                    level.placeVoxel(VoxelType.ROCK, x, terrainHeight - DIRT_DEPTH - 1, z);
                }
                // rock hills
                else if (terrainHeight < ROCK_LEVEL * level.getHeightInVoxels()) {
                    level.placeVoxel(VoxelType.ROCK, x, terrainHeight, z);
                }
                // snowy peaks
                else {
                    level.placeVoxel(VoxelType.SNOW, x, terrainHeight, z);
                    level.placeVoxel(VoxelType.ROCK, x, terrainHeight - 1, z);
                }
            }
        }
    }

    private static Grid generateGrid(int width, int height, int seed) {
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
}
