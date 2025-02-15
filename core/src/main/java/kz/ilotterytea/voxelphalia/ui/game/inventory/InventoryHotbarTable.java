package kz.ilotterytea.voxelphalia.ui.game.inventory;

import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.Align;
import kz.ilotterytea.voxelphalia.inventory.Inventory;

public class InventoryHotbarTable extends Table {
    private final Inventory inventory;
    private final InventorySlotStack[] slots;

    public InventoryHotbarTable(Skin skin, Inventory inventory) {
        super();
        setFillParent(true);
        align(Align.bottom);
        pad(8f);

        this.inventory = inventory;
        this.slots = new InventorySlotStack[9];

        float size = 48f;

        Table bar = new Table(skin);
        bar.setBackground("hotbar-background");
        add(bar).height(64f);

        for (int i = 0; i < this.slots.length; i++) {
            InventorySlotStack stack = new InventorySlotStack(skin, inventory.getSlot(i));
            bar.add(stack).size(size, size);
            this.slots[i] = stack;
        }
    }

    @Override
    public void act(float delta) {
        super.act(delta);

        for (int i = 0; i < slots.length; i++) {
            InventorySlotStack stack = slots[i];
            stack.setActive(i == inventory.getSlotIndex());
        }
    }
}
