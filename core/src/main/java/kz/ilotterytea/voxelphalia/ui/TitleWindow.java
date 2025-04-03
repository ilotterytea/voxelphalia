package kz.ilotterytea.voxelphalia.ui;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.Window;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Array;
import kz.ilotterytea.voxelphalia.ui.sound.SoundingImageButton;

public class TitleWindow extends Window {
    private final Array<CloseableListener> closeableListeners;

    public TitleWindow(Skin skin, String title, Actor contentBody) {
        super("", skin);
        this.closeableListeners = new Array<>();

        pad(16f);

        // --- WINDOW HEADER ---
        Table windowHeader = new Table();
        add(windowHeader).growX().padBottom(15f).row();

        // Window title
        Table titleTable = new Table();
        titleTable.align(Align.left);
        Label titleLabel = new Label(title, skin);
        titleTable.add(titleLabel);
        windowHeader.add(titleTable).growX();

        // Close button
        SoundingImageButton closeButton = new SoundingImageButton(skin, "close");
        closeButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                super.clicked(event, x, y);
                remove();
                for (CloseableListener listener : closeableListeners) {
                    listener.onClose();
                }
            }
        });
        windowHeader.add(closeButton);

        // --- WINDOW BODY ---
        Table windowBody = new Table();
        windowBody.align(Align.top);
        add(windowBody).grow();

        windowBody.add(contentBody);
    }

    public void onClose(CloseableListener listener) {
        this.closeableListeners.add(listener);
    }
}
