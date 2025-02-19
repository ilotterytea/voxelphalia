package kz.ilotterytea.voxelphalia.ui.game;

import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.Align;
import kz.ilotterytea.voxelphalia.entities.PlayerEntity;
import kz.ilotterytea.voxelphalia.ui.game.inventory.InventoryHotbarTable;

public class HotbarTable extends Table {
    public HotbarTable(Skin skin, PlayerEntity player) {
        super();
        setFillParent(true);
        align(Align.bottom);

        Table wrapper = new Table();
        wrapper.align(Align.center);
        add(wrapper).growX();

        Table bar = new Table();
        bar.pad(8f);
        wrapper.add(bar).grow();

        HealthHotbarTable health = new HealthHotbarTable(skin, player);
        bar.add(health).expandX().left();

        InventoryHotbarTable inventory = new InventoryHotbarTable(skin, player.getInventory());
        bar.add(inventory).expandY().align(Align.bottomRight);
    }
}
