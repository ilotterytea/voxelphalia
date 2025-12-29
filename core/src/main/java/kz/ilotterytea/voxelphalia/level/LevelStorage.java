package kz.ilotterytea.voxelphalia.level;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.JsonReader;
import com.badlogic.gdx.utils.JsonValue;
import kz.ilotterytea.voxelphalia.VoxelphaliaConstants;
import kz.ilotterytea.voxelphalia.VoxelphaliaGame;
import kz.ilotterytea.voxelphalia.entities.*;
import kz.ilotterytea.voxelphalia.inventory.Inventory;
import kz.ilotterytea.voxelphalia.items.Weapon;
import kz.ilotterytea.voxelphalia.utils.Identifier;
import kz.ilotterytea.voxelphalia.utils.registries.VoxelRegistry;
import kz.ilotterytea.voxelphalia.voxels.Voxel;
import kz.ilotterytea.voxelphalia.voxels.specialvoxels.ChestVoxel;
import kz.ilotterytea.voxelphalia.voxels.specialvoxels.FurnaceVoxel;
import kz.ilotterytea.voxelphalia.voxels.specialvoxels.WorkbenchVoxel;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

public class LevelStorage {
    public static void save(Level level) {
        String folderName = VoxelphaliaConstants.Paths.LEVEL_DIRECTORY + "/" + level.name;

        File folder;

        if (!(folder = new File(folderName + "/regions")).exists()) {
            folder.mkdirs();
        }

        // level
        Gdx.app.log("LevelStorage:" + level.name, "Saving level.dat...");

        JsonValue levelJson = new JsonValue(JsonValue.ValueType.object);

        levelJson.addChild("seed", new JsonValue(level.seed));
        levelJson.addChild("width", new JsonValue(level.width));
        levelJson.addChild("height", new JsonValue(level.height));
        levelJson.addChild("depth", new JsonValue(level.depth));
        levelJson.addChild("name", new JsonValue(level.name));
        levelJson.addChild("generatorType", new JsonValue(level.generatorType.toString()));
        levelJson.addChild("gameMode", new JsonValue(level.gameMode.toString()));

        level.setLastTimeOpened(System.currentTimeMillis());
        levelJson.addChild("lastOpened", new JsonValue(level.lastTimeOpened));

        try (FileOutputStream fos = new FileOutputStream(folderName + "/level.dat")) {
            GZIPOutputStream gos = new GZIPOutputStream(fos);
            gos.write(levelJson.toString().getBytes(StandardCharsets.UTF_8));
            gos.close();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        // entities
        Gdx.app.log("LevelStorage:" + level.name, "Saving entities.dat...");
        JsonValue entitiesJson = new JsonValue(JsonValue.ValueType.array);

        for (Entity entity : level.getEntities()) {
            JsonValue entityJson = new JsonValue(JsonValue.ValueType.object);

            entityJson.addChild("type", new JsonValue(entity.getClass().getSimpleName()));

            switch (entity) {
                case SaplingEntity e -> {
                    entityJson.addChild("lifeTime", new JsonValue(e.getLifeTime()));
                    entityJson.addChild("adultLifeTime", new JsonValue(e.getAdultLifeTime()));
                }
                case ParticleEntity e -> {
                    entityJson.addChild("velocityX", new JsonValue(e.getVelocity().x));
                    entityJson.addChild("velocityY", new JsonValue(e.getVelocity().y));
                    entityJson.addChild("color", new JsonValue(e.getColor().toIntBits()));
                }
                case DropEntity e -> {
                    entityJson.addChild("dropId", new JsonValue(e.getIdentifier().getFullName()));
                    entityJson.addChild("dropAmount", new JsonValue(e.getAmount()));
                    entityJson.addChild("lifeTime", new JsonValue(e.getLifeTime()));
                }
                case BulletEntity e -> {
                    JsonValue initialPositionJson = new JsonValue(JsonValue.ValueType.array);
                    initialPositionJson.addChild(new JsonValue(e.getInitialPosition().x));
                    initialPositionJson.addChild(new JsonValue(e.getInitialPosition().y));
                    initialPositionJson.addChild(new JsonValue(e.getInitialPosition().z));
                    entityJson.addChild("initialPosition", initialPositionJson);

                    JsonValue initialDirectionJson = new JsonValue(JsonValue.ValueType.array);
                    initialDirectionJson.addChild(new JsonValue(e.getInitialDirection().x));
                    initialDirectionJson.addChild(new JsonValue(e.getInitialDirection().y));
                    initialDirectionJson.addChild(new JsonValue(e.getInitialDirection().z));
                    entityJson.addChild("initialDirection", initialDirectionJson);

                    entityJson.addChild("weaponId", new JsonValue(e.getWeapon().getId().getFullName()));
                }
                case LivingEntity e -> {
                    if (e instanceof PlayerEntity ee) {
                        JsonValue spawnPointJson = new JsonValue(JsonValue.ValueType.array);
                        spawnPointJson.addChild(new JsonValue(ee.getSpawnPoint().x));
                        spawnPointJson.addChild(new JsonValue(ee.getSpawnPoint().y));
                        spawnPointJson.addChild(new JsonValue(ee.getSpawnPoint().z));
                        entityJson.addChild("spawnPoint", spawnPointJson);

                        entityJson.addChild("energy", new JsonValue(ee.getEnergy()));
                        entityJson.addChild("maxEnergy", new JsonValue(ee.getMaxEnergy()));

                        entityJson.addChild("recoilDelayTime", new JsonValue(ee.getRecoilDelayTime()));

                        JsonValue inventoryDataJson = new JsonValue(JsonValue.ValueType.array);

                        for (Inventory.Slot slot : ee.getInventory().getSlots()) {
                            JsonValue slotJson = new JsonValue(JsonValue.ValueType.object);
                            slotJson.addChild("id", new JsonValue(slot.id != null ?
                                slot.id.getFullName() :
                                null));
                            slotJson.addChild("quantity", new JsonValue(slot.quantity));
                            slotJson.addChild("size", new JsonValue(slot.size));
                            inventoryDataJson.addChild(slotJson);
                        }

                        entityJson.addChild("inventory", inventoryDataJson);
                    }

                    entityJson.addChild("health", new JsonValue(e.getHealth()));
                    entityJson.addChild("maxHealth", new JsonValue(e.getMaxHealth()));
                    entityJson.addChild("damage", new JsonValue(e.getDamage()));
                    entityJson.addChild("dead", new JsonValue(e.isDead()));
                }
                default -> {
                }
            }

            JsonValue positionJson = new JsonValue(JsonValue.ValueType.array);
            positionJson.addChild(new JsonValue(entity.getPosition().x));
            positionJson.addChild(new JsonValue(entity.getPosition().y));
            positionJson.addChild(new JsonValue(entity.getPosition().z));
            entityJson.addChild("position", positionJson);

            JsonValue directionJson = new JsonValue(JsonValue.ValueType.array);
            directionJson.addChild(new JsonValue(entity.getDirection().x));
            directionJson.addChild(new JsonValue(entity.getDirection().y));
            directionJson.addChild(new JsonValue(entity.getDirection().z));
            entityJson.addChild("direction", directionJson);

            entitiesJson.addChild(entityJson);
        }

        try (FileOutputStream fos = new FileOutputStream(folderName + "/entities.dat")) {
            GZIPOutputStream gos = new GZIPOutputStream(fos);
            gos.write(entitiesJson.toString().getBytes(StandardCharsets.UTF_8));
            gos.close();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        // chunks
        Gdx.app.log("LevelStorage:" + level.name, "Saving chunk regions...");
        for (Chunk chunk : level.chunks) {
            if (!chunk.modified) continue;
            JsonValue chunkJson = new JsonValue(JsonValue.ValueType.object);

            // voxels
            JsonValue voxelJson = new JsonValue(JsonValue.ValueType.array);
            for (Identifier identifier : chunk.voxels) {
                if (identifier == null) {
                    voxelJson.addChild(new JsonValue(JsonValue.ValueType.nullValue));
                    continue;
                }
                voxelJson.addChild(new JsonValue(identifier.getFullName()));
            }
            chunkJson.addChild("voxels", voxelJson);

            // saving voxel states
            JsonValue voxelStatesJson = new JsonValue(JsonValue.ValueType.array);
            for (Voxel voxel : chunk.voxelStates) {
                if (voxel == null) {
                    voxelStatesJson.addChild(new JsonValue(JsonValue.ValueType.nullValue));
                    continue;
                }
                JsonValue voxelDataJson = new JsonValue(JsonValue.ValueType.object);

                voxelDataJson.addChild("id", new JsonValue(voxel.getId().getFullName()));

                switch (voxel) {
                    case ChestVoxel v -> {
                        voxelDataJson.addChild("type", new JsonValue("chest"));
                        JsonValue chestDataJson = new JsonValue(JsonValue.ValueType.array);

                        for (Inventory.Slot slot : v.getInventory().getSlots()) {
                            JsonValue slotJson = new JsonValue(JsonValue.ValueType.object);
                            slotJson.addChild("id", new JsonValue(slot.id != null ?
                                slot.id.getFullName() :
                                null));
                            slotJson.addChild("quantity", new JsonValue(slot.quantity));
                            slotJson.addChild("size", new JsonValue(slot.size));
                            chestDataJson.addChild(slotJson);
                        }

                        voxelDataJson.addChild("inventory", chestDataJson);
                    }
                    case WorkbenchVoxel ignored -> voxelDataJson.addChild("type", new JsonValue("workbench"));
                    case FurnaceVoxel v -> {
                        voxelDataJson.addChild("type", new JsonValue("furnace"));
                        voxelDataJson.addChild("smeltingVoxel",
                            new JsonValue(v.getSmeltId() != null ?
                                v.getSmeltId().getFullName() :
                                null
                            )
                        );
                    }
                    default -> voxelDataJson.addChild("type", new JsonValue("default"));
                }

                voxelStatesJson.addChild(voxelDataJson);
            }
            chunkJson.addChild("voxelStates", voxelStatesJson);

            // other chunk parameters
            chunkJson.addChild("size", new JsonValue(chunk.size));

            JsonValue offsetValue = new JsonValue(JsonValue.ValueType.array);
            offsetValue.addChild(new JsonValue(chunk.getOffset().x));
            offsetValue.addChild(new JsonValue(chunk.getOffset().y));
            offsetValue.addChild(new JsonValue(chunk.getOffset().z));
            chunkJson.addChild("offset", offsetValue);

            try (FileOutputStream fos = new FileOutputStream(folderName + String.format("/regions/region.%s.%s.%s.vpr",
                Math.round(chunk.getOffset().x),
                Math.round(chunk.getOffset().y),
                Math.round(chunk.getOffset().z)
            ))) {
                GZIPOutputStream gos = new GZIPOutputStream(fos);
                gos.write(chunkJson.toString().getBytes(StandardCharsets.UTF_8));
                gos.close();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }

        Gdx.app.log("LevelStorage:" + level.name, "Finished!");
    }

    public static Level loadAll(String name) {
        Level level = loadLevel(name);
        if (level == null) return null;

        level.entities.addAll(loadEntities(name));

        Array<Chunk> chunks = loadChunkRegions(name);
        if (chunks != null) {
            for (Chunk chunk : chunks) {
                int index = level.getChunkIndex((int) chunk.offset.x, (int) chunk.offset.y, (int) chunk.offset.z);
                level.chunks.set(index, chunk);
            }
        }

        Gdx.app.log("LevelStorage:" + name, "Finished!");

        return level;
    }

    public static Level loadLevel(String levelName) {
        String folderName = VoxelphaliaConstants.Paths.LEVEL_DIRECTORY + "/" + levelName;

        if (!new File(folderName).exists()) {
            return null;
        }

        Level level;

        Gdx.app.log("LevelStorage", "Loading level.dat...");

        // loading level
        try (FileInputStream fis = new FileInputStream(folderName + "/level.dat")) {
            GZIPInputStream gis = new GZIPInputStream(fis);
            String contents = new String(gis.readAllBytes(), StandardCharsets.UTF_8);
            JsonValue json = new JsonReader().parse(contents);

            level = new Level(
                json.getString("name"),
                json.getInt("width"),
                json.getInt("height"),
                json.getInt("depth"),
                json.getInt("seed"),
                Level.LevelGeneratorType.valueOf(json.getString("generatorType")),
                Level.LevelGameMode.valueOf(json.getString("gameMode"))
            );

            level.setLastTimeOpened(json.getLong("lastOpened"));

            gis.close();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        return level;
    }

    public static ArrayList<Entity> loadEntities(String levelName) {
        String folderName = VoxelphaliaConstants.Paths.LEVEL_DIRECTORY + "/" + levelName;
        if (!new File(folderName).exists()) {
            return null;
        }

        ArrayList<Entity> entities = new ArrayList<>();

        Gdx.app.log("LevelStorage:" + levelName, "Loading entities.dat...");

        // loading entities
        try (FileInputStream fis = new FileInputStream(folderName + "/entities.dat")) {
            GZIPInputStream gis = new GZIPInputStream(fis);
            String contents = new String(gis.readAllBytes(), StandardCharsets.UTF_8);

            JsonValue jsonValue = new JsonReader().parse(contents);

            for (JsonValue entityJson : jsonValue.iterator()) {
                Entity entity = null;

                String entityType = entityJson.getString("type");

                switch (entityType) {
                    case "SaplingEntity" -> {
                        SaplingEntity e = new SaplingEntity(new Vector3(), entityJson.getFloat("adultLifeTime"));
                        e.setLifeTime(entityJson.getFloat("lifeTime"));
                        entity = e;
                    }
                    case "ParticleEntity" -> entity = new ParticleEntity(
                        new Color(entityJson.getInt("color")),
                        entityJson.getFloat("velocityX"),
                        entityJson.getFloat("velocityY")
                    );
                    case "DropEntity" -> {
                        DropEntity e = new DropEntity(
                            VoxelphaliaGame.getInstance().getIdentifierRegistry()
                                .getEntry(entityJson.getString("dropId")),
                            entityJson.getByte("dropAmount")
                        );
                        e.setLifeTime(entityJson.getFloat("lifeTime"));
                        entity = e;
                    }
                    case "CorpseEntity" -> {
                        // TODO: When LivingEntity will use Identifiers, we should change this.
                        entity = new CorpseEntity(new LivingEntity());
                    }
                    case "BulletEntity" -> {
                        JsonValue initialPositionJson = entityJson.get("initialPosition");
                        JsonValue initialDirectionJson = entityJson.get("initialDirection");

                        entity = new BulletEntity(
                            (Weapon) VoxelphaliaGame.getInstance().getItemRegistry()
                                .getEntry(entityJson.getString("weaponId")),
                            new Vector3(
                                initialPositionJson.getFloat(0),
                                initialPositionJson.getFloat(1),
                                initialPositionJson.getFloat(2)
                            ),
                            new Vector3(
                                initialDirectionJson.getFloat(0),
                                initialDirectionJson.getFloat(1),
                                initialDirectionJson.getFloat(2)
                            )
                        );
                    }
                    case "LivingEntity", "PlayerEntity" -> {
                        LivingEntity e;

                        if (entityType.equals("PlayerEntity")) {
                            JsonValue spawnPointJson = entityJson.get("spawnPoint");

                            PlayerEntity ee = new PlayerEntity(
                                new Vector3(
                                    spawnPointJson.getFloat(0),
                                    spawnPointJson.getFloat(1),
                                    spawnPointJson.getFloat(2)
                                ),
                                null
                            );
                            e = ee;

                            ee.setEnergy(entityJson.getFloat("energy"));
                            ee.setMaxEnergy(entityJson.getFloat("maxEnergy"));
                            ee.setRecoilDelayTime(entityJson.getFloat("recoilDelayTime"));

                            JsonValue inventoryDataJson = entityJson.get("inventory");

                            for (int i = 0; i < inventoryDataJson.size; i++) {
                                JsonValue slotJson = inventoryDataJson.get(i);
                                Inventory.Slot slot = new Inventory.Slot(
                                    slotJson.has("id") ? VoxelphaliaGame.getInstance().getIdentifierRegistry()
                                        .getEntry(slotJson.getString("id")) : null,
                                    slotJson.getByte("quantity"),
                                    slotJson.getByte("size")
                                );
                                ee.getInventory().getSlots()[i] = slot;
                            }
                        } else {
                            e = new LivingEntity();
                        }

                        e.setHealth(entityJson.getInt("health"));
                        e.setMaxHealth(entityJson.getInt("maxHealth"));
                        e.setDamage(entityJson.getInt("damage"));
                        e.setDead(entityJson.getBoolean("dead"));

                        entity = e;
                    }
                    default -> {
                    }
                }

                if (entity == null) continue;

                JsonValue positionJson = entityJson.get("position");
                entity.setPosition(
                    positionJson.getFloat(0),
                    positionJson.getFloat(1),
                    positionJson.getFloat(2)
                );

                JsonValue directionJson = entityJson.get("direction");
                entity.setDirection(
                    directionJson.getFloat(0),
                    directionJson.getFloat(1),
                    directionJson.getFloat(2)
                );

                entities.add(entity);
            }

            gis.close();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        return entities;
    }

    public static Array<Chunk> loadChunkRegions(String levelName) {
        String folderName = VoxelphaliaConstants.Paths.LEVEL_DIRECTORY + "/" + levelName;
        if (!new File(folderName).exists()) {
            return null;
        }

        Gdx.app.log("LevelStorage:" + levelName, "Loading chunk regions...");

        Array<Chunk> chunks = new Array<>();

        // loading regions
        File regionFolder = new File(folderName + "/regions");
        for (File file : regionFolder.listFiles()) {
            try (FileInputStream fis = new FileInputStream(file)) {
                GZIPInputStream gis = new GZIPInputStream(fis);
                String contents = new String(gis.readAllBytes(), StandardCharsets.UTF_8);
                JsonValue json = new JsonReader().parse(contents);

                JsonValue offsetJson = json.get("offset");

                Chunk chunk = new Chunk(
                    json.getInt("size"),
                    new Vector3(
                        offsetJson.getFloat(0),
                        offsetJson.getFloat(1),
                        offsetJson.getFloat(2)
                    )
                );

                // voxels
                VoxelRegistry voxelRegistry = VoxelphaliaGame.getInstance().getVoxelRegistry();
                JsonValue voxelsJson = json.get("voxels");

                for (int i = 0; i < voxelsJson.size; i++) {
                    JsonValue voxelJson = voxelsJson.get(i);
                    Identifier id = null;

                    if (voxelJson != null && !voxelJson.isNull()) {
                        if (voxelRegistry.containsEntry(voxelJson.asString())) {
                            id = voxelRegistry.getEntry(voxelJson.asString()).getId();
                        } else {
                            id = voxelRegistry.getEntry("missing_voxel").getId();
                        }
                    }

                    chunk.voxels[i] = id;
                }

                // saving voxel states
                JsonValue voxelStatesJson = json.get("voxelStates");

                for (int i = 0; i < voxelStatesJson.size; i++) {
                    JsonValue voxelJson = voxelStatesJson.get(i);
                    if (voxelJson == null || voxelJson.isNull()) continue;

                    Voxel voxel;

                    if (voxelRegistry.containsEntry(voxelJson.getString("id"))) {
                        voxel = voxelRegistry.getEntry(voxelJson.getString("id")).clone();

                        switch (voxel) {
                            case ChestVoxel v -> {
                                JsonValue inventoryJson = voxelJson.get("inventory");

                                for (int slotI = 0; slotI < inventoryJson.size; slotI++) {
                                    JsonValue slotJson = inventoryJson.get(slotI);

                                    Inventory.Slot slot = new Inventory.Slot(
                                        slotJson.has("id") ?
                                            VoxelphaliaGame.getInstance().getIdentifierRegistry()
                                                .getEntry(slotJson.getString("id"))
                                            : null,
                                        slotJson.getByte("quantity"),
                                        slotJson.getByte("size")
                                    );

                                    v.getInventory().getSlots()[slotI] = slot;
                                }
                            }
                            case FurnaceVoxel v -> {
                                String smeltingVoxelString = voxelJson.getString("smeltingVoxel");

                                if (smeltingVoxelString != null) {
                                    v.setVoxelToSmelt(Identifier.of(smeltingVoxelString));
                                }
                            }
                            default -> {
                            }
                        }
                    } else {
                        voxel = voxelRegistry.getEntry("missing_voxel").clone();
                    }

                    chunk.voxelStates[i] = voxel;
                }
                gis.close();

                chunk.setLock(true);
                chunks.add(chunk);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }

        return chunks;
    }

    public static Array<Level> loadAllLevelsData() {
        Array<Level> levels = new Array<>();

        FileHandle rootDirectory = Gdx.files.absolute(VoxelphaliaConstants.Paths.LEVEL_DIRECTORY);
        if (!rootDirectory.exists()) rootDirectory.mkdirs();
        else if (!rootDirectory.isDirectory()) throw new RuntimeException(rootDirectory.path() + " is not a directory");

        for (FileHandle folder : rootDirectory.list()) {
            if (!folder.isDirectory()) continue;
            Level level = loadLevel(folder.name());
            if (level == null) continue;
            levels.add(level);
        }

        return levels;
    }

    public static Array<String> loadAllLevelNames() {
        Array<String> names = new Array<>();

        FileHandle rootDirectory = Gdx.files.absolute(VoxelphaliaConstants.Paths.LEVEL_DIRECTORY);
        if (!rootDirectory.exists()) rootDirectory.mkdirs();
        else if (!rootDirectory.isDirectory()) throw new RuntimeException(rootDirectory.path() + " is not a directory");

        for (FileHandle folder : rootDirectory.list()) {
            if (!folder.isDirectory()) continue;
            names.add(folder.name());
        }

        return names;
    }
}
