package kz.ilotterytea.voxelphalia.utils.registries;

import kz.ilotterytea.voxelphalia.utils.Identifiable;
import kz.ilotterytea.voxelphalia.utils.Identifier;

import java.util.ArrayList;
import java.util.Optional;

public abstract class Registry<T extends Identifiable> {
    protected final ArrayList<T> entries;

    public Registry() {
        this.entries = new ArrayList<>();
    }

    public T addEntry(T entry) {
        Optional<T> i = this.entries.stream().filter((x) -> x.equals(entry)).findFirst();

        if (i.isPresent()) {
            return i.get();
        } else {
            this.entries.add(entry);
            return entry;
        }
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
        return getEntry(Identifier.of(identifier));
    }

    public boolean containsEntry(String id) {
        return containsEntry(Identifier.of(id));
    }

    public boolean containsEntry(Identifier id) {
        for (T entry : entries) {
            if (entry.getId().equals(id)) {
                return true;
            }
        }
        return false;
    }

    public abstract void load();
}
