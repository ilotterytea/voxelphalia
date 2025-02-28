package kz.ilotterytea.voxelphalia.ui.sound;

import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.CheckBox;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import kz.ilotterytea.voxelphalia.VoxelphaliaGame;
import kz.ilotterytea.voxelphalia.audio.IdentifiedSound;
import kz.ilotterytea.voxelphalia.utils.registries.SoundRegistry;

public class SoundingCheckbox extends CheckBox {
    public SoundingCheckbox(String text, Skin skin) {
        this(text, skin, "sfx.ui.hover", "sfx.ui.click");
    }

    public SoundingCheckbox(String text, Skin skin, String hoverSoundId, String clickSoundId) {
        super(text, skin);

        SoundRegistry soundRegistry = VoxelphaliaGame.getInstance().getSoundRegistry();
        Preferences preferences = VoxelphaliaGame.getInstance().getPreferences();

        addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                super.clicked(event, x, y);
                if (clickSoundId == null || isDisabled()) return;

                IdentifiedSound sound = soundRegistry.getEntry(clickSoundId);

                if (sound != null) {
                    sound.getSound().play(preferences.getFloat("sfx", 1f));
                }
            }

            @Override
            public void enter(InputEvent event, float x, float y, int pointer, Actor fromActor) {
                super.enter(event, x, y, pointer, fromActor);
                if (hoverSoundId == null || isDisabled()) return;

                IdentifiedSound sound = soundRegistry.getEntry(hoverSoundId);

                if (sound != null) {
                    sound.getSound().play(preferences.getFloat("sfx", 1f));
                }
            }
        });
    }
}
