package kz.ilotterytea.voxelphalia.ui.game;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Stack;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Align;
import kz.ilotterytea.voxelphalia.VoxelphaliaGame;
import kz.ilotterytea.voxelphalia.entities.PlayerEntity;
import kz.ilotterytea.voxelphalia.inventory.Inventory;

public class PlayerHandsTable extends Table {
    private final VoxelphaliaGame game;
    private final PlayerEntity playerEntity;

    private final TextureAtlas handAtlas, voxelAtlas;
    private final Texture itemTexture;

    private Image item;

    public PlayerHandsTable(PlayerEntity playerEntity) {
        super();
        this.playerEntity = playerEntity;
        this.game = VoxelphaliaGame.getInstance();

        handAtlas = game.getAssetManager()
            .get("textures/player/player_hand.atlas");
        voxelAtlas = game.getAssetManager()
            .get("textures/gui/gui_voxels.atlas");
        itemTexture = game.getAssetManager()
            .get("textures/items.png");

        setFillParent(true);
        align(Align.bottom);
        setZIndex(0);

        showHoldingHands();
    }

    private void showHoldingHands() {
        clear();
        layout();

        Stack stack = new Stack();

        // ---hands---
        Table handTable = new Table();
        stack.add(handTable);

        // left hand
        Image leftHand = new Image(handAtlas.findRegion("hand"));

        float w = leftHand.getWidth() * 3f, h = leftHand.getHeight() * 3f;

        handTable.add(leftHand).size(w, h).padRight(w);

        // right hand
        Image rightHand = new Image(handAtlas.findRegion("hand"));
        rightHand.setScale(-1f, 1f);
        handTable.add(rightHand).size(w, h).padRight(-w);

        // --- item ---
        Table itemTable = new Table();
        stack.add(itemTable);

        item = new Image(voxelAtlas.findRegion("grass_voxel"));

        itemTable.add(item).size(item.getWidth() * 8f, item.getHeight() * 8f).padBottom(item.getHeight() / 2f);

        // --- fingers ---
        Table fingersTable = new Table();
        stack.add(fingersTable);

        // left finger
        Image leftFinger = new Image(handAtlas.findRegion("finger"));

        fingersTable.add(leftFinger).size(w, h).padRight(w);

        // right finger
        Image rightFinger = new Image(handAtlas.findRegion("finger"));
        rightFinger.setScale(-1f, 1f);
        fingersTable.add(rightFinger).size(w, h).padRight(-w);

        add(stack).size(w, h);

        addAction(Actions.moveTo(0f, -100f));
    }

    @Override
    public void act(float delta) {
        super.act(delta);

        float y = 24f * playerEntity.getBobbing();

        addAction(Actions.sequence(
            Actions.moveBy(0f, y, 0.1f),
            Actions.moveBy(0f, -y, 0.1f)
        ));

        Inventory inventory = playerEntity.getInventory();

        if (inventory.getCurrentSlot().id != null) {
            item.setVisible(true);
            Inventory.Slot slot = inventory.getCurrentSlot();

            TextureRegion region;

            if (game.getItemRegistry().containsEntry(slot.id)) {
                region = game.getItemRegistry().getEntry(slot.id)
                    .getMaterial()
                    .getTextureRegion(itemTexture);
            } else {
                region = voxelAtlas.findRegion(slot.id.getName());
            }

            if (region == null) {
                region = voxelAtlas.findRegion(game.getIdentifierRegistry().getEntry("missing_voxel").getName());
            }

            item.setDrawable(new TextureRegionDrawable(region));
        } else {
            item.setVisible(false);
        }
    }
}
