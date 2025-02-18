package kz.ilotterytea.voxelphalia.ui;

import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.utils.Align;

public class IconButton extends Button {
    public IconButton(String text, Image icon, Skin skin) {
        super(skin);
        pad(8f);

        add(icon).size(32f, 32f).padRight(4f);

        Label label = new Label(text, skin);
        label.setAlignment(Align.left);
        add(label).grow();
    }
}
