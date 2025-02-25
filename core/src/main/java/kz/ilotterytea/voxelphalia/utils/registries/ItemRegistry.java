package kz.ilotterytea.voxelphalia.utils.registries;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.JsonReader;
import com.badlogic.gdx.utils.JsonValue;
import kz.ilotterytea.voxelphalia.VoxelphaliaGame;
import kz.ilotterytea.voxelphalia.items.Item;
import kz.ilotterytea.voxelphalia.items.ItemMaterial;
import kz.ilotterytea.voxelphalia.items.Weapon;
import kz.ilotterytea.voxelphalia.utils.Identifier;

public class ItemRegistry extends Registry<Item> {
    @Override
    public void load() {
        FileHandle folder = Gdx.files.internal("data/items");

        for (FileHandle handle : folder.list()) {
            Identifier id = VoxelphaliaGame.getInstance()
                .getIdentifierRegistry()
                .getEntry(new Identifier(handle.nameWithoutExtension()));

            if (id == null) {
                Gdx.app.log("ItemRegistry", "No identifier for " + handle.name());
                continue;
            }

            JsonValue json = new JsonReader().parse(handle);
            ItemMaterial material = new ItemMaterial();

            if (json.has("material")) {
                JsonValue matJson = json.get("material");

                if (matJson.has("region")) {
                    int[] values = matJson.get("region").asIntArray();
                    material.setRegion(new Vector2(values[0], values[1]));
                }
            }

            Item item;

            if (json.has("behaviour")) {
                JsonValue behJson = json.get("behaviour");
                switch (behJson.getString("name").toLowerCase()) {
                    case "weapon" -> {
                        float recoilTime = behJson.getFloat("recoilTime");
                        float energyCost = behJson.getFloat("energyCost");
                        int damage = behJson.getInt("damage");
                        int bulletMinAmount = behJson.getInt("bulletMinAmount", 1);
                        float bulletSpread = behJson.getFloat("bulletSpread", 0f);
                        boolean spawnBullet = behJson.getBoolean("spawnBullet");
                        item = new Weapon(id, material, recoilTime, energyCost, damage, bulletMinAmount, bulletSpread, spawnBullet);
                    }
                    default -> item = new Item(id, material);
                }
            } else {
                item = new Item(id, material);
            }

            addEntry(item);
        }

        Gdx.app.log("ItemRegistry", "Loaded " + entries.size() + " items!");
    }
}
