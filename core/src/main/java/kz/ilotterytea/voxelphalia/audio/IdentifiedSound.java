package kz.ilotterytea.voxelphalia.audio;

import com.badlogic.gdx.audio.Sound;
import kz.ilotterytea.voxelphalia.utils.Identifiable;
import kz.ilotterytea.voxelphalia.utils.Identifier;

public class IdentifiedSound implements Identifiable {
    private final Identifier id;
    private final Sound sound;

    public IdentifiedSound(Identifier id, Sound sound) {
        this.id = id;
        this.sound = sound;
    }

    public Sound getSound() {
        return sound;
    }

    @Override
    public Identifier getId() {
        return id;
    }
}
