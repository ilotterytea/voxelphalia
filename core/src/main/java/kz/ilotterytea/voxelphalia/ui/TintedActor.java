package kz.ilotterytea.voxelphalia.ui;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Stack;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.Align;

public class TintedActor extends Stack {
    public TintedActor(Skin skin, Actor actor) {
        super();
        setFillParent(true);

        Image tint = new Image(skin.getDrawable("black-transparent"));
        tint.setFillParent(true);
        add(tint);

        Table contentTable = new Table();
        contentTable.align(Align.center);
        contentTable.setFillParent(true);
        add(contentTable);

        contentTable.add(actor);
    }
}
