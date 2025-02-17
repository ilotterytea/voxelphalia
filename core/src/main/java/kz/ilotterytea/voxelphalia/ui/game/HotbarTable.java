package kz.ilotterytea.voxelphalia.ui.game;

import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.DragAndDrop;
import com.badlogic.gdx.utils.Align;
import kz.ilotterytea.voxelphalia.entities.PlayerEntity;
import kz.ilotterytea.voxelphalia.ui.game.inventory.InventoryHotbarTable;

public class HotbarTable extends Table {
    public HotbarTable(Skin skin, DragAndDrop dragAndDrop, PlayerEntity player) {
        super();
        setFillParent(true);
        align(Align.bottom);

        Table wrapper = new Table();
        wrapper.align(Align.center);
        add(wrapper).growX();

        Table bar = new Table();
        wrapper.add(bar);

        HealthHotbarTable health = new HealthHotbarTable(skin, player);
        bar.add(health).growX().padBottom(8f).row();

        InventoryHotbarTable inventory = new InventoryHotbarTable(skin, dragAndDrop, player.getInventory());
        bar.add(inventory).growX().size(496f, 64f).padBottom(8f).row();
    }
}
