package kz.ilotterytea.voxelphalia.utils.registries;

import java.util.ArrayList;

public abstract class Registry<T> {
    protected final ArrayList<T> entries;

    public Registry() {
        this.entries = new ArrayList<>();
        load();
    }

    public void addEntry(T entry) {
        this.entries.add(entry);
    }

    public ArrayList<T> getEntries() {
        return entries;
    }

    public abstract T getEntryById(byte id);

    protected abstract void load();
}
