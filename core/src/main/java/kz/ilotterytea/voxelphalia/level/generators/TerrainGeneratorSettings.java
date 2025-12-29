package kz.ilotterytea.voxelphalia.level.generators;

import com.badlogic.gdx.utils.Array;
import kz.ilotterytea.voxelphalia.level.Chunk;
import kz.ilotterytea.voxelphalia.level.Level;

public class TerrainGeneratorSettings {
    public record Mineral(String identifier, float minY, float maxY, float threshold) {
    }

    public float
        waterHeight, sandLevel, grassLevel,
        rockLevel, height;

    public int dirtDepth, treeAmount, mobAmount;

    public Array<Mineral> minerals = new Array<>();

    public TerrainGeneratorSettings() {
    }

    public static TerrainGeneratorSettings getDefaultSettings(Level.LevelGeneratorType generator) {
        return switch (generator) {
            case LIMITED -> defaultLimitedSettings();
            case ISLAND -> defaultIslandSettings();
            default -> throw new RuntimeException("Unimplemented generator type: " + generator);
        };
    }

    public static TerrainGeneratorSettings defaultIslandSettings() {
        TerrainGeneratorSettings s = new TerrainGeneratorSettings();
        s.waterHeight = 10f;
        s.sandLevel = 0.18f;
        s.grassLevel = 0.6f;
        s.rockLevel = 0.66f;
        s.dirtDepth = 4;
        s.treeAmount = 16000;
        s.mobAmount = 2000;
        s.height = 5 * Chunk.SIZE;

        s.minerals.addAll(
            new Mineral("coal_mineral", 40f, 100f, 0.6f),
            new Mineral("iron_mineral", 32f, 49f, 0.94f),
            new Mineral("gold_mineral", 21f, 33f, 0.85f),
            new Mineral("ruby_mineral", 40f, 100f, 0.9f),
            new Mineral("gemstone_mineral", 11f, 20f, 0.97f)
        );

        return s;
    }

    public static TerrainGeneratorSettings defaultLimitedSettings() {
        TerrainGeneratorSettings s = new TerrainGeneratorSettings();
        s.waterHeight = 20f;
        s.sandLevel = 0.35f;
        s.grassLevel = 0.6f;
        s.rockLevel = 0.7f;
        s.dirtDepth = 4;
        s.treeAmount = 760;
        s.mobAmount = 50;
        s.height = 4 * Chunk.SIZE;

        s.minerals.addAll(
            new Mineral("coal_mineral", 20f, 100f, 240f),
            new Mineral("iron_mineral", 15f, 30f, 120f),
            new Mineral("gold_mineral", 12f, 32f, 100f),
            new Mineral("ruby_mineral", 30f, 60f, 70f),
            new Mineral("gemstone_mineral", 4f, 11f, 40f)
        );

        return s;
    }
}
