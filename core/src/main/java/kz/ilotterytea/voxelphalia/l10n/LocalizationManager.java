package kz.ilotterytea.voxelphalia.l10n;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.Array;

public class LocalizationManager {
    private final Array<Localization> localizations;
    private int index;

    public LocalizationManager() {
        this.localizations = new Array<>();

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

    public void setLocaleIndex(int index) {
        if (index + 1 > localizations.size)
            throw new IllegalArgumentException("Localization index: " + index + " > " + (localizations.size - 1));

        this.index = index;
    }

    public int getLocaleIndex() {
        return index;
    }

    public Array<String> getNames() {
        Array<String> names = new Array<>();
        this.localizations.forEach(l -> names.add(l.getName()));
        return names;
    }
}
