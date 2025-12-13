package kz.ilotterytea.voxelphalia.utils.registries;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import kz.ilotterytea.voxelphalia.utils.Identifier;

public class IdentifierRegistry extends Registry<Identifier> {
    @Override
    public void load() {
        FileHandle assetsFile = Gdx.files.internal("assets.txt");

        for (String path : assetsFile.readString().split("\n")) {
            if (!path.startsWith("data/")) continue;
            FileHandle handle = Gdx.files.internal(path);
            Identifier id = new Identifier(handle.nameWithoutExtension());

            if (!entries.contains(id)) {
                addEntry(id);
            }
        }

        Gdx.app.log("IdentifierRegistry", "Loaded " + entries.size() + " identifiers!");
    }
}
