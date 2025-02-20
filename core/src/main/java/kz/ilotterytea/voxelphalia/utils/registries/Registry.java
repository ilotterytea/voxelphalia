package kz.ilotterytea.voxelphalia.utils.registries;

import kz.ilotterytea.voxelphalia.utils.Identifiable;
import kz.ilotterytea.voxelphalia.utils.Identifier;

import java.util.ArrayList;

public abstract class Registry<T extends Identifiable> {
    protected final ArrayList<T> entries;

    public Registry() {
        this.entries = new ArrayList<>();
    }

    public void addEntry(T entry) {
        this.entries.add(entry);
    }

    public ArrayList<T> getEntries() {
        return entries;
    }

    public T getEntry(Identifier identifier) {
        for (T entry : entries) {
            if (entry.getId().equals(identifier)) {
                return entry;
            }
        }
        return null;
    }

    public T getEntry(String identifier) {
        for (T entry : entries) {
            if (entry.getId().equals(new Identifier(identifier))) {
                return entry;
            }
        }
        return null;
    }

    public abstract void load();
}
