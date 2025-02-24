package kz.ilotterytea.voxelphalia.utils.registries;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.JsonReader;
import com.badlogic.gdx.utils.JsonValue;
import kz.ilotterytea.voxelphalia.VoxelphaliaGame;
import kz.ilotterytea.voxelphalia.utils.Identifier;
import kz.ilotterytea.voxelphalia.voxels.Voxel;
import kz.ilotterytea.voxelphalia.voxels.VoxelMaterial;
import kz.ilotterytea.voxelphalia.voxels.VoxelMaterialType;
import kz.ilotterytea.voxelphalia.voxels.specialvoxels.ChestVoxel;
import kz.ilotterytea.voxelphalia.voxels.specialvoxels.FurnaceVoxel;
import kz.ilotterytea.voxelphalia.voxels.specialvoxels.WorkbenchVoxel;

public class VoxelRegistry extends Registry<Voxel> {
    @Override
    public void load() {
        FileHandle folder = Gdx.files.internal("data/voxels");

        for (FileHandle handle : folder.list()) {
            Identifier id = VoxelphaliaGame.getInstance()
                .getIdentifierRegistry()
                .getEntry(new Identifier(handle.nameWithoutExtension()));

            if (id == null) {
                Gdx.app.log("VoxelRegistry", "No identifier for " + handle.name());
                continue;
            }

            JsonValue json = new JsonReader().parse(handle);

            // parsing material
            VoxelMaterial material = new VoxelMaterial();

            if (json.has("material")) {
                JsonValue matJson = json.get("material");

                if (matJson.has("side")) {
                    int[] values = matJson.get("side").asIntArray();
                    Vector2 vec = new Vector2(values[0], values[1]);
                    material.setBack(vec);
                    material.setFront(vec);
                    material.setLeft(vec);
                    material.setRight(vec);
                    material.setTop(vec);
                    material.setBottom(vec);
                }

                if (matJson.has("top")) {
                    int[] values = matJson.get("top").asIntArray();
                    Vector2 vec = new Vector2(values[0], values[1]);
                    material.setTop(vec);
                    material.setBottom(vec);
                }

                if (matJson.has("bottom")) {
                    int[] values = matJson.get("bottom").asIntArray();
                    Vector2 vec = new Vector2(values[0], values[1]);
                    material.setBottom(vec);
                }

                if (matJson.has("front")) {
                    int[] values = matJson.get("front").asIntArray();
                    Vector2 vec = new Vector2(values[0], values[1]);
                    material.setFront(vec);
                }

                if (matJson.has("back")) {
                    int[] values = matJson.get("back").asIntArray();
                    Vector2 vec = new Vector2(values[0], values[1]);
                    material.setBack(vec);
                }

                if (matJson.has("left")) {
                    int[] values = matJson.get("left").asIntArray();
                    Vector2 vec = new Vector2(values[0], values[1]);
                    material.setLeft(vec);
                }

                if (matJson.has("right")) {
                    int[] values = matJson.get("right").asIntArray();
                    Vector2 vec = new Vector2(values[0], values[1]);
                    material.setRight(vec);
                }

                if (matJson.has("type")) {
                    material.setType(VoxelMaterialType.valueOf(matJson.getString("type").toUpperCase()));
                }
            }

            if (json.has("state_save")) {
                material.setStateSave(json.getBoolean("state_save"));
            }

            // parsing voxel type
            String voxelType = "";

            if (json.has("behaviour")) {
                voxelType = json.getString("behaviour");
            }

            Voxel voxel = switch (voxelType) {
                case "workbench" -> new WorkbenchVoxel(id, material);
                case "furnace" -> new FurnaceVoxel(id, material);
                case "chest" -> new ChestVoxel(id, material);
                case null, default -> new Voxel(id, material);
            };

            if (json.has("dropId")) {
                Identifier dropId = VoxelphaliaGame.getInstance()
                    .getIdentifierRegistry()
                    .getEntry(Identifier.of(json.getString("dropId")));

                if (dropId != null) {
                    voxel.setDropId(dropId);

                    if (json.has("dropAmount")) {
                        voxel.setDropAmount(json.getByte("dropAmount"));
                    }
                }
            }

            addEntry(voxel);
        }

        Gdx.app.log("VoxelRegistry", "Loaded " + entries.size() + " voxels!");
    }
}
