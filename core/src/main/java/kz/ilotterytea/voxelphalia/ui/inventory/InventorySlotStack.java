package kz.ilotterytea.voxelphalia.ui.inventory;

import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Stack;
import com.badlogic.gdx.utils.Align;
import kz.ilotterytea.voxelphalia.VoxelphaliaGame;
import kz.ilotterytea.voxelphalia.inventory.Inventory;

public class InventorySlotStack extends Stack {
    private final Inventory.Slot slot;
    private final Label amountLabel;
    private final Image icon, activeImage;

    public InventorySlotStack(Skin skin, Inventory.Slot slot) {
        super();
        this.slot = slot;

        TextureAtlas atlas = VoxelphaliaGame.getInstance()
            .getAssetManager()
            .get("textures/gui/gui_voxels.atlas");

        activeImage = new Image(skin.getDrawable("hotbar-foreground"));
        activeImage.setVisible(false);
        add(activeImage);

        icon = new Image(atlas.findRegion(String.valueOf(slot.id)));
        add(icon);

        amountLabel = new Label(String.valueOf(slot.quantity), skin);
        amountLabel.setFillParent(true);
        amountLabel.setAlignment(Align.bottomRight);
        add(amountLabel);
    }

    @Override
    public void act(float delta) {
        super.act(delta);

        boolean visible = slot.id != 0;

        amountLabel.setVisible(visible);
        icon.setVisible(visible);

        if (visible) {
            amountLabel.setText(String.valueOf(slot.quantity));
        }
    }

    public void setActive(boolean active) {
        activeImage.setVisible(active);
    }
}
