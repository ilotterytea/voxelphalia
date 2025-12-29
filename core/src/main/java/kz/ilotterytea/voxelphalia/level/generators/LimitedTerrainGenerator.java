package kz.ilotterytea.voxelphalia.level.generators;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector3;
import com.github.czyzby.noise4j.map.Grid;
import com.github.czyzby.noise4j.map.generator.noise.NoiseGenerator;
import kz.ilotterytea.voxelphalia.VoxelphaliaConstants;
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

import java.util.*;

public class LimitedTerrainGenerator implements TerrainGenerator {
    private final TerrainGeneratorSettings settings;

    public LimitedTerrainGenerator() {
        this(TerrainGeneratorSettings.defaultLimitedSettings());
    }

    public LimitedTerrainGenerator(TerrainGeneratorSettings settings) {
        this.settings = settings;
    }

    @Override
    public void generateLandscape(Level level) {
        NoiseGenerator generator = new NoiseGenerator();
        Grid grid = new Grid(level.getWidthInVoxels(), level.getDepthInVoxels());
        noiseStage(grid, generator, 32, 0.6f, level.getSeed());
        noiseStage(grid, generator, 16, 0.2f, level.getSeed());
        noiseStage(grid, generator, 8, 0.1f, level.getSeed());
        noiseStage(grid, generator, 4, 0.1f, level.getSeed());
        noiseStage(grid, generator, 1, 0.05f, level.getSeed());

        VoxelRegistry voxelRegistry = VoxelphaliaGame.getInstance().getVoxelRegistry();
        Voxel mantle = voxelRegistry.getEntry("mantle");
        Voxel stone = voxelRegistry.getEntry("stone");
        Voxel water = voxelRegistry.getEntry("water");
        Voxel sand = voxelRegistry.getEntry("sand");
        Voxel dirt = voxelRegistry.getEntry("dirt");
        Voxel grass = voxelRegistry.getEntry("grass_voxel");
        Voxel snow = voxelRegistry.getEntry("snow");

        for (int x = 0; x < grid.getWidth(); x++) {
            for (int z = 0; z < grid.getHeight(); z++) {
                float heightValue = grid.get(x, z) * settings.height;
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
            }
        }
    }

    @Override
    public void generateTrees(Level level) {
        Random random = new Random(level.getSeed());

        for (int i = 0; i < random.nextInt(settings.treeAmount, (int) Math.pow(settings.treeAmount, 2)); i++) {
            int x = random.nextInt(0, level.getWidthInVoxels());
            int z = random.nextInt(0, level.getDepthInVoxels());
            int y = level.getHighestY(x, z);

            VoxelRegistry voxelRegistry = VoxelphaliaGame.getInstance().getVoxelRegistry();

            Voxel voxelBelow = voxelRegistry.getEntry(level.getVoxel(x, y - 1, z));

            if (!voxelBelow.getId().equals(VoxelphaliaConstants.Metadata.APP_ID + ":grass_voxel")) {
                continue;
            }

            SaplingEntity entity = new SaplingEntity(new Vector3(x, y, z), 0);
            level.addEntity(entity);
        }
    }

    @Override
    public void generateMobs(Level level, MobType mob) {
        Random random = new Random(level.getSeed() + 18 + mob.hashCode());

        try {
            int amount = random.nextInt(settings.mobAmount, settings.mobAmount * 2);
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

            Gdx.app.log(getClass().getSimpleName(), "Generated " + amount + " " + mob + " mob");
        } catch (Exception e) {
            Gdx.app.log(getClass().getSimpleName(), "Failed to create a mob");
        }
    }

    @Override
    public void generateMinerals(Level level) {
        int[] generatedAmount = new int[settings.minerals.size];

        for (int i = 0; i < settings.minerals.size; i++) {
            int generatedMinerals = generateMineral(level, settings.minerals.get(i), level.getSeed() + 121);
            generatedAmount[i] += generatedMinerals;
        }

        for (int i = 0; i < generatedAmount.length; i++) {
            Gdx.app.log(getClass().getSimpleName(), "Generated " + generatedAmount[i] + " " + settings.minerals.get(i).identifier());
        }
    }

    private int generateMineral(Level level, TerrainGeneratorSettings.Mineral mineral, int seed) {
        Random random = new Random(seed + mineral.identifier().hashCode());
        int t = (int) mineral.threshold(), amount = 0;
        Voxel voxel = VoxelphaliaGame.getInstance().getVoxelRegistry().getEntry(mineral.identifier());

        for (int i = 0; i < random.nextInt(t, MathUtils.nextPowerOfTwo(t)); i++) {
            int attempts = 1200;
            Vector3 p = new Vector3();
            Identifier point = null;

            while (attempts-- > 0) {
                p.x = random.nextInt(0, level.getWidthInVoxels());
                p.z = random.nextInt(0, level.getDepthInVoxels());
                p.y = random.nextInt((int) mineral.minY(), (int) mineral.maxY());
                point = level.getVoxel(p.x, p.y, p.z);

                if (point != null && point.equals("stone")) break;
            }

            if (point == null) continue;

            Queue<Vector3> queue = new ArrayDeque<>();
            Set<Vector3> visited = new HashSet<>();
            queue.add(p);

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

    private void noiseStage(final Grid grid, final NoiseGenerator noiseGenerator, final int radius,
                            final float modifier, int seed) {
        noiseGenerator.setRadius(radius);
        noiseGenerator.setModifier(modifier);
        noiseGenerator.setSeed(seed);
        noiseGenerator.generate(grid);
    }

    @Override
    public TerrainGeneratorSettings getSettings() {
        return settings;
    }
}
