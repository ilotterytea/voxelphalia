package kz.ilotterytea.voxelphalia.ui.game.inventory;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.Window;
import com.badlogic.gdx.scenes.scene2d.utils.DragAndDrop;
import kz.ilotterytea.voxelphalia.entities.PlayerEntity;
import kz.ilotterytea.voxelphalia.inventory.Inventory;

public class InventoryWindow extends Window {
    private final PlayerEntity playerEntity;

    public InventoryWindow(Skin skin, DragAndDrop dragAndDrop, PlayerEntity playerEntity) {
        super("inventory", skin);
        setMovable(true);
        setPosition(200, 200);
        setSize(572f, 260f);
        setVisible(false);

        this.playerEntity = playerEntity;

        Inventory inventory = playerEntity.getInventory();

        Table inventoryGrid = new Table();
        inventoryGrid.setFillParent(true);
        add(inventoryGrid).grow();

        for (int i = 10; i < inventory.getSlots().length; i++) {
            Inventory.Slot slot = inventory.getSlot(i);
            InventorySlotStack stack = new InventorySlotStack(skin, dragAndDrop, inventory, slot);

            inventoryGrid.add(stack).pad(4f).size(48f, 48f);

            if (i % 10 == 9) {
                inventoryGrid.row();
            }
        }
    }

    @Override
    public void act(float delta) {
        super.act(delta);

        if (Gdx.input.isKeyJustPressed(Input.Keys.E)) {
            setVisible(!isVisible());
            playerEntity.setFocused(!isVisible());
        }
    }
}
