package kz.ilotterytea.voxelphalia.ui.game.inventory;

import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.DragAndDrop;
import kz.ilotterytea.voxelphalia.VoxelphaliaGame;
import kz.ilotterytea.voxelphalia.entities.PlayerEntity;
import kz.ilotterytea.voxelphalia.inventory.Inventory;
import kz.ilotterytea.voxelphalia.l10n.LineId;
import kz.ilotterytea.voxelphalia.voxels.specialvoxels.ChestVoxel;

public class ChestWindow extends Window {
    private final DragAndDrop dragAndDrop;
    private final Skin skin;
    private final PlayerEntity playerEntity;

    private ChestVoxel chest;
    private final Table slotTable;

    public ChestWindow(Skin skin, DragAndDrop dragAndDrop, PlayerEntity playerEntity) {
        super("", skin);
        setMovable(true);
        setPosition(200, 200);
        setSize(572f, 260f);
        setVisible(false);

        this.dragAndDrop = dragAndDrop;
        this.skin = skin;
        this.playerEntity = playerEntity;

        // HEADER
        Table header = new Table();
        add(header).growX().row();

        header.add(new Label(VoxelphaliaGame.getInstance().getLocalizationManager().getLine(LineId.CHEST_TITLE), skin)).growX();

        // close button
        ImageButton closeButton = new ImageButton(skin, "close");
        closeButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                super.clicked(event, x, y);
                setVisible(false, null);
            }
        });
        header.add(closeButton);

        slotTable = new Table();
        slotTable.setFillParent(true);
        add(slotTable).grow();

        setVisible(false, null);
    }

    public void setVisible(boolean visible, ChestVoxel chest) {
        super.setVisible(visible);

        this.chest = chest;
        buildLayout();

        if (playerEntity != null) {
            playerEntity.setFocused(!visible);
        }
    }

    private void buildLayout() {
        slotTable.clear();

        if (chest == null) return;

        for (int i = 0; i < chest.getInventory().getSize(); i++) {
            Inventory.Slot slot = chest.getInventory().getSlot(i);
            InventorySlotStack stack = new InventorySlotStack(skin, dragAndDrop, chest.getInventory(), slot);

            slotTable.add(stack).pad(4f).size(48f, 48f);

            if (i % 10 == 9) {
                slotTable.row();
            }
        }
    }
}
