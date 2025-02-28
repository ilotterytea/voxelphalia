package kz.ilotterytea.voxelphalia.ui.game.inventory;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.Window;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.DragAndDrop;
import kz.ilotterytea.voxelphalia.VoxelphaliaGame;
import kz.ilotterytea.voxelphalia.entities.PlayerEntity;
import kz.ilotterytea.voxelphalia.inventory.Inventory;
import kz.ilotterytea.voxelphalia.l10n.LineId;
import kz.ilotterytea.voxelphalia.ui.sound.SoundingImageButton;

public class InventoryWindow extends Window {
    private final PlayerEntity playerEntity;

    public InventoryWindow(Skin skin, DragAndDrop dragAndDrop, PlayerEntity playerEntity) {
        super("", skin);
        setMovable(true);
        setPosition(200, 200);
        setSize(572f, 260f);
        setVisible(false);

        this.playerEntity = playerEntity;

        // HEADER
        Table header = new Table();
        add(header).growX().row();

        header.add(new Label(VoxelphaliaGame.getInstance().getLocalizationManager().getLine(LineId.INVENTORY_TITLE), skin)).growX();

        // close button
        SoundingImageButton closeButton = new SoundingImageButton(skin, "close");
        closeButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                super.clicked(event, x, y);
                setVisible(false);
            }
        });
        header.add(closeButton);

        // inventory
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
        }
    }

    @Override
    public void setVisible(boolean visible) {
        super.setVisible(visible);
        if (playerEntity != null) {
            playerEntity.setFocused(!visible);
        }
    }
}
