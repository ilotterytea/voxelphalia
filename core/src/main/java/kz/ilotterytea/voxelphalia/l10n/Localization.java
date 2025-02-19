package kz.ilotterytea.voxelphalia.l10n;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.JsonReader;
import com.badlogic.gdx.utils.JsonValue;

import java.util.HashMap;

public class Localization {
    private final HashMap<LineId, String> lines;
    private final String name;

    public Localization(FileHandle handle) {
        this.name = handle.nameWithoutExtension();
        this.lines = new HashMap<>();

        JsonValue json = new JsonReader().parse(handle.readString());

        for (LineId id : LineId.values()) {
            String value;
            if (json.has(id.toString())) {
                value = json.getString(id.toString());
            } else {
                value = "missingno";
            }
            this.lines.put(id, value);
        }

        Gdx.app.log("Localization:" + this.name, "Loaded " + this.lines.size() + " lines!");
    }

    public String getLine(LineId id) {
        return this.lines.get(id);
    }

    public String getLine(LineId id, Object... arguments) {
        String line = this.lines.get(id);
        int index = 0;

        while (arguments.length > index) {
            line = line.replaceFirst("%s", arguments[index].toString());
            index++;
        }

        return line;
    }

    public String getName() {
        return name;
    }
}
