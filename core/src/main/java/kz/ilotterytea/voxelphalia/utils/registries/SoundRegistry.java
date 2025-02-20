package kz.ilotterytea.voxelphalia.utils.registries;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.math.MathUtils;
import kz.ilotterytea.voxelphalia.VoxelphaliaGame;
import kz.ilotterytea.voxelphalia.audio.IdentifiedSound;
import kz.ilotterytea.voxelphalia.utils.Identifier;

import java.util.ArrayList;

public class SoundRegistry extends Registry<IdentifiedSound> {
    @Override
    public void load() {
        FileHandle folder = Gdx.files.internal("sfx");
        loadFolder(folder);
        Gdx.app.log("SoundRegistry", "Loaded " + entries.size() + " sounds!");
    }

    private void loadFolder(FileHandle folder) {
        AssetManager assetManager = VoxelphaliaGame.getInstance().getAssetManager();

        for (FileHandle file : folder.list()) {
            if (file.isDirectory()) {
                loadFolder(file);
                continue;
            }

            Sound sound = assetManager.get(file.path());
            String name = file.pathWithoutExtension().replace('/', '.').toLowerCase();
            name = name.substring(0, name.length() - 1);
            Identifier identifier = VoxelphaliaGame.getInstance()
                .getIdentifierRegistry()
                .addEntry(new Identifier(name));

            addEntry(new IdentifiedSound(identifier, sound));
        }
    }

    @Override
    public IdentifiedSound getEntry(String identifier) {
        return getEntry(Identifier.of(identifier));
    }

    @Override
    public IdentifiedSound getEntry(Identifier identifier) {
        ArrayList<IdentifiedSound> sounds = new ArrayList<>();
        for (IdentifiedSound entry : entries) {
            if (entry.getId().equals(identifier)) {
                sounds.add(entry);
            }
        }

        if (sounds.isEmpty()) return null;

        return sounds.get(MathUtils.random(0, sounds.size() - 1));
    }
}
