package kz.ilotterytea.voxelphalia.items;

import kz.ilotterytea.voxelphalia.utils.Identifiable;
import kz.ilotterytea.voxelphalia.utils.Identifier;

public class Item implements Identifiable {
    private final Identifier identifier;
    private final ItemMaterial material;

    public Item(Identifier identifier, ItemMaterial material) {
        this.identifier = identifier;
        this.material = material;
    }

    public ItemMaterial getMaterial() {
        return material;
    }

    @Override
    public Identifier getId() {
        return identifier;
    }
}
