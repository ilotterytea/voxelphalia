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

        FileHandle folder = Gdx.files.internal("l10n");

        for (FileHandle handle : folder.list()) {
            this.localizations.add(new Localization(handle));
        }
    }

    public String getLine(LineId id) {
        return this.localizations.get(index).getLine(id);
    }

    public List<String> getNames() {
        return this.localizations.stream()
            .map(Localization::getName)
            .toList();
    }
}
