package kz.ilotterytea.voxelphalia.l10n;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;

import java.util.ArrayList;
import java.util.List;

public class LocalizationManager {
    private final ArrayList<Localization> localizations;
    private int index;

    public LocalizationManager() {
        this.localizations = new ArrayList<>();

        FileHandle assetsFile = Gdx.files.internal("assets.txt");

        for (String path : assetsFile.readString().split("\n")) {
            if (!path.startsWith("l10n/")) continue;
            this.localizations.add(new Localization(Gdx.files.internal(path)));
        }
    }

    public String getLine(LineId id) {
        return this.localizations.get(index).getLine(id);
    }

    public String getLine(LineId id, Object... arguments) {
        return this.localizations.get(index).getLine(id, arguments);
    }

    public List<String> getNames() {
        return this.localizations.stream()
            .map(Localization::getName)
            .toList();
    }
}
