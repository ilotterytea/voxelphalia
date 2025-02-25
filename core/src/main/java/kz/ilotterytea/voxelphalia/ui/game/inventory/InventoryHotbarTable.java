package kz.ilotterytea.voxelphalia.ui.game.inventory;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.utils.Align;
import kz.ilotterytea.voxelphalia.inventory.Inventory;
import kz.ilotterytea.voxelphalia.utils.tuples.Pair;

public class InventoryHotbarTable extends Table {
    private final Inventory inventory;
    private final Pair<InventorySlotStack, Label>[] slots;
    private final ScrollPane pane;

    public InventoryHotbarTable(Skin skin, Inventory inventory) {
        super(skin);
        setBackground("inventorybar-background");
        this.inventory = inventory;
        this.slots = new Pair[inventory.getSize()];

        float size = 48f;

        Table table = new Table(skin);
        table.pad(8f);
        table.align(Align.center);
        pane = new ScrollPane(table, skin);
        pane.setFadeScrollBars(true);
        pane.setScrollbarsVisible(false);
        pane.setScrollingDisabled(false, true);
        add(pane).grow();

        for (int i = 0; i < this.slots.length; i++) {
            InventorySlotStack stack = new InventorySlotStack(skin, null, inventory, inventory.getSlot(i));

            Table slot = new Table();
            slot.add(stack).size(size, size).row();

            Label label = new Label(String.valueOf(i + 1), skin, "tiny-default");
            label.setAlignment(Align.center);
            slot.add(label).growX().row();

            Cell<Table> s = table.add(slot);

            if (i < this.slots.length - 1) {
                s.padRight(4f);
            }

            this.slots[i] = new Pair<>(stack, label);
        }

        pane.scrollTo(0f, 0f, pane.getWidth(), pane.getHeight());
    }

    @Override
    public void act(float delta) {
        super.act(delta);

        for (int i = 0; i < slots.length; i++) {
            Pair<InventorySlotStack, Label> stack = slots[i];
            stack.first.setActive(i == inventory.getSlotIndex());

            if (i == inventory.getSlotIndex()) {
                stack.first.setActive(true);
                pane.scrollTo(i * 48f - pane.getWidth() / 2f, 0f, pane.getWidth(), pane.getHeight());
                stack.second.setColor(Color.SALMON);
            } else {
                stack.first.setActive(false);
                stack.second.setColor(Color.GRAY);
            }
        }
    }
}
