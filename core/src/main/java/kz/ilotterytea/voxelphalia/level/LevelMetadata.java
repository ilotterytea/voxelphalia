package kz.ilotterytea.voxelphalia.level;

public class LevelMetadata {
    public enum LevelGeneratorType {
        LIMITED,
        ISLAND
    }

    public enum LevelGameMode {
        SURVIVAL
    }

    protected final int width, height, depth;
    protected final int seed;
    protected final String name;
    protected long lastTimeOpened;

    protected final LevelGameMode gameMode;
    protected final LevelGeneratorType generatorType;

    public LevelMetadata(String name, int width, int height, int depth, int seed, LevelGeneratorType generatorType, LevelGameMode gameMode) {
        this.width = width;
        this.height = height;
        this.depth = depth;
        this.seed = seed;
        this.name = name;
        this.generatorType = generatorType;
        this.gameMode = gameMode;
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

    public Level.LevelGameMode getGameMode() {
        return gameMode;
    }

    public Level.LevelGeneratorType getGeneratorType() {
        return generatorType;
    }
}
