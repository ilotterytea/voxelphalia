package kz.ilotterytea.voxelphalia.level.generators;

import kz.ilotterytea.voxelphalia.entities.mobs.MobType;
import kz.ilotterytea.voxelphalia.level.Level;

public interface TerrainGenerator {
    void generateLandscape(Level level);

    void generateTrees(Level level);

    void generateMobs(Level level, MobType mob);

    void generateMinerals(Level level);
}
