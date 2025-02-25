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

    // im too lazy to make it as enum.
    // 0 - hands, 1 - pistol, 2 - smg, 3 - shotgun
    private int currentItem;

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

        addAction(Actions.moveTo(0f, -50f));
    }

    private void showGun(String regionName) {
        clear();
        layout();

        Stack stack = new Stack();

        // --- shotgun ---
        Table gunTable = new Table();
        stack.add(gunTable);

        Image gun = new Image(handAtlas.findRegion(regionName));

        float w = gun.getWidth() * 3f, h = gun.getHeight() * 3f;

        gunTable.add(gun).size(w, h);

        add(stack).size(w, h);
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
        Inventory.Slot slot = inventory.getCurrentSlot();

        if (slot.id == null) {
            if (currentItem != 0) {
                currentItem = 0;
                showHoldingHands();
            }

            item.setVisible(false);
            return;
        }

        boolean holdingShotgun = slot.id.equals("voxelphalia:shotgun");
        boolean holdingPistol = slot.id.equals("voxelphalia:pistol");
        boolean holdingSMG = slot.id.equals("voxelphalia:smg");

        // changing item
        if (holdingShotgun && currentItem != 3) {
            currentItem = 3;
            showGun("shotgun");
        } else if (holdingSMG && currentItem != 2) {
            currentItem = 2;
            showGun("smg");
        } else if (holdingPistol && currentItem != 1) {
            currentItem = 1;
            showGun("pistol");
        } else if (!holdingShotgun && !holdingPistol && !holdingSMG && currentItem != 0) {
            currentItem = 0;
            showHoldingHands();
            System.out.println("aight");
        }

        if (currentItem == 0) {
            item.setVisible(true);

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
        }
    }
}
