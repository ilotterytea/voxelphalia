package kz.ilotterytea.voxelphalia.utils.registries;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import kz.ilotterytea.voxelphalia.utils.Identifier;

public class IdentifierRegistry extends Registry<Identifier> {
    @Override
    public void load() {
        FileHandle folder = Gdx.files.internal("data");
        loadFolder(folder);
        Gdx.app.log("IdentifierRegistry", "Loaded " + entries.size() + " identifiers!");
    }

    private void loadFolder(FileHandle folder) {
        for (FileHandle file : folder.list()) {
            if (file.isDirectory()) {
                loadFolder(file);
                continue;
            }

            Identifier id = new Identifier(file.nameWithoutExtension());

            if (!entries.contains(id)) {
                addEntry(id);
            }
        }
    }
}
