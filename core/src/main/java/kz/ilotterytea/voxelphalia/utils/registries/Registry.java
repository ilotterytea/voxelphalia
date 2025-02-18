package kz.ilotterytea.voxelphalia.utils.registries;

import com.badlogic.gdx.utils.Array;

public abstract class Registry<T> {
    protected final Array<T> entries;

    public Registry() {
        this.entries = new Array<>();
        load();
    }

    public void addEntry(T entry) {
        this.entries.add(entry);
    }

    public Array<T> getEntries() {
        return entries;
    }

    protected abstract void load();
}
