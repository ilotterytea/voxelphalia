package kz.ilotterytea.voxelphalia.ui.game.inventory;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Stack;
import com.badlogic.gdx.scenes.scene2d.utils.DragAndDrop;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Align;
import kz.ilotterytea.voxelphalia.VoxelphaliaGame;
import kz.ilotterytea.voxelphalia.inventory.Inventory;
import kz.ilotterytea.voxelphalia.utils.Identifier;
import kz.ilotterytea.voxelphalia.utils.tuples.Triple;

public class InventorySlotStack extends Stack {
    private final Skin skin;
    private final Inventory.Slot slot;
    private final Label amountLabel;
    private final Image icon, activeImage;
    private Identifier slotId;

    public InventorySlotStack(Skin skin, DragAndDrop dragAndDrop, Inventory inventory, Inventory.Slot slot) {
        super();
        this.slot = slot;
        this.slotId = null;
        this.skin = skin;

        activeImage = new Image(skin.getDrawable("slot-inactive"));
        add(activeImage);

        setSize(48f, 48f);

        icon = new Image();
        add(icon);

        amountLabel = new Label(String.valueOf(slot.quantity), skin);
        amountLabel.setFillParent(true);
        amountLabel.setAlignment(Align.bottomRight);
        add(amountLabel);

        if (dragAndDrop != null) {
            dragAndDrop.addSource(new DragAndDrop.Source(this) {
                private float ox, oy;

                @Override
                public DragAndDrop.Payload dragStart(InputEvent event, float x, float y, int pointer) {
                    InventorySlotStack stack = (InventorySlotStack) getActor();

                    if (stack.getSlot().id == null || stack.getSlot().quantity == 0) {
                        return null;
                    }

                    DragAndDrop.Payload payload = new DragAndDrop.Payload();

                    ox = getActor().getX();
                    oy = getActor().getY();

                    payload.setObject(new Triple<>(stack.getSlot(), ox, oy));
                    payload.setDragActor(getActor());

                    return payload;
                }

                @Override
                public void dragStop(InputEvent event, float x, float y, int pointer, DragAndDrop.Payload payload, DragAndDrop.Target target) {
                    if (target == null) {
                        getActor().setPosition(ox, oy);
                    }
                }

                @Override
                public void drag(InputEvent event, float x, float y, int pointer) {
                    super.drag(event, x, y, pointer);
                    getActor().setPosition(Gdx.input.getX(), Gdx.input.getY());
                }
            });

            dragAndDrop.addTarget(new DragAndDrop.Target(this) {
                @Override
                public boolean drag(DragAndDrop.Source source, DragAndDrop.Payload payload, float x, float y, int pointer) {
                    return getActor() != source.getActor();
                }

                @Override
                public void drop(DragAndDrop.Source source, DragAndDrop.Payload payload, float x, float y, int pointer) {
                    InventorySlotStack myStack = (InventorySlotStack) getActor();

                    Inventory.Slot slot = myStack.getSlot();
                    Triple<Inventory.Slot, Float, Float> load = (Triple<Inventory.Slot, Float, Float>) payload.getObject();

                    if (slot.id == load.first.id) {
                        load.first.quantity = slot.add(load.first.quantity);
                        if (load.first.quantity <= 0) {
                            load.first.id = null;
                            load.first.quantity = 0;
                        }
                    } else {
                        Inventory.Slot tempSlot = new Inventory.Slot(slot.id, slot.quantity, slot.size);

                        slot.id = load.first.id;
                        slot.quantity = load.first.quantity;
                        slot.size = load.first.size;

                        load.first.id = tempSlot.id;
                        load.first.quantity = tempSlot.quantity;
                        load.first.size = tempSlot.size;
                    }

                    source.getActor().setPosition(load.second, load.third);
                }
            });
        }
    }

    @Override
    public void act(float delta) {
        super.act(delta);

        boolean visible = slot.id != null;

        icon.setVisible(visible);

        if (visible) {
            amountLabel.setText(String.valueOf(slot.quantity));

            if (slotId != slot.id && slot.id != null) {
                VoxelphaliaGame game = VoxelphaliaGame.getInstance();

                TextureAtlas atlas = VoxelphaliaGame.getInstance()
                    .getAssetManager()
                    .get("textures/gui/gui_voxels.atlas");
                TextureRegion region;

                if (game.getItemRegistry().containsEntry(slot.id)) {
                    region = game.getItemRegistry().getEntry(slot.id)
                        .getMaterial()
                        .getTextureRegion(game.getAssetManager().get("textures/items.png", Texture.class));
                } else {
                    region = atlas.findRegion(slot.id.getName());
                }

                if (region == null) {
                    region = atlas.findRegion(VoxelphaliaGame.getInstance().getIdentifierRegistry().getEntry("missing_voxel").getName());
                }

                icon.setDrawable(new TextureRegionDrawable(region));

                slotId = slot.id;
            }
        } else {
            amountLabel.setText("");
        }
    }

    public void setActive(boolean active) {
        activeImage.setDrawable(skin.getDrawable(active ? "slot-active" : "slot-inactive"));
    }

    public Inventory.Slot getSlot() {
        return slot;
    }
}
